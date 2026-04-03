package cooccon.spring.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import cooccon.spring.dto.UserMeDTO;
import cooccon.spring.dto.UserScoreDTO;
import cooccon.spring.entity.Users;

/**
 * Test class cho UserRepository Sử dụng @DataJpaTest để test JPA queries với H2
 * database
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	/**
	 * Thiết lập dữ liệu test trước mỗi test case
	 */
	@BeforeEach
	void setUp() {
		// Xóa tất cả user cũ
		userRepository.deleteAll();
		// Xóa tất cả idx_user cũ
		userRepository.dropUsernameIndex();

		// Tạo user test
		Users user1 = new Users();
		user1.setUsername("user1");
		user1.setPassword("password1");
		user1.setEmail("user1@gmail.com");
		user1.setScore(100);
		user1.setTurns(5);
		user1.setRole("ADMIN");

		Users user2 = new Users();
		user2.setUsername("user2");
		user2.setPassword("password2");
		user2.setEmail("user2@gmail.com");
		user2.setScore(90);
		user2.setTurns(3);
		user2.setRole("USER");

		Users user3 = new Users();
		user3.setUsername("user3");
		user3.setPassword("password3");
		user3.setEmail("user3@gmail.com");
		user3.setScore(80);
		user3.setTurns(2);
		user3.setRole("USER");

		// Lưu vào database
		userRepository.save(user1);
		userRepository.save(user2);
		userRepository.save(user3);
	}

	/**
	 * Test case: Tìm user theo username thành công
	 */
	@Test
	@DisplayName("Test findByUsername - user tồn tại")
	void testFindByUsername_Success() {
		// Act: Tìm user
		Optional<Users> result = userRepository.findByUsername("user1");

		// Assert: Kiểm tra user được trả về
		assertTrue(result.isPresent());
		assertEquals("user1", result.get().getUsername());
		assertEquals("user1@gmail.com", result.get().getEmail());
	}

	/**
	 * Test case: Tìm user theo username không tồn tại
	 */
	@Test
	@DisplayName("Test findByUsername - user không tồn tại")
	void testFindByUsername_NotFound() {
		// Act: Tìm user không tồn tại
		Optional<Users> result = userRepository.findByUsername("nonexistent");

		// Assert: Optional rỗng
		assertFalse(result.isPresent());
	}

	/**
	 * Test case: Kiểm tra username unique constraint
	 */
	@Test
	@DisplayName("Test findByUsername - username phải unique")
	void testFindByUsername_UniqueConstraint() {
		// Arrange: Tạo user có username trùng
		Users duplicateUser = new Users();
		duplicateUser.setUsername("user1");
		duplicateUser.setPassword("newpassword");
		duplicateUser.setEmail("newemail@gmail.com");

		// Act & Assert: Không thể lưu duplicate username
		assertThrows(Exception.class, () -> userRepository.save(duplicateUser));
	}

	/**
	 * Test case: Lấy top 10 user theo điểm cao nhất
	 */
	@Test
	@DisplayName("Test findTop10UsersByScore - lấy top 10")
	void testFindTop10UsersByScore() {
		// Act: Lấy top 10
		List<UserScoreDTO> result = userRepository.findTop10UsersByScore();

		// Assert: Kiểm tra kết quả
		assertNotNull(result);
		assertEquals(3, result.size());
		// Kiểm tra sắp xếp theo điểm giảm dần
		assertEquals("user1", result.get(0).getUsername());
		assertEquals(100, result.get(0).getScore());
		assertEquals("user2", result.get(1).getUsername());
		assertEquals(90, result.get(1).getScore());
		assertEquals("user3", result.get(2).getUsername());
		assertEquals(80, result.get(2).getScore());
	}

	/**
	 * Test case: Lấy thông tin user với query findMe
	 */
	@Test
	@DisplayName("Test findMe - lấy thông tin chi tiết user")
	void testFindMe() {
		// Act: Lấy thông tin user
		Optional<UserMeDTO> result = userRepository.findMe("user1");

		// Assert: Kiểm tra thông tin
		assertTrue(result.isPresent());
		UserMeDTO userMeDTO = result.get();
		assertEquals("user1", userMeDTO.getUsername());
		assertEquals("user1@gmail.com", userMeDTO.getEmail());
		assertEquals(100, userMeDTO.getScore());
		assertEquals(5, userMeDTO.getTurns());
	}

	/**
	 * Test case: findMe khi user không tồn tại
	 */
	@Test
	@DisplayName("Test findMe - user không tồn tại")
	void testFindMe_NotFound() {
		// Act: Lấy thông tin user không tồn tại
		Optional<UserMeDTO> result = userRepository.findMe("nonexistent");

		// Assert: Optional rỗng
		assertFalse(result.isPresent());
	}

	/**
	 * Test case: Kiểm tra lưu user thành công
	 */
	@Test
	@DisplayName("Test save - lưu user mới")
	void testSave() {
		// Arrange: Tạo user mới
		Users newUser = new Users();
		newUser.setUsername("user4");
		newUser.setPassword("password4");
		newUser.setEmail("user4@gmail.com");
		newUser.setScore(70);
		newUser.setTurns(1);

		// Act: Lưu user
		Users savedUser = userRepository.save(newUser);

		// Assert: Kiểm tra user được lưu
		assertNotNull(savedUser.getId());
		assertEquals("user4", savedUser.getUsername());

		// Verify: User được lưu vào database
		Optional<Users> retrievedUser = userRepository.findByUsername("user4");
		assertTrue(retrievedUser.isPresent());
	}

	/**
	 * Test case: Kiểm tra cập nhật user
	 */
	@Test
	@DisplayName("Test update - cập nhật user")
	void testUpdate() {
		// Arrange: Lấy user cũ
		Users user = userRepository.findByUsername("user1").get();

		// Act: Cập nhật thông tin
		user.setScore(150);
		user.setTurns(10);
		userRepository.save(user);

		// Assert: Kiểm tra cập nhật thành công
		Users updatedUser = userRepository.findByUsername("user1").get();
		assertEquals(150, updatedUser.getScore());
		assertEquals(10, updatedUser.getTurns());
	}

	/**
	 * Test case: Kiểm tra xóa user
	 */
	@Test
	@DisplayName("Test delete - xóa user")
	void testDelete() {
		// Arrange: Lấy user cần xóa
		Users user = userRepository.findByUsername("user1").get();

		// Act: Xóa user
		userRepository.delete(user);

		// Assert: Kiểm tra user bị xóa
		Optional<Users> result = userRepository.findByUsername("user1");
		assertFalse(result.isPresent());
	}
}