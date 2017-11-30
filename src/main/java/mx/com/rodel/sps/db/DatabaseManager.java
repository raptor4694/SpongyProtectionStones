package mx.com.rodel.sps.db;

import java.sql.SQLException;

import javax.sql.DataSource;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.db.common.CommonDataSource;
import mx.com.rodel.sps.db.common.SqlServiceNotFound;

public class DatabaseManager implements CommonDataSource{
	private SpongyPS pl;
	private CommonDataSource dataSource;
	
	public DatabaseManager(SpongyPS pl, CommonDataSource dataSource) {
		this.pl = pl;
		this.dataSource = dataSource;
	}

	@Override
	public DataSource getDataSource() throws SqlServiceNotFound, SQLException {
		return dataSource.getDataSource();
	}

	@Override
	public void connect() throws SqlServiceNotFound, SQLException {
		dataSource.connect();
		createTables();
	}

	@Override
	public void createTables() {
		dataSource.createTables();
	}
}
