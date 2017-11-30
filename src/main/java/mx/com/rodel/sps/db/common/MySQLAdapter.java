package mx.com.rodel.sps.db.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.spongepowered.api.service.sql.SqlService;

public class MySQLAdapter implements CommonDataSource{
	private DataSource dataSource;
	private String connectionURL;
	private String protection_table;
	
	public MySQLAdapter(String host, int port, String database, String username, String password, String protection_table) {
		this.protection_table = protection_table;
		connectionURL = "jdbc:mysql://"+username+":"+password+"@"+host+":"+port+"/"+database;
	}
	
	@Override
	public void connect() throws SqlServiceNotFound, SQLException {
		SqlService service = getSqlService();
		dataSource = service.getDataSource(connectionURL);
	}
	
	@Override
	public void createTables() throws SQLException {
		// Question, this is needed? Or its closed automatic, i see the major of plugins don't close connections, and i close the statement cause SonarLint say it...
		Connection con = null;
		PreparedStatement statement = null;
		try  {
			con = getDataSource().getConnection();
			
			String query = "create table if not exists "+protection_table+" "
					+ "("
					+ "`id` int(10) unsigned not null auto_increment,"
					+ "`owner` varchar(255) not null,"
					+ "`pos1` varchar(255) not null,"
					+ "`pos2` varchar(255) not null,"
					+ "`center` varchar(255) not null,"
					+ "`world` varchar(255) not null,"
					+ "`members` text default null,"
					+ "`type` varchar(255),"
					+ "primary key (`id`)"
					+ ") "
					+ "ENGINE=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;";
			statement = con.prepareStatement(query);
			statement.execute();
		} finally {
			System.out.println("Closing...");
			if(statement!=null) statement.close();
			if(con!=null) con.close();
		}
	}

	@Override
	public DataSource getDataSource() {
		return dataSource;
	}
}
