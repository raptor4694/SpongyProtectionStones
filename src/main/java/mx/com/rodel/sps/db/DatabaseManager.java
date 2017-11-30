package mx.com.rodel.sps.db;

import java.sql.SQLException;

import javax.sql.DataSource;

import mx.com.rodel.sps.db.common.CommonDataSource;
import mx.com.rodel.sps.db.common.SqlServiceNotFound;

public class DatabaseManager implements CommonDataSource{
	private CommonDataSource dataSource;
	
	public DatabaseManager(CommonDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public DataSource getDataSource() {
		return dataSource.getDataSource();
	}

	@Override
	public void connect() throws SqlServiceNotFound, SQLException {
		dataSource.connect();
		createTables();
	}

	@Override
	public void createTables() throws SQLException {
		dataSource.createTables();
	}
}
