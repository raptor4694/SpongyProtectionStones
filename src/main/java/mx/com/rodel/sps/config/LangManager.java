package mx.com.rodel.sps.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import mx.com.rodel.sps.SpongyPS;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class LangManager {
	private SpongyPS pl;
	private HoconConfigurationLoader loader;
	private CommentedConfigurationNode rootNode;
	private CommentedConfigurationNode rootDefault;
	
	public LangManager(SpongyPS pl){
		this.pl = pl;
		
		loader = HoconConfigurationLoader.builder().setPath(pl.getConfigDir().resolve("lang.conf")).build();
		try {
			boolean create = createFile();
			if(create){
				URL defConf = pl.getPluginContainer().getAsset("lang.conf").orElseThrow(()->new FileNotFoundException("lang.conf not found in jar")).getUrl();
				HoconConfigurationLoader defLoader = HoconConfigurationLoader.builder().setURL(defConf).build();
				rootDefault = defLoader.load();
				loader.save(rootDefault);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void load(){
		try {
			rootNode = loader.load();
			
			boolean diff = false;
			Map<Object, ? extends CommentedConfigurationNode> childs = rootNode.getChildrenMap();
			for(Entry<Object, ? extends CommentedConfigurationNode> key : rootDefault.getChildrenMap().entrySet()){
				if(!childs.containsKey(key.getKey())){
					rootNode.getNode(key.getKey()).setValue(key.getValue());
					diff = true;
				}
			}
			
			if(diff){
				loader.save(rootNode);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean createFile() throws IOException{
		File file = pl.getConfigDir().resolve("lang.conf").toFile();
		
		if(!file.exists()){
			file.getParentFile().mkdirs();
			file.createNewFile();
			return true;
		}
		
		return false;
	}
}
