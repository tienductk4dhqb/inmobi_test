package cooccon.spring.security;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import java.io.IOException;
import java.util.ArrayList;

//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthenticationVerficationFilter extends BasicAuthenticationFilter {

	public JWTAuthenticationVerficationFilter(AuthenticationManager authManager) {
		super(authManager);
	}

	// Kiểm tra token trong mỗi request
	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		Cookie[] cookies = req.getCookies();

		if (cookies == null || cookies.length == 0) {
			chain.doFilter(req, res);
			return;
		}

		String token = "";
		for (Cookie cookie : cookies) {
			if ("token".equals(cookie.getName())) {
				token = cookie.getValue();
			}
		}

		if (token.isEmpty()) {
			chain.doFilter(req, res);
			return;
		}

//		System.out.print("Cookie Token: " + token);
//		// Get Token
//		String token = header.replace(SecurityConstants.TOKEN_PREFIX, "");
		// Get user từ token
		String user = JWT.require(HMAC512(SecurityConstants.SECRET.getBytes())).build().verify(token).getSubject(); // Lấy
																													// username
																													// từ
																													// token
		// Xác thực từ token
		UsernamePasswordAuthenticationToken authencation = new UsernamePasswordAuthenticationToken(user, null,
				new ArrayList<>());
		// Lưu xác thực vào context
		SecurityContextHolder.getContext().setAuthentication(authencation);
		// Tiếp tục xử lý request
		chain.doFilter(req, res);
	}

	// Trích xuất thông tin user từ token
	private UsernamePasswordAuthenticationToken getAuthentication(String header) {
		// Get Token
		String token = header.replace(SecurityConstants.TOKEN_PREFIX, "");

		if (token != null) {
			String user = JWT.require(HMAC512(SecurityConstants.SECRET.getBytes())).build().verify(token).getSubject(); // Lấy
																														// username
																														// từ
																														// token
			if (user != null) {
				return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
			}
			return null;
		}
		return null;

	}

//
}
