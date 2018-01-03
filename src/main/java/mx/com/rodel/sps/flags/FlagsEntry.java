package mx.com.rodel.sps.flags;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class FlagsEntry {
	private Map<String, Object> flags = new HashMap<>();

	public FlagsEntry() {
		setDefault();
	}
	
	public void setDefault(){
		flags.put("prevent-build", true);
	}
	
	public ImmutableMap<String, Object> getFlags(){
		return ImmutableMap.copyOf(flags);
	}
	
	public String serialize(){
		JsonObject obj = new JsonObject();
		for(Entry<String, Object> flag : flags.entrySet()){
			String k = flag.getKey();
			Object o = flag.getValue();
			if(o instanceof Boolean){
				obj.addProperty(k, (Boolean) o);
			}else if(o instanceof Number){
				obj.addProperty(k, (Number) o);
			}else if(o instanceof String){
				obj.addProperty(k, (String) o);
			}else if(o instanceof Character){
				obj.addProperty(k, (Character) o);
			}
		}
		
		return obj.toString();
	}
	
	public static FlagsEntry deserialize(String json){
		FlagsEntry out = new FlagsEntry();
		
		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(json).getAsJsonObject();
		for(Entry<String, JsonElement> e : obj.entrySet()){
			System.out.println(e.getKey()+" "+e.getValue());
			
			String key = e.getKey();
			
			JsonElement el = e.getValue();
			if(el.isJsonPrimitive()){
				JsonPrimitive primitive = el.getAsJsonPrimitive();
				if(primitive.isBoolean()){
					out.flags.put(key, primitive.getAsBoolean());
				}else if(primitive.isNumber()){
					out.flags.put(key, primitive.getAsNumber());
				}else if(primitive.isString()){
					out.flags.put(key, primitive.getAsString());
				}
				
				System.out.println(out.flags.get(key)+" "+out.flags.get(key).getClass().getName());
			}
		}
		
		return out;
	}
}
