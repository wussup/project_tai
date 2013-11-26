package pl.edu.agh.tai.jdbc.server.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.tai.jdbc.client.User;

/**
 * Miscellaneous methods for operate on database
 * 
 * @since 26.11.2013
 * @author Taras Melon&Jakub Kolodziej
 */
public class MySQLAccess {

	private static String PORT = "3307";

	private static final transient Logger log = LoggerFactory
			.getLogger(MySQLAccess.class);

	private Connection connect = null;
	private Statement statement = null;
	private java.sql.PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

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
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			log.error("Error!", "In method getUserByLogin", e);
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
					.prepareStatement("insert into  TAI.USERS(userId, name, surname, login, password, salt) values (default, ?, ?, ?, ? , ?)");
			preparedStatement.setString(1, user.getName());
			preparedStatement.setString(2, user.getSurname());
			preparedStatement.setString(3, user.getLogin());
			preparedStatement.setString(4, user.getPassword());
			preparedStatement.setString(5, user.getSalt());
			preparedStatement.executeUpdate();

			return true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return false;
	}

	/**
	 * Find dropbox token for logging to Dropbox
	 * 
	 * @return dropbox token
	 */
	public String getDropboxToken() {
		try {
			connectToDatabase();
			// Result set get the result of the SQL query
			resultSet = statement.executeQuery("select * from TAI.SETTINGS");

			return resultSet.first() ? resultSet.getString("dropbox_token")
					: null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return null;
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
			e.printStackTrace();
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

		}
	}

}
