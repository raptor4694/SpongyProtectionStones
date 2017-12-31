package mx.com.rodel.sps.config;

import org.spongepowered.api.text.Text;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.utils.Helper;

public class LangManager extends IConfiguration{
	
	public LangManager(SpongyPS pl) {
		super("lang.conf", pl);
		defaultNodes = true;
	}
	
	public String localize(String key){
		return getNode(key).getString();
	}
	
	public String formatString(LocaleFormat format){
		return format.toString();
	}
	
	public Text translate(String key, boolean header){
		return Helper.chatColor((header ? getHeader() : "")+localize(key));
	}
	
	public Text translate(LocaleFormat format, boolean header){
		format.text = localize(format.text); // Localize the text before use the replacers
		return Helper.chatColor((header ? getHeader() : "")+format.toString());
	}
	
	public String getHeader(){
		return getNode("header").getString();
	}
}
