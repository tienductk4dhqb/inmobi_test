package cooccon.spring.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Test class cho JWTAuthenticationVerficationFilter Kiểm tra quá trình xác thực
 * JWT token từ cookie
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JWTAuthenticationVerficationFilter Tests")
class JWTAuthenticationVerficationFilterTest {

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;

	private JWTAuthenticationVerficationFilter jwtVerificationFilter;
	private String validToken;

	/**
	 * Thiết lập test
	 */
	@BeforeEach
	void setUp() {
		// Tạo filter
		jwtVerificationFilter = new JWTAuthenticationVerficationFilter(authenticationManager);

		// Tạo token hợp lệ
		validToken = JWT.create().withSubject("user_test").sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));

		// Reset SecurityContext
		SecurityContextHolder.clearContext();
	}

	/**
	 * Test case: Kiểm tra xác thực thành công với token hợp lệ
	 */
	@Test
	@DisplayName("Test doFilterInternal - token hợp lệ")
	void testDoFilterInternal_ValidToken() throws IOException, ServletException {
		// Arrange: Tạo cookie với token
		Cookie tokenCookie = new Cookie("token", validToken);
		Cookie[] cookies = { tokenCookie };

		when(request.getCookies()).thenReturn(cookies);

		// Act: Chạy filter
		jwtVerificationFilter.doFilterInternal(request, response, filterChain);

		// Assert: SecurityContext đ��ợc set
		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		assertEquals("user_test", SecurityContextHolder.getContext().getAuthentication().getPrincipal());

		// Verify: FilterChain tiếp tục
		verify(filterChain, times(1)).doFilter(request, response);
	}

	/**
	 * Test case: Kiểm tra xử lý khi không có cookie
	 */
	@Test
	@DisplayName("Test doFilterInternal - không có cookie")
	void testDoFilterInternal_NoCookies() throws IOException, ServletException {
		// Arrange: Không có cookie
		when(request.getCookies()).thenReturn(null);

		// Act: Chạy filter
		jwtVerificationFilter.doFilterInternal(request, response, filterChain);

		// Assert: Yêu cầu tiếp tục mà không xác thực
		verify(filterChain, times(1)).doFilter(request, response);
	}

	/**
	 * Test case: Kiểm tra xử lý khi có cookie nhưng không phải token Test này xác
	 * nhận rằng trong trường hợp “cookie không phải token”, filter vẫn hoạt động
	 * đúng: bỏ qua và cho request đi tiếp.
	 */
	@Test
	@DisplayName("Test doFilterInternal - cookie không phải token")
	void testDoFilterInternal_OtherCookie() throws IOException, ServletException {
		// Arrange: Cookie khác
		Cookie otherCookie = new Cookie("sessionId", "xyz123");
		Cookie[] cookies = { otherCookie };

		when(request.getCookies()).thenReturn(cookies);

		// Act: Chạy filter
		jwtVerificationFilter.doFilterInternal(request, response, filterChain);

		// Verify: FilterChain tiếp tục
		verify(filterChain, times(1)).doFilter(request, response);
	}

	/**
	 * Test case: Kiểm tra xử lý cookie rỗng
	 */
	@Test
	@DisplayName("Test doFilterInternal - mảng cookie rỗng")
	void testDoFilterInternal_EmptyCookies() throws IOException, ServletException {
		// Arrange: Mảng cookie rỗng
		Cookie[] cookies = {};

		when(request.getCookies()).thenReturn(cookies);

		// Act: Chạy filter
		jwtVerificationFilter.doFilterInternal(request, response, filterChain);

		// Verify: FilterChain tiếp tục
		verify(filterChain, times(1)).doFilter(request, response);
	}

	/**
	 * Test case: Kiểm tra xử lý token không hợp lệ
	 */
	@Test
	@DisplayName("Test doFilterInternal - token không hợp lệ")
	void testDoFilterInternal_InvalidToken() throws IOException, ServletException {
		// Arrange: Token sai
		Cookie tokenCookie = new Cookie("token", "invalid.token.here");
		Cookie[] cookies = { tokenCookie };

		when(request.getCookies()).thenReturn(cookies);

		// Act & Assert: Kiểm tra xử lý lỗi
		assertThrows(Exception.class, () -> jwtVerificationFilter.doFilterInternal(request, response, filterChain));
	}

	/**
	 * Test case: Kiểm tra xử lý token bị giả mạo
	 */
	@Test
	@DisplayName("Test doFilterInternal - token bị giả mạo")
	void testDoFilterInternal_TamperedToken() throws IOException, ServletException {
		// Arrange: Token được tạo bằng khóa khác
		String tamperedToken = JWT.create().withSubject("user_test").sign(Algorithm.HMAC512("wrongsecret".getBytes()));

		Cookie tokenCookie = new Cookie("token", tamperedToken);
		Cookie[] cookies = { tokenCookie };

		when(request.getCookies()).thenReturn(cookies);

		// Act & Assert: Kiểm tra xử lý lỗi
		assertThrows(Exception.class, () -> jwtVerificationFilter.doFilterInternal(request, response, filterChain));
	}

	/**
	 * Test case: Kiểm tra constructor
	 */
	@Test
	@DisplayName("Test constructor - AuthenticationManager được set")
	void testConstructor() {
		// Assert: Filter được tạo không null
		assertNotNull(jwtVerificationFilter);
	}
}