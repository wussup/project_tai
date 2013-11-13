package pl.edu.agh.tai.jdbc.server.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import pl.edu.agh.tai.jdbc.client.User;

public class MySQLAccess {
	private Connection connect = null;
	private Statement statement = null;
	private java.sql.PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	private static String PORT = "3306";
	
	public User getUserByLogin(String login) {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost:"+PORT+"/tai?"
							+ "user=root");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement
					.executeQuery("select * from TAI.USERS as u where u.login=\'"
							+ login + "\'");

			resultSet.first();
			String name = resultSet.getString("name");
			String surname = resultSet.getString("surname");
			String salt = resultSet.getString("salt");
			String password = resultSet.getString("password");
			int type = resultSet.getInt("userType");
			User user = new User(name, surname, login, password, salt, type);

			return user;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return null;
	}

	public boolean createUser(User user) {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost:"+PORT+"/tai?"
							+ "user=root");

			// Statements allow to issue SQL queries to the database
			// statement = connect.createStatement();

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

	public ArrayList<User> readDataBase() throws Exception {
		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost:"+PORT+"/tai?"
							+ "user=root");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement.executeQuery("select * from TAI.USERS");
			return writeResultSet(resultSet);

			// // PreparedStatements can use variables and are more efficient
			// preparedStatement = connect
			// .prepareStatement("insert into  USE.COMMENTS values (default, ?, ?, ?, ? , ?, ?)");
			// //
			// "myuser, webpage, datum, summary, COMMENTS from FEEDBACK.COMMENTS");
			// // Parameters start with 1
			// preparedStatement.setString(1, "Test");
			// preparedStatement.setString(2, "TestEmail");
			// preparedStatement.setString(3, "TestWebpage");
			// preparedStatement.setDate(4, new java.sql.Date(2009, 12, 11));
			// preparedStatement.setString(5, "TestSummary");
			// preparedStatement.setString(6, "TestComment");
			// preparedStatement.executeUpdate();
			//
			// preparedStatement = connect
			// .prepareStatement("SELECT myuser, webpage, datum, summary, COMMENTS from FEEDBACK.COMMENTS");
			// resultSet = preparedStatement.executeQuery();
			// writeResultSet(resultSet);
			//
			// // Remove again the insert comment
			// preparedStatement = connect
			// .prepareStatement("delete from FEEDBACK.COMMENTS where myuser= ? ; ");
			// preparedStatement.setString(1, "Test");
			// preparedStatement.executeUpdate();
			//
			// resultSet = statement
			// .executeQuery("select * from FEEDBACK.COMMENTS");
			// writeMetaData(resultSet);

		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}

	}

	// private void writeMetaData(ResultSet resultSet) throws SQLException {
	// // Now get some metadata from the database
	// // Result set get the result of the SQL query
	//
	// System.out.println("The columns in the table are: ");
	//
	// System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
	// for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
	// System.out.println("Column " + i + " "
	// + resultSet.getMetaData().getColumnName(i));
	// }
	// }

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
			// System.out.println("User: " + name);
			// System.out.println("Website: " + surname);
			// System.out.println("Summary: " + login);
			// System.out.println("Comment: " + password);
			users.add(new User(name, surname, login, password, type));
		}
		return users;
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

	public boolean login(String login, String password) {
		// This will load the MySQL driver, each DB has its own driver
		try {
			Class.forName("com.mysql.jdbc.Driver");

			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost:"+PORT+"/tai?"
							+ "user=root");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
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

}
