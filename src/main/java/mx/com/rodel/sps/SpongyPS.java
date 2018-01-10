package mx.com.rodel.sps;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import com.google.inject.Inject;

import mx.com.rodel.sps.command.SPSCommand;
import mx.com.rodel.sps.config.ConfigurationManager;
import mx.com.rodel.sps.config.LangManager;
import mx.com.rodel.sps.db.DatabaseManager;
import mx.com.rodel.sps.db.common.MySQLAdapter;
import mx.com.rodel.sps.flags.FlagManager;
import mx.com.rodel.sps.limits.LimitsManager;
import mx.com.rodel.sps.listener.BlockListener;
import mx.com.rodel.sps.listener.PlayerListener;
import mx.com.rodel.sps.listener.WorldListener;
import mx.com.rodel.sps.protection.ProtectionManager;
import mx.com.rodel.sps.utils.Helper;

@Plugin(id = "spongyps", name = "Spongy Protection Stones", version = Info.VERSION, description = "A basic Protection Stones port to Sponge")
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
	@ConfigDir(sharedRoot = false)
	private Path configDir;
	public Path getConfigDir(){
		return configDir;
	}
	
	@Inject
	private Logger log;
	public Logger getLogger(){
		return log;
	}
	
	@Inject
	private PluginContainer container;
	public PluginContainer getPluginContainer(){
		return container;
	}
	
	private ConfigurationManager configManager;
	public ConfigurationManager getConfigManger(){
		return configManager;
	}
	private DatabaseManager databaseManager;
	public DatabaseManager getDatabaseManger(){
		return databaseManager;
	}
	
	private ProtectionManager protectionManager;
	public ProtectionManager getProtectionManager(){
		return protectionManager;
	}
	
	private LangManager langManager;
	public LangManager getLangManager(){
		return langManager;
	}
	
	private LimitsManager limitsManager;
	public LimitsManager getLimitsManager(){
		return limitsManager;
	}
	
	private FlagManager flagManager;
	public FlagManager getFlagManager() {
		return flagManager;
	}
	
	@Listener
	public void onGameReloadEvent(GameReloadEvent e){
		reload(Sponge.getServer().getConsole());
	}
	
	public void reload(CommandSource cause){
		// Configuration
		if(configManager==null){
			configManager = new ConfigurationManager(this);
		}
		configManager.load();
		
		
		// Localization
		if(langManager==null){
			langManager = new LangManager(this);
		}
		langManager.load();
		
		// Flags
		if(flagManager==null){
			flagManager = new FlagManager(this);
		}
		flagManager.registerFlags();
		
		// Stones
		if(protectionManager==null){
			protectionManager = new ProtectionManager(this);
		}
		protectionManager.load();
		protectionManager.loadStones();

		// Limits
		if(limitsManager==null){
			limitsManager = new LimitsManager(this);
		}
		limitsManager.load();
		limitsManager.loadLimits();
		
		cause.sendMessage(Helper.chatColor("&aSPS Configurations Loaded"));
	}
	
	@Listener
	public void onGamePreInitializationEvent(GamePreInitializationEvent e){
		instance = this;
		
		log.info("Initializing Spongy Protection Stones!");
		
		reload(Sponge.getServer().getConsole());

		// Init DB
		try {
			// Currently only mysql support
			databaseManager = new DatabaseManager(new MySQLAdapter(
					configManager.getRoot().getNode("storage", "mysql", "host").getString(), 
					configManager.getRoot().getNode("storage", "mysql", "port").getInt(),
					configManager.getRoot().getNode("storage", "mysql", "database").getString(), 
					configManager.getRoot().getNode("storage", "mysql", "username").getString(), 
					configManager.getRoot().getNode("storage", "mysql", "password").getString(), 
					configManager.getRoot().getNode("storage", "mysql", "protection-table").getString()));
			databaseManager.connect();
		} catch (Exception e2) {
			log.error("Error connecting to db:");
			e2.printStackTrace();
		}

		
		SPSCommand command = new SPSCommand(this);
		
		Sponge.getCommandManager().register(this, command, "ps", "sps");
		
		Sponge.getEventManager().registerListeners(this, new BlockListener());
		Sponge.getEventManager().registerListeners(this, new WorldListener());
		Sponge.getEventManager().registerListeners(this, new PlayerListener());
	}
}
