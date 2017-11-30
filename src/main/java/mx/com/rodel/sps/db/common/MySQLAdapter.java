package mx.com.rodel.sps.db.common;

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
	public void createTables() {
		System.out.println("Creating tables "+protection_table);
	}

	@Override
	public DataSource getDataSource() throws SqlServiceNotFound, SQLException {
		return dataSource;
	}
}
