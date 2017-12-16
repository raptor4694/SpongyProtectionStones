package mx.com.rodel.sps.db.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MySQLAdapter implements CommonDataSource{
	private HikariDataSource dataSource;
	private String connectionURL;
	private String protection_table, username, password;
	
	public MySQLAdapter(String host, int port, String database, String username, String password, String protection_table) {
		this.protection_table = protection_table;
		this.username = username;
		this.password = password;
		connectionURL = "jdbc:mysql://"+host+":"+port+"/"+database;
	}
	
	@Override
	public void connect() throws SqlServiceNotFound, SQLException {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(connectionURL);
		config.setUsername(username);
		config.setPassword(password);
		config.setRegisterMbeans(true);
		config.setPoolName("SPS");
		dataSource = new HikariDataSource(config);
		
		createTables();
	}
	
	@Override
	public void createTables() throws SQLException {
		try (Connection conn = getDataSource().getConnection()) {
			String query = "create table if not exists "+protection_table+" "
					+ "("
					+ "`id` int(10) unsigned not null auto_increment,"
					+ "`owner` varchar(255) not null,"
					+ "`minx` bigint not null,"
					+ "`miny` tinyint unsigned not null,"
					+ "`minz` bigint not null,"
					+ "`maxx` bigint not null,"
					+ "`maxy` tinyint unsigned not null,"
					+ "`maxz` bigint not null,"
					+ "`centerx` bigint not null,"
					+ "`centery` tinyint unsigned not null,"
					+ "`centerz` bigint not null,"
					+ "`world` varchar(255) not null,"
					+ "`members` text default null,"
					+ "`flags` text default null,"
					+ "`type` varchar(255),"
					+ "primary key (`id`)"
					+ ") "
					+ "ENGINE=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;";
			conn.prepareStatement(query).execute();
		}
	}

	@Override
	public int countProtectionsOfType(UUID uuid, String name) {
		try (Connection conn = getDataSource().getConnection()){
			PreparedStatement ps = conn.prepareStatement("select count(*) from "+protection_table+" where `owner`=? and `type`=?");
			ps.setString(1, uuid.toString());
			ps.setString(2, name);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return rs.getInt("count(*)");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	@Override
	public HikariDataSource getDataSource() {
		return dataSource;
	}
}
