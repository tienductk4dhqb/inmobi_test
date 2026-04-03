package cooccon.spring.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cooccon.spring.dto.UserMeDTO;
import cooccon.spring.dto.UserScoreDTO;
import cooccon.spring.entity.Users;
import cooccon.spring.repository.UserRepository;
import cooccon.spring.service.GameService;

@RestController
public class GameController {
	private final GameService gameService;
	private final UserRepository userRepository;

	public GameController(GameService gameService, UserRepository userRepository) {
		super();
		this.gameService = gameService;
		this.userRepository = userRepository;
	}

	@PostMapping("/guess")
	public Map guess(@RequestParam int number, Principal principal) {
		Users user = userRepository.findByUsername(principal.getName()).orElseThrow();
		return gameService.guessNumber(user, number);
	}
	
	@PostMapping("/buy-turns")
	public String buyTurns(Principal principal) {
		Users user = userRepository.findByUsername(principal.getName()).orElseThrow();
		gameService.buyTurns(user);
		return "Đã mua thêm 5 lượt!";
	}

	@GetMapping("/leaderboard")
	public List<UserScoreDTO> leaderboard() {
		return gameService.leaderboard();
		
	}

	@GetMapping("/me")
	public UserMeDTO me(Principal principal) {
		UserMeDTO userMe = userRepository.findMe(principal.getName()).orElseThrow();
		return userMe;
	}
}
