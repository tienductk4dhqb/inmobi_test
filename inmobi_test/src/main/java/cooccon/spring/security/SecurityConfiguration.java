package cooccon.spring.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import cooccon.spring.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	
	/**
	 * Bean cấu hình chuỗi filter bảo mật chính.
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager)
			throws Exception {
		// Tạo filter xử lý đăng nhập và cấp JWT token
		JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(authManager);

		// Tạo filter kiểm tra JWT token trong các request tiếp theo
		JWTAuthenticationVerficationFilter jwtVerificationFilter = new JWTAuthenticationVerficationFilter(authManager);

		http
				// Tắt CORS và CSRF vì ứng dụng sử dụng JWT và không cần session
				.cors(cors -> cors.disable()).csrf(csrf -> csrf.disable())
				// Cấu hình phân quyền cho các endpoint
				.authorizeHttpRequests(auth -> auth
						// Các request khác đều yêu cầu xác thực
						.anyRequest().authenticated())
				// Thêm các filter xử lý JWT vào chuỗi filter của Spring Security
				.addFilter(jwtAuthenticationFilter)
				.addFilter(jwtVerificationFilter)
				// Trả về mã lỗi 401 nếu người dùng chưa xác thực
				.exceptionHandling(
						ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

		// Trả về chuỗi filter đã cấu hình
		return http.build();
	}

	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
	
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
    
}
