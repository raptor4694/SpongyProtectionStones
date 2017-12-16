package mx.com.rodel.sps.config;

import java.util.HashMap;
import java.util.Map.Entry;

public class LocaleFormat {
	protected String text;
	protected HashMap<String, Object> replacer = new HashMap<>();
	
	public LocaleFormat(String text) {
		this.text = text;
	}
	
	public LocaleFormat add(String key, String replacement){
		replacer.put(key, replacement);
		return this;
	}
	
	@Override
	public String toString() {
		String tempText = text;
		for(Entry<String, Object> replace : replacer.entrySet()){
			tempText = tempText.replace(replace.getKey(), replace.getValue().toString());
		}
		
		return tempText;
	}
}
