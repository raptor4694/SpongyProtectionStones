package mx.com.rodel.sps.protection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Objects;

public class Protection {
	private int id;
	private UUID owner;
	private List<UUID> members = new ArrayList<>();
	private List<String> flags = new ArrayList<>();
	private World world;
	private Vector3i center, min, max;
	
	public Protection(int id) {
		this.id = id;
	}
	
	public Protection(World world, UUID owner, Vector3i center, Vector3i min, Vector3i max, List<UUID> members, List<String> flags) {
		this.world = world;
		this.owner = owner;
		this.center = center;
		this.min = min;
		this.max = max;
		this.members = members;
		this.flags = flags;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("owner", owner)
				.add("members", Arrays.deepToString(members.toArray(new UUID[] {})))
				.add("flags", Arrays.deepToString(flags.toArray(new String[] {})))
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
	}
	
	public void resetVisualize(Player player){
		
	}
	
	public Protection setOwner(UUID owner){
		this.owner = owner;
		return this;
	}
	
	public UUID getOwner(){
		return owner;
	}
	
	public List<UUID> getMembers(){
		return members;
	}
	
	public int getID(){
		return id;
	}
}
