package mx.com.rodel.sps.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;

import mx.com.rodel.sps.db.common.CommonDataSource;
import mx.com.rodel.sps.db.common.SqlServiceNotFound;
import mx.com.rodel.sps.protection.Protection;

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
	}

	@Override
	public void createTables() throws SQLException {
		dataSource.createTables();
	}
	
	@Override
	public int countProtectionsOfType(UUID uuid, String name) {
		return dataSource.countProtectionsOfType(uuid, name);
	}

	@Override
	public int createProtection(UUID owner, String owner_name, Vector3i min, Vector3i max, Location<World> location, String protectionType) throws SQLException {
		return dataSource.createProtection(owner, owner_name, min, max, location, protectionType);
	}
	
	@Override
	public Protection searchRegion(UUID world, int x, int y, int z) {
		return dataSource.searchRegion(world, x, y, z);
	}
	
	@Override
	public List<Protection> searchProtections(World world) {
		return dataSource.searchProtections(world);
	}
	
	@Override
	public void updatePlayerName(Player player) {
		dataSource.updatePlayerName(player);
	}
	
	@Override
	public void updateMembers(int id, Map<UUID, String> members) {
		dataSource.updateMembers(id, members);
	}

	@Override
	public void updateOwner(int id, UUID owner, String ownername) {
		dataSource.updateOwner(id, owner, ownername);
	}
	
	@Override
	public void updateFlags(int id, String json){
		dataSource.updateFlags(id, json);
	}
	
	@Override
	public Protection searchRegion(int id) {
		return dataSource.searchRegion(id);
	}

	@Override
	public void deleteProtection(int id) {
		dataSource.deleteProtection(id);
	}
}
