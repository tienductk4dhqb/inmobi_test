package cooccon.spring.security;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;

import com.fasterxml.jackson.databind.ObjectMapper;
import cooccon.spring.entity.Users;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private static final Logger log = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
	
	private final AuthenticationManager authenticationManager; // Dung de xac thuc nguoi dung

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	// Thuc hien xac thuc tu thong tin dang nhap gui len
	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException {
		log.info("public Authentication attemptAuthentication");
		try {
			// Mapping JSON -> Object -> req(JSON) tra ve
			Users credentials = new ObjectMapper().readValue(req.getInputStream(), Users.class); // request body JSON
			
			return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(),
					credentials.getPassword(), new ArrayList<>())); // Khong co ROLE cu the

		} catch (Exception e) {
			log.error("Error parsing login request", e.toString());
			// TODO: handle exception
			throw new RuntimeException(e); // Xử lý lỗi đọc dữ liệu
		}
	}

	// Nếu đăng nhập thành công ⇒ tạo JWT và gắn vào header

	@Override
	protected void successfulAuthentication(HttpServletRequest req,
			HttpServletResponse res,
			FilterChain filterchain,
			Authentication auth) throws IOException, ServletException {
		
		String token = JWT.create()
				.withSubject(((Users) auth.getPrincipal()).getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
				.sign(HMAC512(SecurityConstants.SECRET.getBytes()));
		
		// Cookie
		ResponseCookie cookie = ResponseCookie.from("token", token)
				    .path("/")                  //  cookie áp dụng cho toàn bộ domain
				    .maxAge(Duration.ofHours(240)) //  thời gian sống của cookie
				    .build();
		
		
		
//		log.info("Token User Name: " + userName + " : " + token);
		res.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
		res.setHeader(SecurityConstants.CONTENT_TYPE, SecurityConstants.APPLICATION_JSON);
//		res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
	}

}
