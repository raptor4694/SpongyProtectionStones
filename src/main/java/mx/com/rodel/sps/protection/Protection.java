package mx.com.rodel.sps.protection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;

public class Protection {
	private int id;
	private UUID owner;
	private List<UUID> members = new ArrayList<>();
	private World world;
	private Vector3d center, pos1, pos2;
	
	public Protection(int id) {
		this.id = id;
	}
	
	public Protection(World world, Vector3d center, Vector3d pos1, Vector3d pos2) {
		this.world = world;
		this.center = center;
		this.pos1 = pos1;
		this.pos2 = pos2;
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
