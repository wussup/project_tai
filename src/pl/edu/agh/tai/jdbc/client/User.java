package pl.edu.agh.tai.jdbc.client;

import java.io.Serializable;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String surname;
	private String login;
	private String password;
	private String salt;
	private int type;

	public User() {

	}

	public User(String name, String surname, String login, String password, int type) {
		this.name = name;
		this.surname = surname;
		this.login = login;
		this.password = password;
		this.type = type;
	}

	public User(String name, String surname, String login, String password,
			String salt, int type) {
		this.name = name;
		this.surname = surname;
		this.login = login;
		this.password = password;
		this.salt = salt;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
