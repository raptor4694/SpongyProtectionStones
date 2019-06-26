package mx.com.rodel.sps.config;

import mx.com.rodel.sps.SpongyPS;

public class ConfigurationManager extends IConfiguration{
	public ConfigurationManager(SpongyPS pl) {
		super("config.conf", pl);
		defaultNodes = true;
	}
	
	public String getVisualizeBlock() {
		return getNode("config", "visualize-block").getString();
	}
	
	public long getVisualizeTime() {
		return getNode("config", "visualize-time").getLong();
	}
	
	public boolean canMembersBreakProtectionStone() {
		return getNode("config", "members-can-break-protection-stone").getBoolean();
	}
	
	public boolean areContainersProtected() {
		return getNode("config", "protect-containers").getBoolean();
	}
	
	public boolean areEntitiesProtected() {
		return getNode("config", "protect-entities").getBoolean();
	}
}