package pl.edu.agh.tai.jdbc.client;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.tai.jdbc.server.mysql.MySQLAccess;

public class DatabaseTests extends TestCase {

	private MySQLAccess sql;
	
	public DatabaseTests() {
		sql = new MySQLAccess();
	}

	@Before
	public void setUp() throws Exception {
		sql = new MySQLAccess();
	}

	@After
	public void tearDown() throws Exception {
		sql=null;
	}

	@Test
	public void testGetUserByLogin() {
		assertNull(sql.getUserByLogin("abcde"));
		
		assertNotNull(sql.getUserByLogin("taras"));
		
	}

	@Test
	public void testLogin() {
		assertTrue(sql.login("taras", "melon"));
		assertFalse(sql.login("what", "what"));
	}

	@Test
	public void testGetAllUsers() {
		assertNotNull(sql.getAllUsers());
	}

	@Test
	public void testGetDropboxToken() {
		assertNotNull(sql.getDropboxToken());
	}

}
