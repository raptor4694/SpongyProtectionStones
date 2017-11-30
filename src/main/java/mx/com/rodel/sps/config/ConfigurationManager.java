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
	
	public CommentedConfigurationNode getNode(String key){
		return rootNode.getNode(key);
	}
	
	private void defaultValues(){
		registerDefault("storage.mysql.host", "localhost", null);
		// Fun fact: When i was developing the DB backend, i spend like 2 hours dealing with a "Cannot connect" error, until i see that the port its 3306 and not... 3006 #HumanMistakes... and then remember kids, mysql default port its 3306 THREE THOUSAND, THREE HUNDRED AND SIX!
		registerDefault("storage.mysql.port", 3306, null);
		registerDefault("storage.mysql.database", "mc", null);
		registerDefault("storage.mysql.username", "user", null);
		registerDefault("storage.mysql.password", "password", null);
		registerDefault("storage.mysql.protection_table", "protection_stones", null);
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