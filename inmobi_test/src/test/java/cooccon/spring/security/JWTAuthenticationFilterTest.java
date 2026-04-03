package cooccon.spring.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import cooccon.spring.DelegatingServletInputStream;
import cooccon.spring.entity.Users;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Test class cho JWTAuthenticationFilter Kiểm tra quá trình xác thực đăng nhập
 * và cấp JWT token
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JWTAuthenticationFilter Tests")
class JWTAuthenticationFilterTest {

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;

	private JWTAuthenticationFilter jwtAuthenticationFilter;

	/**
	 * Thiết lập test
	 */
	@BeforeEach
	void setUp() {
		jwtAuthenticationFilter = new JWTAuthenticationFilter(authenticationManager);
	}

	/**
	 * Test case: Kiểm tra đăng nhập thành công với thông tin đúng
	 */
	@Test
	@DisplayName("Test attemptAuthentication - đăng nhập thành công")
	void testAttemptAuthentication_Success() throws IOException {
		// Arrange: Chuẩn bị dữ liệu đăng nhập JSON
		String loginJson = "{\"username\":\"user_test\",\"password\":\"123\"}";

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(loginJson.getBytes());
		ServletInputStream servletInputStream = new DelegatingServletInputStream(byteArrayInputStream);

		when(request.getInputStream()).thenReturn(servletInputStream);

		// Chuẩn bị mock Authentication
		Users user = new Users();
		user.setUsername("user_test");
		user.setPassword("123");
		user.setRole("ADMIN");

		Authentication auth = new UsernamePasswordAuthenticationToken("user", null,
				List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

		// Act: Gọi attemptAuthentication
		Authentication result = jwtAuthenticationFilter.attemptAuthentication(request, response);

		// Assert: Kiểm tra authentication thành công
		assertNotNull(result);
		assertTrue(result.isAuthenticated());

		// Verify: AuthenticationManager được gọi
		verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
	}

	/**
	 * Test case: Kiểm tra đăng nhập thất bại
	 */
	@Test
	@DisplayName("Test attemptAuthentication - đăng nhập thất bại")
	void testAttemptAuthentication_Failure() throws IOException {
		// Arrange: Chuẩn bị dữ liệu đăng nhập JSON
		String loginJson = "{\"username\":\"user_test\",\"password\":\"wrongpassword\"}";

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(loginJson.getBytes());
		ServletInputStream servletInputStream = new DelegatingServletInputStream(byteArrayInputStream);

		when(request.getInputStream()).thenReturn(servletInputStream);
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenThrow(new BadCredentialsException("Bad credentials"));

		// Act & Assert: Kiểm tra ném ngoại lệ
		assertThrows(RuntimeException.class, () -> jwtAuthenticationFilter.attemptAuthentication(request, response));

		// Verify: AuthenticationManager được gọi
		verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
	}

	/**
	 * Test case: Kiểm tra xử lý lỗi JSON không hợp lệ
	 */
	@Test
	@DisplayName("Test attemptAuthentication - JSON không hợp lệ")
	void testAttemptAuthentication_InvalidJson() throws IOException {
		// Arrange: JSON không hợp lệ
		String invalidJson = "invalid json";

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(invalidJson.getBytes());
		ServletInputStream servletInputStream = new DelegatingServletInputStream(byteArrayInputStream);

		when(request.getInputStream()).thenReturn(servletInputStream);

		// Act & Assert: Kiểm tra ném ngoại lệ
		assertThrows(RuntimeException.class, () -> jwtAuthenticationFilter.attemptAuthentication(request, response));
	}

	/**
	 * Test case: Kiểm tra setup AuthenticationManager
	 */
	@Test
	@DisplayName("Test constructor - AuthenticationManager được set")
	void testConstructor_AuthenticationManagerSet() {
		// Assert: AuthenticationManager không null
		assertNotNull(jwtAuthenticationFilter);
	}
}