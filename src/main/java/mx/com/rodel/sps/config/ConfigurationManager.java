package mx.com.rodel.sps.config;

import mx.com.rodel.sps.SpongyPS;

public class ConfigurationManager extends IConfiguration{
	public ConfigurationManager(SpongyPS pl) {
		super("config.conf", pl);
		defaultNodes = true;
	}
}