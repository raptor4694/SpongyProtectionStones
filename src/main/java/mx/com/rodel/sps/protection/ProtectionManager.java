package mx.com.rodel.sps.protection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.config.IConfiguration;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class ProtectionManager extends IConfiguration{
	// It can be changed while players are placing stones
	private ConcurrentMap<String, ProtectionStone> stoneTypes = Maps.newConcurrentMap();
	
	private HashMap<Vector2i, List<Protection>> protectionByChunk = Maps.newHashMap();
	
	public ProtectionManager(SpongyPS pl) {
		super("stones.conf", pl);
	}
	
	public void loadStones(){
		stoneTypes.clear();
		Map<Object, ? extends CommentedConfigurationNode> childs = getNode("stones").getChildrenMap();

		for(Entry<Object, ? extends CommentedConfigurationNode> node : childs.entrySet()){
			try {
				ProtectionStone stone = getStone(node.getKey().toString(), node.getValue());
				if(stoneTypes.containsKey(stone.getBlockType())){
					pl.getLogger().warn("There are 2 or more protection stones with the same block type {}", stone.getBlockType().getId());
				}
				stoneTypes.put(node.getKey().toString(), stone);
			} catch (Exception e) {
				pl.getLogger().warn("Error loading protection stone {}:", node.getKey());
				e.printStackTrace();
			}
		}
		
	}
	
	public ImmutableList<ProtectionStone> getStones(){
		return ImmutableList.copyOf(stoneTypes.values());
	}
	
	public Optional<ProtectionStone> getStoneByBlock(BlockType blockType){
		for(Entry<String, ProtectionStone> stoneType : stoneTypes.entrySet()){
			if(stoneType.getValue().getBlockType().equals(blockType)){
				return Optional.of(stoneType.getValue());
			}
		}
		return Optional.empty();
	}
	
	private ProtectionStone getStone(String name, CommentedConfigurationNode node) throws Exception{
		int range = node.getNode("range").getInt();
		String displayName = node.getNode("display-name").getString();
		
		String stype = node.getNode("block-type").getString();
		Optional<BlockType> optional = Sponge.getRegistry().getType(BlockType.class, stype);
		BlockType type = optional.orElseThrow(() -> new Exception("Cannot found block id "+stype));
	
		return new ProtectionStone(name, type, range, displayName);
		
	}
	
	public static boolean createProtection(Player owner, Vector3i min, Vector3i max, Location<World> location, String protectionType){
		try {
			SpongyPS.getInstance().getDatabaseManger().createProtection(owner.getUniqueId(), owner.getName(), min, max, location, protectionType);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Optional<Protection> isRegion(Location<World> location){
		// First check chunk protections in cache
		int chunkX = location.getBlockX() >> 4;
		int chunkZ = location.getBlockZ() >> 4;
		List<Protection> protections = protectionByChunk.get(new Vector2i(chunkX, chunkZ));
		
		if(protections==null){
			return Optional.empty();
		}
		
		for(Protection protection : protections){
			// Then check if any the of protections envelops the location
			if(protection.envelops(location.getBlockPosition())){
				return Optional.of(protection);
			}
		}
		
		return Optional.empty();
	}

	public int loadProtections(World world) {
		List<Protection> protections = SpongyPS.getInstance().getDatabaseManger().searchProtections(world);
		
		for(Protection protection : protections){
			putProtection(protection);
		}
		
		return protections.size();
	}
	
	public void saveProtection(Protection protection) throws SQLException{
		 putProtection(protection);
		 SpongyPS.getInstance().getDatabaseManger().createProtection(
				 protection.getOwner(), protection.getOwnerName(), protection.getMin(), protection.getMax(), protection.getCenter(), protection.getType().getName());
	}
	
	private void putProtection(Protection protection){
		for(Vector2i chunk : protection.getParentChunks()){
			List<Protection> currentProtections = protectionByChunk.getOrDefault(chunk, new ArrayList<>());
			currentProtections.add(protection);
			protectionByChunk.put(chunk, currentProtections);
		}
	}
	
	public List<Protection> getProtectionsInChunk(Vector2i chunk){
		return protectionByChunk.getOrDefault(chunk, new ArrayList<>());
	}

	public int reload() {
		protectionByChunk.clear();
		int count = 0;
		for(World world : Sponge.getServer().getWorlds()){
			count += loadProtections(world);
		}
		return count;
	}

	public int chunks() {
		return protectionByChunk.size();
	}
}
