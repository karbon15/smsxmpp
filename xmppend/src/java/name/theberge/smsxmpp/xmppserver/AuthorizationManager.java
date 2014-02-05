package name.theberge.smsxmpp.xmppserver;

import org.jivesoftware.database.DbConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class AuthorizationManager {
	private Connection connection = null;
	
	private static AuthorizationManager instance = null;
	
	private AuthorizationManager() throws SQLException	{
		connection = DbConnectionManager.getConnection();
	}
	
	public static AuthorizationManager getInstance()	{
		if(instance == null)	{
			try {
				instance = new AuthorizationManager();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	public void finalize()	{
		DbConnectionManager.closeConnection(connection);
	}
	
	public User getUserByJID(String jid) throws SQLException{
		if(connection.isClosed())
			connection = DbConnectionManager.getConnection();
		
		PreparedStatement statement = connection.prepareStatement("SELECT * FROM ofSmsxmppAuth WHERE jid=?");
		statement.setString(1, jid);
		statement.execute();
		
		ResultSet resultSet = statement.getResultSet();

		if(resultSet.next())
			return UserFactory.buildFromResultSet(resultSet);
		else
			return null;

	}	
	public User getUserByNumber(String number) throws SQLException{
		if(connection.isClosed())
			connection = DbConnectionManager.getConnection();
		
		PreparedStatement statement = connection.prepareStatement("SELECT * FROM ofSmsxmppAuth WHERE number=?");
		statement.setString(1, number);
		statement.execute();
		
		ResultSet resultSet = statement.getResultSet();

		if(resultSet.next())
			return UserFactory.buildFromResultSet(resultSet);
		else
			return null;
	}
	
	public Collection<User> getAllUsers() throws SQLException {
		
		ArrayList<User> users = new ArrayList<User>();
		
		if(connection.isClosed())
			connection = DbConnectionManager.getConnection();
		
		Statement statement = connection.createStatement();
		statement.execute("SELECT * FROM ofSmsxmppAuth");
		
		ResultSet resultSet = statement.getResultSet();
		
		while(resultSet.next())
			users.add(UserFactory.buildFromResultSet(resultSet));

		return users;
	}
	
	public void createUser(String jid, String number) throws SQLException {

		if (connection.isClosed())
			connection = DbConnectionManager.getConnection();

		PreparedStatement statement = connection.prepareStatement("INSERT INTO ofSmsxmppAuth (jid, number) VALUES(?, ?)");
		statement.setString(1, jid);
		statement.setString(2, number);
		statement.execute();
	}
	
	public void updateUser(int id, String jid, String number) throws SQLException {

		if (connection.isClosed())
			connection = DbConnectionManager.getConnection();

		PreparedStatement statement = connection.prepareStatement("UPDATE ofSmsxmppAuth SET jid=?, number=? WHERE id=?");
		statement.setString(1, jid);
		statement.setString(2, number);
		statement.setInt(3, id);
		statement.execute();
	}
	
	public void deleteUser(int id) throws SQLException {

		if (connection.isClosed())
			connection = DbConnectionManager.getConnection();

		PreparedStatement statement = connection.prepareStatement("DELETE FROM ofSmsxmppAuth WHERE id=?");
		statement.setInt(1, id);
		statement.execute();
	}
}

