package cooccon.spring.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test class cho SecurityConfiguration Kiểm tra cấu hình Spring Security và các
 * bean
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("SecurityConfiguration Tests")
class SecurityConfigurationTest {

	@Autowired(required = false)
	private SecurityFilterChain securityFilterChain;

	@Autowired(required = false)
	private AuthenticationManager authenticationManager;

	@Autowired(required = false)
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired(required = false)
	private RedisTemplate<String, Object> redisTemplate;

	/**
	 * Test case: Kiểm tra SecurityFilterChain bean được tạo
	 */
	@Test
	@DisplayName("Test securityFilterChain bean - tồn tại")
	void testSecurityFilterChainBean() {
		// Assert: Bean tồn tại
		assertNotNull(securityFilterChain, "SecurityFilterChain bean phải được tạo");
	}

	/**
	 * Test case: Kiểm tra AuthenticationManager bean được tạo
	 */
	@Test
	@DisplayName("Test authenticationManager bean - tồn tại")
	void testAuthenticationManagerBean() {
		// Assert: Bean tồn tại
		assertNotNull(authenticationManager, "AuthenticationManager bean phải được tạo");
	}

	/**
	 * Test case: Kiểm tra BCryptPasswordEncoder bean được tạo
	 */
	@Test
	@DisplayName("Test bCryptPasswordEncoder bean - tồn tại")
	void testBCryptPasswordEncoderBean() {
		// Assert: Bean tồn tại
		assertNotNull(bCryptPasswordEncoder, "BCryptPasswordEncoder bean phải được tạo");
	}

	/**
	 * Test case: Kiểm tra RedisTemplate bean được tạo
	 */
	@Test
	@DisplayName("Test redisTemplate bean - tồn tại")
	void testRedisTemplateBean() {
		// Assert: Bean tồn tại
		assertNotNull(redisTemplate, "RedisTemplate bean phải được tạo");
	}

	/**
	 * Test case: Kiểm tra mã hóa password bằng BCryptPasswordEncoder
	 */
	@Test
	@DisplayName("Test BCryptPasswordEncoder - mã hóa password")
	void testBCryptPasswordEncoding() {
		// Arrange: Password gốc
		String rawPassword = "mypassword123";

		// Act: Mã hóa password
		String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);

		// Assert: Password đã mã hóa khác với gốc
		assertNotEquals(rawPassword, encodedPassword);

		// Verify: Password đúng được match
		assertTrue(bCryptPasswordEncoder.matches(rawPassword, encodedPassword));

		// Verify: Password sai không được match
		assertFalse(bCryptPasswordEncoder.matches("wrongpassword", encodedPassword));
	}

	/**
	 * Test case: Kiểm tra hai password mã hóa giống nhau không bằng nhau
	 */
	@Test
	@DisplayName("Test BCryptPasswordEncoder - mỗi lần mã hóa khác nhau")
	void testBCryptPasswordEncoding_DifferentEachTime() {
		// Arrange: Password gốc
		String rawPassword = "mypassword123";

		// Act: Mã hóa password 2 lần
		String encoded1 = bCryptPasswordEncoder.encode(rawPassword);
		String encoded2 = bCryptPasswordEncoder.encode(rawPassword);

		// Assert: 2 hash khác nhau mặc dù cùng password
		assertNotEquals(encoded1, encoded2);

		// Verify: Cả 2 đều match với password gốc
		assertTrue(bCryptPasswordEncoder.matches(rawPassword, encoded1));
		assertTrue(bCryptPasswordEncoder.matches(rawPassword, encoded2));
	}

	/**
	 * Test case: Kiểm tra RedisTemplate được cấu hình đúng
	 */
	@Test
	@DisplayName("Test redisTemplate - được cấu hình đúng")
	void testRedisTemplateConfiguration() {
		// Assert: RedisTemplate có connection factory
		assertNotNull(redisTemplate.getConnectionFactory());
	}

	/**
	 * Test case: Kiểm tra SecurityConstants
	 */
	@Test
	@DisplayName("Test SecurityConstants - giá trị hợp lệ")
	void testSecurityConstants() {
		// Assert: Các hằng số không null
		assertNotNull(SecurityConstants.SECRET);
		assertNotNull(SecurityConstants.TOKEN_PREFIX);
		assertNotNull(SecurityConstants.HEADER_STRING);
		assertNotNull(SecurityConstants.CONTENT_TYPE);
		assertNotNull(SecurityConstants.APPLICATION_JSON);

		// Assert: Thời gian hết hạn dương
		assertTrue(SecurityConstants.EXPIRATION_TIME > 0);

		// Assert: Giá trị mong đợi
		assertEquals("oursecretkey", SecurityConstants.SECRET);
		assertEquals("Bearer ", SecurityConstants.TOKEN_PREFIX);
		assertEquals("Authorization", SecurityConstants.HEADER_STRING);
		assertEquals("Content-Type", SecurityConstants.CONTENT_TYPE);
		assertEquals("application/json", SecurityConstants.APPLICATION_JSON);
		assertEquals(864_000_000, SecurityConstants.EXPIRATION_TIME);
	}
}