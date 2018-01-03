package mx.com.rodel.sps.api;

import java.util.Optional;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.protection.Protection;

public class SPSApi {
	
	private SPSApi() {
		throw new IllegalStateException("Please use this class from static methods!");
	}
	
	public static Optional<Protection> getProtection(Location<World> location){
		return SpongyPS.getInstance().getProtectionManager().isRegion(location);
	}
	
	public static void registerFlag(String name, Object defaultValue){
		SpongyPS.getInstance().getFlagManager().registerFlag(name, defaultValue);
	}
}
