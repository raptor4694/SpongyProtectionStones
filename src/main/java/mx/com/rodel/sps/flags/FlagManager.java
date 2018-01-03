package mx.com.rodel.sps.flags;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import mx.com.rodel.sps.SpongyPS;

public class FlagManager {
	private Map<String, Object> registeredFlags = new HashMap<>();
	
	private SpongyPS pl;
	
	public FlagManager(SpongyPS pl) {
		this.pl = pl;
	}
	
	public void registerFlags(){
		registerFlag("prevent-build", true);
	}
	
	/**
	 * Register a flag, its must be lower case, and "-" separator (example: prevent-special-stuff)
	 * 
	 * @param flag
	 * @param defaultVal bool, string, int
	 */
	public void registerFlag(String flag, Object defaultVal){
		pl.getLogger().info("Flag {} (Default: {flag}) registered!", flag);
		registeredFlags.put(flag, defaultVal);
	}
	
	public ImmutableMap<String, Object> getFlags(){
		return ImmutableMap.copyOf(registeredFlags);
	}
}