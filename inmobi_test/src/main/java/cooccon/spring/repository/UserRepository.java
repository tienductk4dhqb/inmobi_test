package cooccon.spring.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cooccon.spring.dto.UserMeDTO;
import cooccon.spring.dto.UserScoreDTO;
import cooccon.spring.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {
	Optional<Users> findByUsername(String username);
	
	@Query(value = "SELECT username, score FROM users ORDER BY score DESC LIMIT 10", nativeQuery = true)
	List<UserScoreDTO> findTop10UsersByScore();
	
	@Query(value = "SELECT username, email, score, turns FROM users WHERE username=:username", nativeQuery = true)
	Optional<UserMeDTO> findMe(@Param("username") String username);
	
	@Modifying
	@Query(value = "DROP INDEX IF EXISTS IDX_USERS_USERNAME;", nativeQuery = true)
	void dropUsernameIndex();
}
