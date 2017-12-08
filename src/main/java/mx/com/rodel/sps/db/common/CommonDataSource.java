package mx.com.rodel.sps.db.common;

import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

public interface CommonDataSource {
	/**
	 * Get {@link DataSource} wrapper
	 * 
	 * @return a DataSource depending of the engine used
	 * @throws SqlServiceNotFound 
	 * @throws SQLException 
	 */
	abstract DataSource getDataSource();
	
	/**
	 * Connect to table wrapper
	 * @throws SQLException 
	 * @throws SqlServiceNotFound 
	 */
	abstract void connect() throws SqlServiceNotFound, SQLException;

	/**
	 * Create table wrapper
	 * @throws SQLException 
	 */
	abstract void createTables() throws SQLException;

	/**
	 * Return the Sponge native SqlService
	 * 
	 * @return a new service (This don't save the service, please save it instead of create new each time)
	 * @throws SqlServiceNotFound
	 */
	public default SqlService getSqlService() throws SqlServiceNotFound{
		Optional<SqlService> oService = Sponge.getServiceManager().provide(SqlService.class);
		if(oService.isPresent()){
			return oService.get();
		}else{
			throw new SqlServiceNotFound();
		}
	}
}