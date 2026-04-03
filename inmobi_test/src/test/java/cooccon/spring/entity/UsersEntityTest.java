package cooccon.spring.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

/**
 * Test class cho Users entity Kiểm tra getter/setter và UserDetails
 * implementation
 */
@DisplayName("Users Entity Tests")
class UsersEntityTest {

	private Users testUser;

	/**
	 * Thiết lập dữ liệu test
	 */
	@BeforeEach
	void setUp() {
		testUser = new Users();
	}

	/**
	 * Test case: Kiểm tra constructor không tham số
	 */
	@Test
	@DisplayName("Test empty constructor")
	void testEmptyConstructor() {
		// Assert: Object được tạo không null
		assertNotNull(testUser);
	}

	/**
	 * Test case: Kiểm tra constructor có tham số
	 */
	@Test
	@DisplayName("Test parameterized constructor")
	void testParameterizedConstructor() {
		// Act: Tạo user với constructor có tham số
		Users user = new Users(1L, "user_test", "password", "test@gmail.com", 10, 5, "ADMIN");

		// Assert: Kiểm tra tất cả thuộc tính được gán đúng
		assertEquals(1L, user.getId());
		assertEquals("user_test", user.getUsername());
		assertEquals("password", user.getPassword());
		assertEquals("test@gmail.com", user.getEmail());
		assertEquals(10, user.getScore());
		assertEquals(5, user.getTurns());
		assertEquals("ADMIN", user.getRole());
	}

	/**
	 * Test case: Kiểm tra getter/setter cho id
	 */
	@Test
	@DisplayName("Test id getter/setter")
	void testIdGetterSetter() {
		// Act: Set và get id
		testUser.setId(1L);

		// Assert: Kiểm tra id
		assertEquals(1L, testUser.getId());
	}

	/**
	 * Test case: Kiểm tra getter/setter cho username
	 */
	@Test
	@DisplayName("Test username getter/setter")
	void testUsernameGetterSetter() {
		// Act: Set và get username
		testUser.setUsername("user_test");

		// Assert: Kiểm tra username
		assertEquals("user_test", testUser.getUsername());
	}

	/**
	 * Test case: Kiểm tra getter/setter cho password
	 */
	@Test
	@DisplayName("Test password getter/setter")
	void testPasswordGetterSetter() {
		// Act: Set và get password
		testUser.setPassword("hashedPassword");

		// Assert: Kiểm tra password
		assertEquals("hashedPassword", testUser.getPassword());
	}

	/**
	 * Test case: Kiểm tra getter/setter cho email
	 */
	@Test
	@DisplayName("Test email getter/setter")
	void testEmailGetterSetter() {
		// Act: Set và get email
		testUser.setEmail("test@gmail.com");

		// Assert: Kiểm tra email
		assertEquals("test@gmail.com", testUser.getEmail());
	}

	/**
	 * Test case: Kiểm tra getter/setter cho score
	 */
	@Test
	@DisplayName("Test score getter/setter")
	void testScoreGetterSetter() {
		// Act: Set và get score
		testUser.setScore(100);

		// Assert: Kiểm tra score
		assertEquals(100, testUser.getScore());
	}

	/**
	 * Test case: Kiểm tra getter/setter cho turns
	 */
	@Test
	@DisplayName("Test turns getter/setter")
	void testTurnsGetterSetter() {
		// Act: Set và get turns
		testUser.setTurns(5);

		// Assert: Kiểm tra turns
		assertEquals(5, testUser.getTurns());
	}

	/**
	 * Test case: Kiểm tra getter/setter cho role
	 */
	@Test
	@DisplayName("Test role getter/setter")
	void testRoleGetterSetter() {
		// Act: Set và get role
		testUser.setRole("ADMIN");

		// Assert: Kiểm tra role
		assertEquals("ADMIN", testUser.getRole());
	}

	/**
	 * Test case: Kiểm tra getAuthorities với role hợp lệ
	 */
	@Test
	@DisplayName("Test getAuthorities - role hợp lệ")
	void testGetAuthorities_ValidRole() {
		// Arrange: Set role
		testUser.setRole("ADMIN");

		// Act: Lấy authorities
		Collection<? extends GrantedAuthority> authorities = testUser.getAuthorities();

		// Assert: Kiểm tra authorities
		assertNotNull(authorities);
		assertEquals(1, authorities.size());
		assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
	}

	/**
	 * Test case: Kiểm tra getAuthorities khi role null
	 */
	@Test
	@DisplayName("Test getAuthorities - role null")
	void testGetAuthorities_NullRole() {
		// Arrange: Role null
		testUser.setRole(null);

		// Act: Lấy authorities
		Collection<? extends GrantedAuthority> authorities = testUser.getAuthorities();

		// Assert: Authorities rỗng
		assertNotNull(authorities);
		assertEquals(0, authorities.size());
	}

	/**
	 * Test case: Kiểm tra getAuthorities khi role rỗng
	 */
	@Test
	@DisplayName("Test getAuthorities - role rỗng")
	void testGetAuthorities_EmptyRole() {
		// Arrange: Role rỗng
		testUser.setRole("");

		// Act: Lấy authorities
		Collection<? extends GrantedAuthority> authorities = testUser.getAuthorities();

		// Assert: Authorities rỗng
		assertNotNull(authorities);
		assertEquals(0, authorities.size());
	}

	/**
	 * Test case: Kiểm tra multiple roles
	 */
	@Test
	@DisplayName("Test getAuthorities - multiple roles")
	void testGetAuthorities_MultipleRoles() {
		// Arrange: Set nhiều role (mặc dù hệ thống hiện chỉ hỗ trợ 1 role)
		testUser.setRole("ADMIN");

		// Act: Lấy authorities
		Collection<? extends GrantedAuthority> authorities = testUser.getAuthorities();

		// Assert: Kiểm tra
		assertEquals(1, authorities.size());
		assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
	}

	/**
	 * Test case: Kiểm tra default score
	 */
	@Test
	@DisplayName("Test default score value")
	void testDefaultScoreValue() {
		// Arrange: Tạo user mới mà không set score

		// Assert: Score mặc định là 0
		assertEquals(0, testUser.getScore());
	}

	/**
	 * Test case: Kiểm tra default turns
	 */
	@Test
	@DisplayName("Test default turns value")
	void testDefaultTurnsValue() {
		// Arrange: Tạo user mới mà không set turns

		// Assert: Turns mặc định là 0
		assertEquals(0, testUser.getTurns());
	}
}