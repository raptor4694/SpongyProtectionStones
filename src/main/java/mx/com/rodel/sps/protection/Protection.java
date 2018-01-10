package mx.com.rodel.sps.protection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.flags.FlagsEntry;
import mx.com.rodel.sps.utils.Helper;

public class Protection {
	private int id;
	private UUID owner;
	private String owner_name;
	private Map<UUID, String> members = new HashMap<>();
	private World world;
	private Vector3i center, min, max;
	private ProtectionStone type;
	private FlagsEntry flags = new FlagsEntry();
	
	public Protection(int id) {
		this.id = id;
	}
	
	public Protection(Player owner, World world, Vector3i center, ProtectionStone type) {
		this(owner.getUniqueId(), owner.getName(), world, center, type);
	}
	
	public Protection(UUID owner, String owner_name, World world, Vector3i center, ProtectionStone type) {
		this.owner = owner;
		this.owner_name = owner_name;
		this.center = center;
		this.world = world;
		this.type = type;
		
		Vector3i[] vertices = Helper.get2Vertices(type.getRange()/2, center);
		min = vertices[0];
		max = vertices[1];
	}
	
	public Protection(int id, World world, UUID owner, String owner_name, Vector3i center, Vector3i min, Vector3i max, Map<UUID, String> members, String flags) {
		this.id = id;
		this.world = world;
		this.owner = owner;
		this.owner_name = owner_name;
		this.center = center;
		this.min = min;
		this.max = max;
		this.members = members;
		this.flags = flags==null || flags.isEmpty() ? this.flags : FlagsEntry.deserialize(flags);
	}
	
	public Protection(int id, World world, Player owner, Vector3i center, Vector3i min, Vector3i max, Map<UUID, String> members, String flags) {
		this(id, world, owner.getUniqueId(), owner.getName(), center, min, max, members, flags);
	}
	
	public HashSet<Vector2i> getParentChunks(){
		HashSet<Vector2i> chunks = new HashSet<>();
		
		for (int x = min.getX(); x < max.getX(); x++) {
			for (int z = min.getZ(); z < max.getZ(); z++) {
				chunks.add(new Vector2i(x >> 4, z >> 4));
			}
		}
		
		return chunks;
	}
	
	public boolean intersects(Protection protection){
		if(!world.getUniqueId().equals(protection.world.getUniqueId())){ // @Fact: SonarLint save my life here
			return false;
		}
		
		Vector3i[] corners = protection.getCorners();
		
		// Check if any corner of the other protection its inside current and vice versa 
		
		for(Vector3i corner : corners){
			if(envelops(corner)){
				return true;
			}
		}
		
		corners = getCorners();
		
		for(Vector3i corner : corners){
			if(protection.envelops(corner)){
				return true;
			}
		}
		
		return false;
	}
	
	public Vector3i[] getCorners(){
		Vector3i[] corners = new Vector3i[8];
		
		corners[0] = new Vector3i(min.getX(), min.getY(), min.getZ());
		corners[1] = new Vector3i(min.getX(), min.getY(), max.getZ());
		corners[2] = new Vector3i(min.getX(), max.getY(), min.getZ());
		corners[3] = new Vector3i(min.getX(), max.getY(), max.getZ());
		corners[4] = new Vector3i(max.getX(), min.getY(), min.getZ());
		corners[5] = new Vector3i(max.getX(), min.getY(), max.getZ());
		corners[6] = new Vector3i(max.getX(), max.getY(), min.getZ());
		corners[7] = new Vector3i(max.getX(), max.getY(), max.getZ());
		
		return corners;
	}
	
	public boolean envelops(Vector3i vec){
		int x = vec.getX();
		int y = vec.getY();
		int z = vec.getZ();
		
		return x >= min.getX() && x <= max.getX() && y >= min.getY() && y <= max.getY() && z >= min.getZ() && z <= max.getZ();
	}
	
