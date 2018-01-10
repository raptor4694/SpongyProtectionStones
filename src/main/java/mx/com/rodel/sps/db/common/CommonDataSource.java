package mx.com.rodel.sps.db.common;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;

import mx.com.rodel.sps.protection.Protection;

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

	
	abstract int countProtectionsOfType(UUID uuid, String name);
	
	abstract void createProtection(UUID owner, String owner_name, Vector3i min, Vector3i max, Location<World> location, String protectionType) throws SQLException;
	
	abstract Protection searchRegion(UUID world, int x, int y, int z);
	
	abstract List<Protection> searchProtections(World world);
	
	abstract void updatePlayerName(Player player);
	
	abstract void updateMembers(int id, Map<UUID, String> members);
	
	abstract void updateFlags(int id, String json);
	
	abstract Protection searchRegion(int id);
	
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
