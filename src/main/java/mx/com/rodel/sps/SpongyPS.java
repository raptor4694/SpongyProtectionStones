package mx.com.rodel.sps;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

import mx.com.rodel.sps.config.ConfigurationManager;
import mx.com.rodel.sps.db.DatabaseManager;
import mx.com.rodel.sps.db.common.MySQLAdapter;

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
	private DatabaseManager databaseManager;
	
	@Listener
	public void onPreInit(GamePreInitializationEvent e){
		instance = this;
		
		log.info("Initializing Spongy Protection Stones!");
		
		configManager = new ConfigurationManager(this);
		configManager.save();
		configManager.load();

		// Init DB
		try {
			// Currently only mysql support
			databaseManager = new DatabaseManager(new MySQLAdapter(configManager.getNode("storage.mysql.host").getString(), configManager.getNode("storage.mysql.port").getInt(), configManager.getNode("storage.mysql.database").getString(), configManager.getNode("storage.mysql.username").getString(), configManager.getNode("storage.mysql.password").getString(), configManager.getNode("storage.mysql.protection_table").getString()));
			databaseManager.connect();
		} catch (Exception e2) {
			log.error("Error connecting to db:");
			e2.printStackTrace();
		}
	}
}
