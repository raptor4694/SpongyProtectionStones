package mx.com.rodel.sps.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;

import mx.com.rodel.sps.SpongyPS;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class IConfiguration {
	public String fileName = "";
	protected SpongyPS pl;
	
	// If true will put any missing node on loading
	protected boolean defaultNodes;
	
	private HoconConfigurationLoader confLoader;
	private CommentedConfigurationNode root;
	private File file; 
	
	private CommentedConfigurationNode defaultNode;
	
	public IConfiguration(String fileName, SpongyPS pl) {
		try {
			this.fileName = fileName;
			this.pl = pl;
			
			// Create the loader of the normal configuration file
			Path path = pl.getConfigDir().resolve(fileName);
			file = path.toFile();
			confLoader = HoconConfigurationLoader.builder().setPath(path).build();
			
			// Find and loadup default configuration file
			URL defConfURL = pl.getPluginContainer().getAsset(fileName).orElseThrow(()->new FileNotFoundException(fileName+" default configuration, not found in jar")).getUrl();
			HoconConfigurationLoader defLoader = HoconConfigurationLoader.builder().setURL(defConfURL).build();
			defaultNode = defLoader.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public CommentedConfigurationNode getRoot(){
		return root;
	}
	
	public CommentedConfigurationNode getNode(Object... args){
		return root.getNode(args);
	}
	
	public void saveDefault(){
		try {
			if(create()){
				save(defaultNode);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save(CommentedConfigurationNode node){
		try {
			confLoader.save(node);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void load(){
		try {
			saveDefault();
			
			root = confLoader.load();
			
			if(defaultNodes && setDefaultNodes()){
				save(root);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean setDefaultNodes(){
		boolean diff = false;
		
		Map<Object, ? extends CommentedConfigurationNode> childs = root.getChildrenMap();
		for(Entry<Object, ? extends CommentedConfigurationNode> child : defaultNode.getChildrenMap().entrySet()){
			if(!childs.containsKey(child.getKey())){
				root.getNode(child.getKey()).setValue(child.getValue());
				diff = true;
			}
		}
		
		// @TODO Recursive parent default node (Now this system don't detect childs on parent nodes) 
		
		return diff;
	}
	
	public boolean create() throws IOException{
		file.getParentFile().mkdirs();
		return file.createNewFile();
	}
}