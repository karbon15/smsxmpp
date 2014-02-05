package name.theberge.smsxmpp.xmppserver;

import java.sql.ResultSet;
import java.sql.SQLException;

public  class UserFactory {
	public static User buildFromResultSet(ResultSet rs) throws SQLException{
		User u = new User();
		u.setId(rs.getInt(1));
		u.setJid(rs.getString(2));
		u.setNumber(rs.getString(3));
		return u;
	}
}
