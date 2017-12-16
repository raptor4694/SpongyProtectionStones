package mx.com.rodel.sps.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.api.text.Text;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.utils.Helper;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class LangManager {
	private SpongyPS pl;
	private HoconConfigurationLoader loader;
	private CommentedConfigurationNode rootNode;
	private CommentedConfigurationNode rootDefault;
	private String header;
	
	public LangManager(SpongyPS pl){
		this.pl = pl;
		
		try {
			loader = HoconConfigurationLoader.builder().setPath(pl.getConfigDir().resolve("lang.conf")).build();
			URL defConf = pl.getPluginContainer().getAsset("lang.conf").orElseThrow(()->new FileNotFoundException("lang.conf not found in jar")).getUrl();
			HoconConfigurationLoader defLoader = HoconConfigurationLoader.builder().setURL(defConf).build();
			rootDefault = defLoader.load();
			
			boolean create = createFile();
			if(create){
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
			
			header = rootNode.getNode("header").getString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String localize(String key){
		return SpongyPS.getInstance().getLangManager().rootNode.getNode(key).getString();
	}
	
	public static String formatString(LocaleFormat format){
		return format.toString();
	}
	
	public static Text translate(String key){
		return Helper.chatColor(SpongyPS.getInstance().getLangManager().header+localize(key));
	}
	
	public static Text translate(LocaleFormat format){
		return Helper.chatColor(SpongyPS.getInstance().getLangManager().header+format.toString());
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
