package cooccon.spring;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import cooccon.spring.entity.Users;
import cooccon.spring.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public DataInitializer(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Override
	public void run(String... args) throws Exception {

		Users user = new Users();
		user.setUsername("user_test");
		user.setPassword(bCryptPasswordEncoder.encode("123"));
		user.setEmail("emailtest@gmail.com");
		user.setRole("ADMIN");
		user.setTurns(3);
		userRepository.save(user);

	}
}