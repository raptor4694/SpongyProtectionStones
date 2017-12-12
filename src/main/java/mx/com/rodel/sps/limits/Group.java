package mx.com.rodel.sps.limits;

import java.util.HashMap;
import java.util.stream.Collectors;

import org.spongepowered.api.text.Text;

import mx.com.rodel.sps.utils.Helper;

public class Group {
	private String name;
	HashMap<String, Integer> protections = new HashMap<>();
	
	public Group() {
	}
	
	public Group(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public int getLimit(String name){
		return protections.getOrDefault(name, -1);
	}
	
	public void addLimit(String stone, int amount){
		protections.put(stone, amount);
	}
	
	@Override
	public String toString() {
		return name+"\n>> "+(protections.entrySet().stream().map(entry -> entry.getKey()+": "+entry.getValue()).collect(Collectors.joining("\n>>")));
	}
	
	public Text toText() {
		return Helper.chatColor("&6"+name+"\n&c>>"+(protections.entrySet().stream().map(entry -> entry.getKey()+"="+entry.getValue()).collect(Collectors.joining("\n&c>>"))));
	}
}