	public boolean hasPermission(UUID uuid){
		return owner.equals(uuid) || members.containsKey(uuid);
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("owner", owner)
				.add("members", Joiner.on(";").withKeyValueSeparator("=").join(members))
				.add("flags", flags.serialize())
				.add("world", world)
				.add("center", center)
				.add("min", min)
				.add("max", max)
				.toString();
	}
	
	/**
	 * This method sends ghost blocks to show the region limits
	 * 
	 * @param player
	 */
	public void visualize(Player player){
		try {
			for(Vector3i vector : getLineBounds()){
				player.sendBlockChange(vector, BlockState.builder().blockType(Sponge.getRegistry().getType(BlockType.class, SpongyPS.getInstance().getConfigManger().getNode("config", "visualize-block").getString()).orElseThrow(()->new Exception("Invalid visualize-block id!"))).build());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Sponge.getScheduler().createTaskBuilder().execute(()->resetVisualize(player)).delay(SpongyPS.getInstance().getConfigManger().getNode("config", "visualize-time").getLong(), TimeUnit.SECONDS).submit(SpongyPS.getInstance());
	}
	
	public void resetVisualize(Player player){
		getLineBounds().forEach(vector -> {
			player.resetBlockChange(vector);
		});
	}
	
	public ImmutableList<Vector3i> getLineBounds(){
		List<Vector3i> bounds = new ArrayList<>();
		
		for (int x = min.getX(); x <= max.getX(); x++) {
			for (int y = min.getY(); y <= max.getY(); y++) {
				for (int z = min.getZ(); z <= max.getZ(); z++) {
					// Upper Lower
					if(y==min.getY() || y==max.getY()){
						if((x==min.getX() || x==max.getX()) || (z==min.getZ() || z==max.getZ())){
							bounds.add(new Vector3i(x, y, z));
						}
						// Sides
					}else{
						if((x==max.getX() && z==max.getZ()) || (x==min.getX() && z==max.getZ()) || (x==min.getX() && z==min.getZ()) || (x==max.getX() && z==min.getZ())){
							bounds.add(new Vector3i(x, y, z));
						}
					}
				}
			}
		}
		
		return ImmutableList.copyOf(bounds);
	}
	
	public Protection setOwner(UUID owner, String owner_name){
		this.owner = owner;
		this.owner_name = owner_name;
		return this;
	}
	
	public UUID getOwner(){
		return owner;
	}
	
	public String getOwnerName(){
		return owner_name;
	}
	
	public Map<UUID, String> getMembers(){
		return members;
	}
	
	public void addMember(UUID member, String memberName){
		// @Fact
		// Its needed just to do it in this class, cause when we load the protections
		// we create a new protection and then place it on the chunks
		// we are not cloning or making more protections, just using the same copy all the time
		// then when i update the members variable, this updates in all chunks on the "protectionsByChunk" in ProtectionManager
		// it took me a lot of time to think about it, but after test it works perfectly ;)
		// @Edit
		// These days i learn a lot of c++, java by default uses pointer, thats why this happen
		
		members.put(member, memberName);
		SpongyPS.getInstance().getDatabaseManger().updateMembers(id, members);
	}
	
	public void removeMember(UUID member) {
		members.remove(member);
		SpongyPS.getInstance().getDatabaseManger().updateMembers(id, members);
	}
	
	public FlagsEntry getFlags(){
		return flags;
	}
	
	@SuppressWarnings("unchecked")
	public <T> Optional<T> getFlag(String name, Class<T> type){
		Object o = flags.getFullFlags().get(name);
		return o==null ? Optional.empty() : Optional.of((T) o);
	}
	
	public void setFlag(String name, Object value){
		flags.setFlag(name, value);
		SpongyPS.getInstance().getDatabaseManger().updateFlags(id, flags.serialize());
	}
	
	public int getID(){
		return id;
	}
	
	public Vector3i getMin(){
		return min;
	}
	
	public Vector3i getMax(){
		return max;
	}

	public Location<World> getCenter() {
		return new Location<>(world, center);
	}

	public ProtectionStone getType() {
		return type;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Protection && ((Protection) obj).id == id){
			return true;
		}
		return false;
	}

}
