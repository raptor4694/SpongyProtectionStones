package mx.com.rodel.sps.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.utils.Helper;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigurationManager {
	private SpongyPS pl;
	
	// Key, Value, Comment
	private HashMap<String, Entry<Object, Optional<String>>> values = new HashMap<>();
	private ConfigurationLoader<CommentedConfigurationNode> loader;
	private CommentedConfigurationNode rootNode;
	
	public ConfigurationManager(SpongyPS pl) {
		this.pl = pl;
		
		try {
			createFile();
			loader = HoconConfigurationLoader.builder().setPath(pl.getConfigPath()).build();
			rootNode = loader.createEmptyNode(ConfigurationOptions.defaults());
			defaultValues();
		} catch (Exception e) {
			pl.getLogger().warn("Error initializing configuration: {}", e.getMessage());
		}
	}
	
	public CommentedConfigurationNode getRoot(){
		return rootNode;
	}
	
	private void defaultValues(){
		registerDefault("mysql.host", "localhost", null);
		registerDefault("mysql.port", 3006, null);
		registerDefault("mysql.database", "mc", null);
		registerDefault("mysql.username", "user", null);
		registerDefault("mysql.password", "password", null);
	}
	
	private void registerDefault(String key, Object value, String comment){
		values.put(key, Helper.entry(value, Optional.ofNullable(comment)));
	}
	
	public void load(){
		try {
			loader.load();
		} catch (IOException e) {
			pl.getLogger().warn("Error saving configuratoin: {}", e.getMessage());
		}
	}
	
	public void save(){
		boolean changed = false;
		for(Entry<String, Entry<Object, Optional<String>>> value : values.entrySet()){
			CommentedConfigurationNode node = rootNode.getNode(value.getKey());
			if(node.getValue()==null){
				changed = true;
				node.setValue(value.getValue().getKey());
				if(value.getValue().getValue().isPresent()){
					node.setComment(value.getValue().getValue().get());
				}
			}
		}
		
		if(!changed){
			return;
		}
		
		try {
			loader.save(rootNode);
		} catch (IOException e) {
			pl.getLogger().warn("Error saving configuration: {}", e.getMessage());
		}
	}
	
	private void createFile() throws IOException{
		File file = pl.getConfigPath().toFile();
		
		if(!file.exists()){
			file.getParentFile().mkdirs();
			file.createNewFile();
			save();
		}
	}
}