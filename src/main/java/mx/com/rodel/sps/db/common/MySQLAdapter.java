package mx.com.rodel.sps.db.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import mx.com.rodel.sps.protection.Protection;
import mx.com.rodel.sps.utils.Helper;
import mx.com.rodel.sps.utils.WorldNotFoundException;

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
	public HikariDataSource getDataSource() {
		return dataSource;
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
					+ "`owner_name` varchar(60) not null,"
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
					+ "`name` varchar(255) default null,"
					+ "primary key (`id`)"
					+ ") "
					+ "ENGINE=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;";
			conn.prepareStatement(query).execute();
		}
	}
	
	@Override
	public Protection searchRegion(UUID world, int x, int y, int z) {
		try (Connection conn = getDataSource().getConnection()){
			PreparedStatement ps = conn.prepareStatement("select * from "+protection_table+" where `world`=? and `minx`<=? and `miny`<=? and `minz`<=? and `maxx`>=? and `maxy`>=? and `maxz`>=?");
			int i = 1;
			ps.setString(i, world.toString()); i++;
			ps.setInt(i, x); i++;
			ps.setInt(i, y); i++;
			ps.setInt(i, z); i++;
			ps.setInt(i, x); i++;
			ps.setInt(i, y); i++;
			ps.setInt(i, z); i++;
			ResultSet rs = ps.executeQuery();
			Protection p = null;
			if(rs.next()){
				p = protectionWrapper(rs);
			}
			rs.close();
			ps.close();
			return p;
		} catch (SQLException | WorldNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Protection protectionWrapper(ResultSet rs) throws SQLException, WorldNotFoundException{
		String worldUID = rs.getString("world");
		World world = Sponge.getServer().getWorld(UUID.fromString(worldUID)).orElseThrow(()->new WorldNotFoundException(worldUID));
		
		String member = rs.getString("members");
		Map<UUID, String> members = new HashMap<>();
		if(member!=null){ // Idk, sonarlint told me...
			String[] entries = member.split(";");
			for(String entry : entries){
				String[] kv = entry.split("=");
				members.put(UUID.fromString(kv[0]), kv[1]);
			}
		}
		
		return new Protection(
				rs.getInt("id"),
				world, 
				UUID.fromString(rs.getString("owner")),
				rs.getString("owner_name"),
				new Vector3i(rs.getInt("centerx"), rs.getInt("centery"), rs.getInt("centerz")), 
				new Vector3i(rs.getInt("minx"), rs.getInt("miny"), rs.getInt("minz")), 
				new Vector3i(rs.getInt("maxx"), rs.getInt("maxy"), rs.getInt("maxz")), 
				members, 
				rs.getString("flags"),
				rs.getString("name"));
	}

	@Override
	public int countProtectionsOfType(UUID uuid, String name) {
		try (Connection conn = getDataSource().getConnection()){
			PreparedStatement ps = conn.prepareStatement("select count(*) from "+protection_table+" where `owner`=? and `type`=?");
			ps.setString(1, uuid.toString());
			ps.setString(2, name);
			ResultSet rs = ps.executeQuery();
			int c = 0;
			if(rs.next()){
				c = rs.getInt("count(*)");
			} 
			rs.close();
			ps.close();
			return c;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	@Override
	public List<Protection> searchProtections(World world) {
		List<Protection> protections = new ArrayList<>();
		try (Connection conn = getDataSource().getConnection()){
			PreparedStatement ps = conn.prepareStatement("select * from "+protection_table+" where `world`=?");
			ps.setString(1, world.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				protections.add(protectionWrapper(rs));
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return protections;
	}

	@Override
	public void updatePlayerName(Player player) {
		try (Connection conn = getDataSource().getConnection()) {
			PreparedStatement ps = conn.prepareStatement("update "+protection_table+" set `owner_name`=? where `owner`=? and `owner_name`!=?");
			ps.setString(1, player.getName());
			ps.setString(2, player.getUniqueId().toString());
			ps.setString(3, player.getName());
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void updateName(int id, String newname) {
		try(Connection conn = getDataSource().getConnection()) {
			PreparedStatement ps = conn.prepareStatement("update "+protection_table+" set `name`=? where `id`=?");
			ps.setString(1, newname);
			ps.setInt(2, id);
			ps.executeUpdate();
			ps.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void updateMembers(int id, Map<UUID, String> members) {
		try (Connection conn = getDataSource().getConnection()) {
			PreparedStatement ps = conn.prepareStatement("update "+protection_table+" set `members`=? where `id`=?");
			ps.setString(1, Helper.serializeMembers(members));
			ps.setInt(2, id);
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void updateFlags(int id, String json) {
		try (Connection conn = getDataSource().getConnection()) {
			PreparedStatement ps = conn.prepareStatement("update "+protection_table+" set `flags`=? where `id`=?");
			ps.setString(1, json);
			ps.setInt(2, id);
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateOwner(int id, UUID owner, String ownername) {
		try (Connection conn = getDataSource().getConnection()) {
			PreparedStatement ps = conn.prepareStatement("update "+protection_table+" set `owner`=?, `owner_name`=?, where `id`=?");
			ps.setString(1, owner.toString());
			ps.setString(2, ownername);
			ps.setInt(3, id);
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int createProtection(UUID owner, String owner_name, Vector3i min, Vector3i max, Location<World> location, String protectionType) throws SQLException {
		try (Connection conn = getDataSource().getConnection()) {
			String query = "insert into "+protection_table+" "
					+ "(`owner`, `owner_name`, `minx`, `miny`, `minz`, `maxx`, `maxy`, `maxz`, `centerx`, `centery`, `centerz`, `world`, `type`) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(query);
			int i = 1;
			//Owner
			ps.setString(i, owner.toString()); i++;
			ps.setString(i, owner_name); i++;
			//Min
			ps.setInt(i, min.getX()); i++;
			ps.setInt(i, min.getY()); i++;
			ps.setInt(i, min.getZ()); i++;
			//Max
			ps.setInt(i, max.getX()); i++;
			ps.setInt(i, max.getY()); i++;
			ps.setInt(i, max.getZ()); i++;
			//Center
			ps.setInt(i, location.getBlockX()); i++;
			ps.setInt(i, location.getBlockY()); i++;
			ps.setInt(i, location.getBlockZ()); i++;
			//World
			ps.setString(i, location.getExtent().getUniqueId().toString()); i++;
			//Type
			ps.setString(i, protectionType); i++;
			ps.executeUpdate();
			
			ResultSet rs = ps.getGeneratedKeys();
			if(rs.next()){
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	@Override
	public Protection searchRegion(int id) {
		try (Connection conn = getDataSource().getConnection()) {
			PreparedStatement ps = conn.prepareStatement("select * from "+protection_table+" where `id`=?");
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			Protection protection = null;
			if(rs.next()){
				protection = protectionWrapper(rs);
			}
			rs.close();
			ps.close();
			return protection;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void deleteProtection(int id) {
		try (Connection conn = getDataSource().getConnection()) {
			PreparedStatement ps = conn.prepareStatement("delete from "+protection_table+" where `id`=?");
			ps.setInt(1, id);
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
