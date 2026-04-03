package cooccon.spring.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import cooccon.spring.dto.UserScoreDTO;
import cooccon.spring.entity.Users;
import cooccon.spring.repository.UserRepository;

/**
 * Test class cho GameService Kiểm tra logic của game: đoán số, mua lượt,
 * leaderboard
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GameService Tests")
class GameServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ValueOperations<String, Object> valueOperations;

	@InjectMocks
	private GameService gameService;

	private Users testUser;

	/**
	 * Thiết lập dữ liệu test trước mỗi test case
	 */
	@BeforeEach
	void setUp() {
		// Tạo user test
		testUser = new Users();
		testUser.setId(1L);
		testUser.setUsername("user_test");
		testUser.setPassword("123");
		testUser.setEmail("test@gmail.com");
		testUser.setScore(10);
		testUser.setTurns(5);
		testUser.setRole("ADMIN");

//         Cấu hình mock RedisTemplate
		lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
	}

	/**
	 * Test case: Kiểm tra khi người dùng không còn lượt chơi
	 */
	@Test
	@DisplayName("Test guessNumber - không còn lượt chơi")
	void testGuessNumber_NoTurnsLeft() {
		// Arrange: Người dùng không có lượt chơi
		testUser.setTurns(0);

		// Act: Gọi phương thức đoán số
		Map result = gameService.guessNumber(testUser, 3);

		// Assert: Kiểm tra thông báo lỗi
		assertNotNull(result);
		assertEquals("user_test", result.get("UserName:"));
		assertEquals("Hết lượt chơi!", result.get("Message: "));

		// Verify: Không lưu user vì không còn lượt
		verify(userRepository, never()).save(testUser);
	}

	/**
	 * Test case: Kiểm tra khi người dùng đoán đúng số
	 */
	@Test
	@DisplayName("Test guessNumber - đoán đúng số")
	void testGuessNumber_CorrectGuess() {
		// Arrange: Chuẩn bị user có lượt chơi
		testUser.setTurns(5);
		testUser.setScore(10);

		// Act: Gọi phương thức đoán số (giả sử đoán đúng)
		// Lưu ý: Hàm random nên sẽ khó kiểm tra chính xác
		Map result = gameService.guessNumber(testUser, 3);

		// Assert: Kiểm tra lượt giảm đi 1
		assertNotNull(result);
		assertEquals(4, result.get("Lượt chơi còn lại"));

		// Verify: Lưu user vào database
		verify(userRepository, times(1)).save(testUser);
	}

	/**
	 * Test case: Kiểm tra chức năng mua thêm lượt chơi
	 */
	@Test
	@DisplayName("Test buyTurns - mua thêm 5 lượt")
	void testBuyTurns() {
		// Arrange: Chuẩn bị user
		int initialTurns = testUser.getTurns();

		// Act: Mua thêm lượt
		gameService.buyTurns(testUser);

		// Assert: Kiểm tra lượt đã tăng 5
		assertEquals(initialTurns + 5, testUser.getTurns());

		// Verify: Lưu user vào database
		verify(userRepository, times(1)).save(testUser);
	}

	/**
	 * Test case: Kiểm tra leaderboard - lấy từ cache
	 */
	@Test
	@DisplayName("Test leaderboard - lấy từ Redis cache")
	void testLeaderboard_FromCache() {
		// Arrange: Chuẩn bị dữ liệu cache
		List<UserScoreDTO> cachedLeaderboard = List.of(new UserScoreDTO("user1", 100), new UserScoreDTO("user2", 90));

//        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

		// leaderboard:top10
		when(valueOperations.get("leaderboard:top10")).thenReturn(cachedLeaderboard);

		// Act: Lấy leaderboard
		List<UserScoreDTO> result = gameService.leaderboard();

		// Assert: Kiểm tra kết quả
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("user1", result.get(0).getUsername());

		// Verify: Không query database vì có cache
		verify(userRepository, never()).findTop10UsersByScore();
	}

	/**
	 * Test case: Kiểm tra leaderboard - lấy từ database khi cache trống
	 */
	@Test
	@DisplayName("Test leaderboard - lấy từ database khi cache trống")
	void testLeaderboard_FromDatabase() {
		// Arrange: Cache trống
		when(valueOperations.get("leaderboard:top10")).thenReturn(null);
		List<UserScoreDTO> dbLeaderboard = List.of(new UserScoreDTO("user1", 100), new UserScoreDTO("user2", 90));
		when(userRepository.findTop10UsersByScore()).thenReturn(dbLeaderboard);

		// Act: Lấy leaderboard
		List<UserScoreDTO> result = gameService.leaderboard();

		// Assert: Kiểm tra kết quả
		assertNotNull(result);
		assertEquals(2, result.size());

		// Verify: Query database
		verify(userRepository, times(1)).findTop10UsersByScore();
		// Verify: Lưu vào cache
		verify(valueOperations, times(1)).set(eq("leaderboard:top10"), eq(dbLeaderboard), any());
	}

	/**
	 * Test case: Kiểm tra thread safety - đoán số đồng thời
	 */
	@Test
	@DisplayName("Test guessNumber - thread safety (synchronized)")
	void testGuessNumber_ThreadSafety() throws InterruptedException {
		// Arrange: Tạo 2 thread gọi cùng lúc
		testUser.setTurns(10);

		// Act: Tạo 2 thread đoán số
		Thread thread1 = new Thread(() -> gameService.guessNumber(testUser, 1));
		Thread thread2 = new Thread(() -> gameService.guessNumber(testUser, 2));

		thread1.start();
		thread2.start();

		// Chờ 2 thread kết thúc
		thread1.join();
		thread2.join();

		// Assert: Lượt phải giảm đúng 2
		assertEquals(8, testUser.getTurns());

		// Verify: save được gọi 2 lần
		verify(userRepository, times(2)).save(testUser);
	}
}