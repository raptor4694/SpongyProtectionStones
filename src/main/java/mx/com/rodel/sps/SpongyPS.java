package mx.com.rodel.sps;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

import mx.com.rodel.sps.config.ConfigurationManager;

@Plugin(id = "spongyps", name = "Spongy Protection Stones", version = "1.0", description = "A basic Protection Stones port to Sponge")
public class SpongyPS {
	private static SpongyPS instance;
	public static SpongyPS getInstance(){
		return instance;
	}
	
	@Inject
	@DefaultConfig(sharedRoot = false)
	private Path configPath;
	public Path getConfigPath(){
		return configPath;
	}
	
	@Inject
	private Logger log;
	public Logger getLogger(){
		return log;
	}
	
	private ConfigurationManager configManager;
	
	@Listener
	public void onPreInit(GamePreInitializationEvent e){
		instance = this;
		
		log.info("Initializing Spongy Protection Stones!");
		
		configManager = new ConfigurationManager(this);
		configManager.save();
		configManager.load();
		//TODO Connect to DB
	}
}
