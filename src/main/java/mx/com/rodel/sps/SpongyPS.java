package mx.com.rodel.sps;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import com.google.inject.Inject;

import mx.com.rodel.sps.command.SPSCommand;
import mx.com.rodel.sps.config.ConfigurationManager;
import mx.com.rodel.sps.config.LangManager;
import mx.com.rodel.sps.db.DatabaseManager;
import mx.com.rodel.sps.db.common.MySQLAdapter;
import mx.com.rodel.sps.limits.LimitsManager;
import mx.com.rodel.sps.listener.ProtectionPlaceEvent;
import mx.com.rodel.sps.listener.WorldListener;
import mx.com.rodel.sps.protection.ProtectionManager;
import mx.com.rodel.sps.utils.Helper;

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
	
	@Listener
	public void onGameReloadEvent(GameReloadEvent e){
		reload(Sponge.getServer().getConsole());
	}
	
	public void reload(CommandSource cause){
		configManager.load();
		langManager.load();
		cause.sendMessage(Helper.chatColor("&aSPS Reloaded"));
	}
	
	@Listener
	public void onGamePreInitializationEvent(GamePreInitializationEvent e){
		instance = this;
		
		log.info("Initializing Spongy Protection Stones!");
		
		configManager = new ConfigurationManager(this);
		configManager.load();
		
		langManager = new LangManager(this);
		langManager.load();
		
		protectionManager = new ProtectionManager(this);
		protectionManager.loadStones();

		limitsManager = new LimitsManager();
		limitsManager.loadLimits();

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
		
		CommandSpec psCommand = CommandSpec.builder()
				.description(Text.of("Main SPS command"))
				.executor(command)
				.arguments(
							GenericArguments.choices(Text.of("subcommand"), command.commandChoices)
						)
				.build();
		
		Sponge.getCommandManager().register(this, psCommand, "ps", "sps");
		Sponge.getEventManager().registerListeners(this, new ProtectionPlaceEvent());
		Sponge.getEventManager().registerListeners(this, new WorldListener());
	}
}
