package mx.com.rodel.sps.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import mx.com.rodel.sps.SpongyPS;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class ConfigurationManager {
	private SpongyPS pl;
	
	// Key, Value, Comment
	private HoconConfigurationLoader loader;
	private CommentedConfigurationNode rootNode;
	
	public ConfigurationManager(SpongyPS pl) {
		this.pl = pl;
		
		loader = HoconConfigurationLoader.builder().setPath(pl.getConfigPath()).build();
		try {
			boolean create = createFile();
			if(create){
				URL defConf = SpongyPS.getInstance().getPluginContainer().getAsset("config.conf").orElseThrow(() -> new FileNotFoundException("config.conf not found in jar")).getUrl();
				HoconConfigurationLoader defloader = HoconConfigurationLoader.builder().setURL(defConf).build();
				CommentedConfigurationNode node = defloader.load();
				loader.save(node);
			}
			
		} catch (Exception e) {
			pl.getLogger().warn("Error initializing configuration: ");
			e.printStackTrace();
		}
	}
	
	public CommentedConfigurationNode getRoot(){
		return rootNode;
	}
	
	public CommentedConfigurationNode getNode(Object... key){
		return rootNode.getNode(key);
	}
	
	public void load(){
		try {
			rootNode = loader.load();
		} catch (IOException e) {
			pl.getLogger().warn("Error saving configuration: ");
			e.printStackTrace();
		}
	}
	
	private boolean createFile() throws IOException{
		File file = pl.getConfigPath().toFile();
		
		if(!file.exists()){
			file.getParentFile().mkdirs();
			file.createNewFile();
			return true;
		}
		
		return false;
	}
}