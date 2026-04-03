package cooccon.spring.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import cooccon.spring.dto.UserScoreDTO;
import cooccon.spring.entity.Users;
import cooccon.spring.repository.UserRepository;

@Service
public class GameService {
	private final UserRepository userRepository;
	private final Random random = new Random();
	private final RedisTemplate<String, Object> redisTemplate;
	private static final String LEADERBOARD_KEY = "leaderboard:top10";

	public GameService(UserRepository userRepository, RedisTemplate<String, Object> redisTemplate) {
		super();
		this.userRepository = userRepository;
		this.redisTemplate = redisTemplate;
	}

	// Đảm bảo cùng một user không thể chạy song song logic đoán số.
	public Map guessNumber(Users user, int number) {
		synchronized (user.getId().toString().intern()) {
			Map result = new HashMap<>();

			if (user.getTurns() <= 0) {
				result.put("UserName:", user.getUsername());
				result.put("Message: ", "Hết lượt chơi!");
				return result;
			}

			user.setTurns(user.getTurns() - 1);
			result.put("Lượt chơi còn lại", user.getTurns());
			int serverNumber = random.nextInt(5) + 1;
			if (serverNumber == number) {
				user.setScore(user.getScore() + 1);
				userRepository.save(user);
				result.put("Số dự đoán", number);
				result.put("Kết quả", serverNumber);
				result.put("Điểm hiện tại", user.getScore());
				return result;
			} else {
				userRepository.save(user);
				result.put("Số dự đoán", number);
				result.put("Kết quả", serverNumber);
				result.put("Điểm hiện tại", user.getScore());
				return result;
			}

		}
	}

	public void buyTurns(Users user) {
		user.setTurns(user.getTurns() + 5);
		userRepository.save(user);
	}

	public List<UserScoreDTO> leaderboard() {
		// thử lấy từ cache
		List<UserScoreDTO> cached = (List<UserScoreDTO>) redisTemplate.opsForValue().get("leaderboard:top10");
		if (cached != null) {
			return cached;
		}

		// nếu chưa có cache thì query DB
		List<UserScoreDTO> top10 = userRepository.findTop10UsersByScore();
		// lưu vào cache
		redisTemplate.opsForValue().set(LEADERBOARD_KEY, top10, Duration.ofMinutes(1));
		return top10;
	}
}
