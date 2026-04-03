package cooccon.spring.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import cooccon.spring.entity.Users;
import cooccon.spring.repository.UserRepository;

/**
 * Test class cho UserService Kiểm tra chức năng UserDetailsService và load user
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@InjectMocks
	private UserService userService;

	private Users testUser;

	/**
	 * Thiết lập dữ liệu test
	 */
	@BeforeEach
	void setUp() {
		// Tạo user test
		testUser = new Users();
		testUser.setId(1L);
		testUser.setUsername("user_test");
		testUser.setPassword("$2a$10$hashed_password");
		testUser.setEmail("test@gmail.com");
		testUser.setScore(10);
		testUser.setTurns(5);
		testUser.setRole("ADMIN");
	}

	/**
	 * Test case: Kiểm tra load user theo username thành công
	 */
	@Test
	@DisplayName("Test loadUserByUsername - user tồn tại")
	void testLoadUserByUsername_Success() {
		// Arrange: Mock UserRepository trả về user
		when(userRepository.findByUsername("user_test")).thenReturn(Optional.of(testUser));

		// Act: Load user by username
		UserDetails result = userService.loadUserByUsername("user_test");

		// Assert: Kiểm tra user được trả về
		assertNotNull(result);
		assertEquals("user_test", result.getUsername());

		// Verify: Repository được gọi đúng cách
		verify(userRepository, times(1)).findByUsername("user_test");
	}

	/**
	 * Test case: Kiểm tra lỗi khi user không tồn tại
	 */
	@Test
	@DisplayName("Test loadUserByUsername - user không tồn tại")
	void testLoadUserByUsername_UserNotFound() {
		// Arrange: Mock UserRepository trả về Optional rỗng
		when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

		// Act & Assert: Kiểm tra ném ngoại lệ UsernameNotFoundException
		assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonexistent"));

		// Verify: Repository được gọi
		verify(userRepository, times(1)).findByUsername("nonexistent");
	}

	/**
	 * Test case: Kiểm tra thông tin user được trả về đúng
	 */
	@Test
	@DisplayName("Test loadUserByUsername - kiểm tra chi tiết user")
	void testLoadUserByUsername_CheckUserDetails() {
		// Arrange: Mock UserRepository
		when(userRepository.findByUsername("user_test")).thenReturn(Optional.of(testUser));

		// Act: Load user
		UserDetails result = userService.loadUserByUsername("user_test");

		// Assert: Kiểm tra tất cả thông tin user
		assertNotNull(result);
		assertEquals("user_test", result.getUsername());
		assertEquals("$2a$10$hashed_password", result.getPassword());
		assertNotNull(result.getAuthorities());

		// Verify
		verify(userRepository, times(1)).findByUsername("user_test");
	}

	/**
	 * Test case: Kiểm tra quyền (authorities) của user
	 */
	@Test
	@DisplayName("Test loadUserByUsername - kiểm tra authorities")
	void testLoadUserByUsername_CheckAuthorities() {
		// Arrange: Mock UserRepository
		when(userRepository.findByUsername("user_test")).thenReturn(Optional.of(testUser));

		// Act: Load user
		UserDetails result = userService.loadUserByUsername("user_test");

		// Assert: Kiểm tra authorities
		assertNotNull(result.getAuthorities());
		assertEquals(1, result.getAuthorities().size());
		assertTrue(result.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
	}

	/**
	 * Test case: Kiểm tra user không có role
	 */
	@Test
	@DisplayName("Test loadUserByUsername - user không có role")
	void testLoadUserByUsername_NoRole() {
		// Arrange: Tạo user không có role
		testUser.setRole(null);
		when(userRepository.findByUsername("user_test")).thenReturn(Optional.of(testUser));

		// Act: Load user
		UserDetails result = userService.loadUserByUsername("user_test");

		// Assert: Kiểm tra authorities rỗng
		assertNotNull(result);
		assertEquals(0, result.getAuthorities().size());
	}
}