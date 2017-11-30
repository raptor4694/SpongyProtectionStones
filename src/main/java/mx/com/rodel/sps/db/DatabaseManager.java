package mx.com.rodel.sps.db;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.utils.Helper;

public class DatabaseManager {
	private SpongyPS pl;
	
	private SqlService sql;
	private DataSource connection;
	
	public DatabaseManager(SpongyPS pl) {
		this.pl = pl;
		
		if(sql == null){
			sql = Sponge.getServiceManager().provide(SqlService.class).get();
		}
	}
	
	public void connect(String host, int port, String database, String user, String password, String table) throws SQLException{
		String url = Helper.format("jdbc:mysql://[{0}[:{1}]@]{2}/{3}", user, password, host, database);
		connection = sql.getDataSource(url);
	}
}
