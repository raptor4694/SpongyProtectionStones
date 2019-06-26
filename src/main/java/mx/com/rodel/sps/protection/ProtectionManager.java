package mx.com.rodel.sps.protection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockState.MatcherBuilder;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.block.trait.BooleanTrait;
import org.spongepowered.api.block.trait.EnumTrait;
import org.spongepowered.api.block.trait.IntegerTrait;
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
	
	private ConcurrentMap<UUID, ConcurrentMap<Vector2i, List<Protection>>> protectionByChunk = Maps.newConcurrentMap();
	private ConcurrentMap<UUID, Set<Protection>> protectionByOwner = Maps.newConcurrentMap();
	
	public ProtectionManager(SpongyPS pl) {
		super("stones.conf", pl);
	}
	
	public void loadStones(){
		stoneTypes.clear();
		Map<Object, ? extends CommentedConfigurationNode> childs = getNode("stones").getChildrenMap();

		for(Entry<Object, ? extends CommentedConfigurationNode> node : childs.entrySet()){
			try {
				String key = node.getKey().toString();
				ProtectionStone stone = getStone(node.getKey().toString(), node.getValue());
				if(stoneTypes.containsKey(key)){
					pl.getLogger().warn("There are 2 or more protection stones with the same id {}", key);
				}
				stoneTypes.put(key, stone);
			} catch(Exception e) {
				pl.getLogger().warn("Error loading protection stone {}:", node.getKey());
				e.printStackTrace();
			}
		}
		
	}
	
	public ImmutableList<ProtectionStone> getStones(){
		return ImmutableList.copyOf(stoneTypes.values());
	}
	
	@Deprecated
	public Optional<ProtectionStone> getStoneByBlock(BlockType blockType){
		for(ProtectionStone stoneType : stoneTypes.values()){
			if(stoneType.getBlockType().equals(blockType)){
				return Optional.of(stoneType);
			}
		}
		return Optional.empty();
	}
	
	public Optional<ProtectionStone> getStoneByBlockState(BlockState blockState) {
		for(ProtectionStone stoneType : stoneTypes.values()) {
			if(stoneType.getStateMatcher().matches(blockState)) {
				return Optional.of(stoneType);
			}
		}
		return Optional.empty();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ProtectionStone getStone(String name, CommentedConfigurationNode node) throws Exception{
		int range = node.getNode("range").getInt();
		String displayName = node.getNode("display-name").getString();
		
		String stype = node.getNode("block-type").getString();
		Optional<BlockType> optional = Sponge.getRegistry().getType(BlockType.class, stype);
		BlockType type = optional.orElseThrow(() -> new Exception("Cannot find block id "+stype));
	
		BlockState stateTemplate = type.getDefaultState();
		MatcherBuilder matcher = BlockState.matcher(type);
		
		for(Map.Entry<Object, ? extends CommentedConfigurationNode> entry : node.getNode("block-state").getChildrenMap().entrySet()) {
			if(entry.getKey() instanceof String) {
				String key = (String)entry.getKey();
				BlockTrait<?> trait = type.getTrait(key).orElseThrow(() -> new Exception("Cannot find " + type.getId() + " block trait " + key));
				if(trait instanceof BooleanTrait) {
					BooleanTrait booleanTrait = (BooleanTrait)trait;
					boolean value = entry.getValue().getBoolean();
					matcher.trait(booleanTrait, value);
					stateTemplate = stateTemplate.withTrait(booleanTrait, value).get();
				} else if(trait instanceof EnumTrait) {
					EnumTrait enumTrait = (EnumTrait)trait;
					Enum value = Enum.valueOf((Class<? extends Enum>)enumTrait.getValueClass(), entry.getValue().getString());
					matcher.trait(enumTrait, value);
					stateTemplate = stateTemplate.withTrait(enumTrait, value).get();
				} else if(trait instanceof IntegerTrait) {
					IntegerTrait intTrait = (IntegerTrait)trait;
					int value = entry.getValue().getInt();
					matcher.trait(intTrait, value);
					stateTemplate = stateTemplate.withTrait(intTrait, value).get();
				} else {
					throw new Exception("Unsupported trait type " + trait.getClass().getName() + " for " + type.getId() + " block trait " + key);
				}
			}
		}
		
		return new ProtectionStone(name, stateTemplate, matcher.build(), range, displayName);
		
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
		List<Protection> protections = protectionByChunk.get(location.getExtent().getUniqueId()).get(new Vector2i(chunkX, chunkZ));
		
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
		protectionByChunk.put(world.getUniqueId(), Maps.newConcurrentMap());
		
		for(Protection protection : protections){
			putProtection(protection, world);
		}
		
		return protections.size();
	}
	
	public void deleteProtection(Protection protection){
		ConcurrentMap<Vector2i, List<Protection>> currentWorld = protectionByChunk.get(protection.getCenter().getExtent().getUniqueId());
		if(currentWorld!=null){
			for(Vector2i chunk : protection.getParentChunks()){
				currentWorld.get(chunk).remove(protection);
			}
		}
		protectionByOwner.get(protection.getOwner()).remove(protection);
		SpongyPS.getInstance().getDatabaseManger().deleteProtection(protection.getID());
	}
	
	public void saveProtection(Protection protection) throws SQLException{
		 putProtection(protection, protection.getCenter().getExtent());
		 protection.setID(SpongyPS.getInstance().getDatabaseManger().createProtection(
				 protection.getOwner(), protection.getOwnerName(), protection.getMin(), protection.getMax(), protection.getCenter(), protection.getType().getName()));
	}
	
	private void putProtection(Protection protection, World world){
		ConcurrentMap<Vector2i, List<Protection>> currentWorld = protectionByChunk.get(world.getUniqueId());
		for(Vector2i chunk : protection.getParentChunks()){
			List<Protection> currentProtections = currentWorld.computeIfAbsent(chunk, key -> new ArrayList<>());
			currentProtections.add(protection);
		}
		protectionByOwner.computeIfAbsent(protection.getOwner(), key -> new HashSet<>())
			.add(protection);
	}
	
	public List<Protection> getProtectionsInChunk(UUID worldID, Vector2i chunk){
		return protectionByChunk.get(worldID).getOrDefault(chunk, new ArrayList<>());
	}	
	
	public Set<Protection> getProtectionsOwnedBy(Player player) {
		return getProtectionsOwnedBy(player.getUniqueId());
	}
	
	public Set<Protection> getProtectionsOwnedBy(UUID ownerUuid) {
		return Collections.unmodifiableSet(protectionByOwner.computeIfAbsent(ownerUuid, key -> new HashSet<>()));
	}

	public int reload() {
		protectionByChunk.clear();
		protectionByOwner.clear();
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
