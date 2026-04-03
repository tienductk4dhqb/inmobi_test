package cooccon.spring.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cooccon.spring.dto.UserMeDTO;
import cooccon.spring.dto.UserScoreDTO;
import cooccon.spring.entity.Users;
import cooccon.spring.repository.UserRepository;
import cooccon.spring.service.GameService;

/**
 * Test class cho GameController Sử dụng MockMvc để test các endpoint REST
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GameController Tests")
class GameControllerTest {

	@Mock
	private GameService gameService;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private GameController gameController;

	private Principal mockPrincipal;
	private Users testUser;

	/**
	 * Thiết lập dữ liệu test trước mỗi test case
	 */
	@BeforeEach
	void setUp() {
		// Tạo mock Principal (đại diện cho người dùng đã xác thực)
		mockPrincipal = mock(Principal.class);
		lenient().when(mockPrincipal.getName()).thenReturn("user_test");

		// Tạo user test
		testUser = new Users();
		testUser.setId(1L);
		testUser.setUsername("user_test");
		testUser.setPassword("123");
		testUser.setEmail("test@gmail.com");
		testUser.setScore(10);
		testUser.setTurns(5);
		testUser.setRole("ADMIN");
	}

	/**
	 * Test case: Kiểm tra endpoint /guess trả về đúng kết quả khi đoán số đúng
	 */
	@Test
	@DisplayName("Test /guess endpoint - người dùng đoán đúng số")
	void testGuessEndpoint_WhenGuessCorrect() {
		// Arrange: Chuẩn bị dữ liệu
		Map<String, Object> expectedResult = Map.of("Số dự đoán", 3, "Kết quả", 3, "Lượt chơi còn lại", 4,
				"Điểm hiện tại", 11);

		when(userRepository.findByUsername("user_test")).thenReturn(Optional.of(testUser));
		when(gameService.guessNumber(testUser, 3)).thenReturn(expectedResult);

		// Act: Gọi phương thức controller
		Map result = gameController.guess(3, mockPrincipal);

		// Assert: Kiểm tra kết quả trả về
		assertNotNull(result);
		assertEquals(3, result.get("Số dự đoán"));
		assertEquals(3, result.get("Kết quả"));

		// Verify: Kiểm tra các mock được gọi đúng cách
		verify(userRepository, times(1)).findByUsername("user_test");
		verify(gameService, times(1)).guessNumber(testUser, 3);
	}

	/**
	 * Test case: Kiểm tra endpoint /buy-turns thêm lượt chơi cho người dùng
	 */
	@Test
	@DisplayName("Test /buy-turns endpoint - mua thêm lượt chơi")
	void testBuyTurnsEndpoint() {
		// Arrange: Chuẩn bị dữ liệu
		when(userRepository.findByUsername("user_test")).thenReturn(Optional.of(testUser));

		// Act: Gọi phương thức mua lượt
		String result = gameController.buyTurns(mockPrincipal);

		// Assert: Kiểm tra thông báo trả về
		assertEquals("Đã mua thêm 5 lượt!", result);

		// Verify: Kiểm tra GameService được gọi
		verify(gameService, times(1)).buyTurns(testUser);
	}

	/**
	 * Test case: Kiểm tra endpoint /leaderboard trả về danh sách top 10 người chơi
	 */
	@Test
	@DisplayName("Test /leaderboard endpoint - lấy danh sách xếp hạng")
	void testLeaderboardEndpoint() {
		// Arrange: Chuẩn bị danh sách mock
		List<UserScoreDTO> mockLeaderboard = List.of(new UserScoreDTO("user1", 100), new UserScoreDTO("user2", 90),
				new UserScoreDTO("user_test", 10));
		when(gameService.leaderboard()).thenReturn(mockLeaderboard);

		// Act: Gọi endpoint leaderboard
		List<UserScoreDTO> result = gameController.leaderboard();

		// Assert: Kiểm tra danh sách trả về
		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals("user1", result.get(0).getUsername());
		assertEquals(100, result.get(0).getScore());

		// Verify: Kiểm tra GameService được gọi
		verify(gameService, times(1)).leaderboard();
	}

	/**
	 * Test case: Kiểm tra endpoint /me trả về thông tin người dùng hiện tại
	 */
	@Test
	@DisplayName("Test /me endpoint - lấy thông tin người dùng hiện tại")
	void testMeEndpoint() {
		// Arrange: Chuẩn bị UserMeDTO
		UserMeDTO userMeDTO = new UserMeDTO("user_test", "test@gmail.com", 10, 5);
		when(userRepository.findMe("user_test")).thenReturn(Optional.of(userMeDTO));

		// Act: Gọi endpoint /me
		UserMeDTO result = gameController.me(mockPrincipal);

		// Assert: Kiểm tra thông tin người dùng
		assertNotNull(result);
		assertEquals("user_test", result.getUsername());
		assertEquals("test@gmail.com", result.getEmail());
		assertEquals(10, result.getScore());
		assertEquals(5, result.getTurns());

		// Verify: Kiểm tra UserRepository được gọi
		verify(userRepository, times(1)).findMe("user_test");
	}

	/**
	 * Test case: Kiểm tra xử lý lỗi khi người dùng không tồn tại
	 */
	@Test
	@DisplayName("Test guess endpoint - người dùng không tồn tại")
	void testGuessEndpoint_UserNotFound() {
		// Arrange: Giả lập user không tồn tại
		when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
		when(mockPrincipal.getName()).thenReturn("nonexistent");

		// Act & Assert: Kiểm tra ném ngoại lệ
		assertThrows(Exception.class, () -> gameController.guess(3, mockPrincipal));
	}
}