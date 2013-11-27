package pl.edu.agh.tai.jdbc.server.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import pl.edu.agh.tai.jdbc.client.GreetingService;
import pl.edu.agh.tai.jdbc.client.User;

/**
 * Miscellaneous methods for operate on database
 * 
 * @since 26.11.2013
 * @author Taras Melon&Jakub Kolodziej
 */
public class MySQLAccess {

	private static final Logger log = Logger.getLogger(GreetingService.class
			.getName());
	private static String PORT = "3307";
	
	private Connection connect = null;
	private Statement statement = null;
	private java.sql.PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public MySQLAccess() {
		BasicConfigurator.configure();
	}

	/**
	 * Find user info by user login from database
	 * 
	 * @param login
	 *            user login
	 * @return user info
	 */
	public User getUserByLogin(String login) {
		try {
			connectToDatabase();
			// Result set get the result of the SQL query
			resultSet = statement
					.executeQuery("select * from TAI.USERS as u where u.login=\'"
							+ login + "\'");

			if (resultSet.first()) {
				String name = resultSet.getString("name");
				String surname = resultSet.getString("surname");
				String salt = resultSet.getString("salt");
				String password = resultSet.getString("password");
				int type = resultSet.getInt("userType");
				User user = new User(name, surname, login, password, salt, type);

				return user;
			} else
				return null;
		} catch (ClassNotFoundException e) {
			log.error("Error in getUserByLogin method", e);
		} catch (SQLException e) {
			log.error("Error in getUserByLogin method", e);
		} finally {
			close();
		}
		return null;
	}

	/**
	 * Create user in database by user info
	 * 
	 * @param user
	 *            user info
	 * @return success or fail
	 */
	public boolean createUser(User user) {
		try {
			getConnection();

			preparedStatement = connect
					.prepareStatement("insert into  TAI.USERS(userId, name, surname, login, password, salt, userType) values (default, ?, ?, ?, ? , ?, ?)");
			preparedStatement.setString(1, user.getName());
			preparedStatement.setString(2, user.getSurname());
			preparedStatement.setString(3, user.getLogin());
			preparedStatement.setString(4, user.getPassword());
			preparedStatement.setString(5, user.getSalt());
			preparedStatement.setInt(6, user.getType());
			preparedStatement.executeUpdate();

			return true;
		} catch (ClassNotFoundException e) {
			log.error("Error in createUser method", e);
		} catch (SQLException e) {
			log.error("Error in createUser method", e);
		} finally {
			close();
		}
		return false;
	}

	/**
	 * Check database whether contain login and password
	 * 
	 * @param login
	 *            login for check
	 * @param password
	 *            password for check
	 * @return success or fail
	 */
	public boolean login(String login, String password) {
		// This will load the MySQL driver, each DB has its own driver
		try {
			connectToDatabase();
			// Result set get the result of the SQL query
			resultSet = statement
					.executeQuery("select * from TAI.USERS as u where u.login=\'"
							+ login + "\' and u.password=\'" + password + "\'");

			return resultSet.first();
		} catch (ClassNotFoundException e) {
			log.error("Error in login method", e);
		} catch (SQLException e) {
			log.error("Error in login method", e);
		} finally {
			close();
		}
		return false;
	}

	/**
	 * Fetch all users from database
	 * 
	 * @return all users from database
	 */
	public ArrayList<User> getAllUsers() {
		try {
			connectToDatabase();
			// Result set get the result of the SQL query
			resultSet = statement.executeQuery("select * from TAI.USERS");
			return writeResultSet(resultSet);

		} catch (Exception e) {
			log.error("Error in getAllUsers method", e);
		} finally {
			close();
		}
		return null;

	}

	private ArrayList<User> writeResultSet(ResultSet resultSet)
			throws SQLException {
		ArrayList<User> users = new ArrayList<User>();
		// ResultSet is initially before the first data set
		while (resultSet.next()) {
			// It is possible to get the columns via name
			// also possible to get the columns via the column number
			// which starts at 1
			// e.g. resultSet.getSTring(2);
			String name = resultSet.getString("name");
			String surname = resultSet.getString("surname");
			String login = resultSet.getString("login");
			String password = resultSet.getString("password");
			int type = resultSet.getInt("userType");
			users.add(new User(name, surname, login, password, type));
		}
		return users;
	}

	private void connectToDatabase() throws ClassNotFoundException,
			SQLException {
		getConnection();

		// Statements allow to issue SQL queries to the database
		statement = connect.createStatement();
	}

	private void getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");

		// Setup the connection with the DB
		connect = DriverManager.getConnection("jdbc:mysql://localhost:" + PORT
				+ "/tai?" + "user=root");
	}

	// You need to close the resultSet
	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {
			log.error("Error in close method", e);
		}
	}

	/**
	 * Getting dropbox token for logging to Dropbox
	 * 
	 * @return dropbox token
	 */
	public String getDropboxToken() {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			// Setup the connection with the DB
			connect = DriverManager.getConnection("jdbc:mysql://localhost:"
					+ PORT + "/tai?" + "user=root");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement.executeQuery("select * from TAI.SETTINGS");

			return resultSet.first() ? resultSet.getString("dropbox_token")
					: null;
		} catch (ClassNotFoundException e) {
			log.error("Error in getDropboxToken method", e);
		} catch (SQLException e) {
			log.error("Error in getDropboxToken method", e);
		} finally {
			close();
		}
		return null;
	}

	/**
	 * Set new Dropbox token and save it to DB
	 * 
	 * @param token
	 *            new token
	 * @return true if operation done, otherwise false
	 */
	public boolean setDropboxToken(String token) {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			connect = DriverManager.getConnection("jdbc:mysql://localhost:"
					+ PORT + "/tai?" + "user=root");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			ResultSet tokenExist = statement
					.executeQuery("select * from TAI.SETTINGS");

			// Result set get the result of the SQL query
			if (tokenExist.first()) {
				statement
						.executeUpdate("update TAI.SETTINGS set dropbox_token=\""
								+ token + "\" WHERE settingId IS NOT NULL");
			} else {
				preparedStatement = connect
						.prepareStatement("insert into  TAI.SETTINGS(settingId, dropbox_token) values (default, ?)");
				preparedStatement.setString(1, token);
				preparedStatement.executeUpdate();

			}

			return true;
		} catch (ClassNotFoundException e) {
			log.error("Error in setDropboxToken method", e);
		} catch (SQLException e) {
			log.error("Error in setDropboxToken method", e);
		} finally {
			close();
		}
		return false;
	}

}
