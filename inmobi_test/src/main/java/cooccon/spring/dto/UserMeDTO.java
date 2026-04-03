package cooccon.spring.dto;

public class UserMeDTO {
	private String username;
	private String email;
	private int score;
	private int turns;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getTurns() {
		return turns;
	}
	public void setTurns(int turns) {
		this.turns = turns;
	}
	public UserMeDTO(String username, String email, int score, int turns) {
		super();
		this.username = username;
		this.email = email;
		this.score = score;
		this.turns = turns;
	}
	
	

}
