package mx.com.rodel.sps.flags;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
		registerFlag("welcome-message", "");
	}
	
	/**
	 * Register a flag, its must be lower case, and "-" separator (example: prevent-special-stuff)
	 * 
	 * @param flag
	 * @param defaultVal bool, string, int
	 */
	public void registerFlag(String flag, Object defaultVal){
		for(Types type : Types.values()){
			if(defaultVal.getClass().equals(type.type)){
				pl.getLogger().info("Flag {} (Default: {}) registered!", flag, defaultVal);
				registeredFlags.put(flag, defaultVal);
				return;
			}
		}
		
		throw new IllegalArgumentException("Error on register flag "+flag+" of type "+defaultVal.getClass().getName()+", you can only use "+Arrays.asList(Types.values()).stream().map(Types::name).collect(Collectors.joining(", "))+" for now!");
	}
	
	public ImmutableMap<String, Object> getFlags(){
		return ImmutableMap.copyOf(registeredFlags);
	}
	
	public enum Types{
		BOOLEAN(Boolean.class, "flag-noboolean"),
		INTEGER(Integer.class, "flag-noint"),
		STRING(String.class, "flag-nostring");
		
		public Class<?> type;
		public String message;
		
		public static Types getByClass(Class<?> type){
			for(Types t : values()){
				if(type.equals(t.type)){
					return t;
				}
			}
			
			return null;
		}
		
		private Types(Class<?> type, String message){
			this.type = type;
			this.message = message;
		}
	}
}
