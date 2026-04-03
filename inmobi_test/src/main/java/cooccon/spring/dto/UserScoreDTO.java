package cooccon.spring.dto;

import java.io.Serializable;

public class UserScoreDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String username;
	private int score;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public UserScoreDTO(String username, int score) {
		super();
		this.username = username;
		this.score = score;
	}

}
