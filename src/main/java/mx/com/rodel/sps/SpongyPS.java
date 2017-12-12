package mx.com.rodel.sps;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import com.google.inject.Inject;

import mx.com.rodel.sps.command.SPSCommand;
import mx.com.rodel.sps.config.ConfigurationManager;
import mx.com.rodel.sps.db.DatabaseManager;
import mx.com.rodel.sps.db.common.MySQLAdapter;
import mx.com.rodel.sps.limits.LimitsManager;
import mx.com.rodel.sps.listener.ProtectionPlaceEvent;
import mx.com.rodel.sps.protection.ProtectionManager;

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
	private ProtectionManager protectionManager;
	public ProtectionManager getProtectionManager(){
		return protectionManager;
	}
	
	private LimitsManager limitsManager;
	public LimitsManager getLimitsManager(){
		return limitsManager;
	}
	
	@Listener
	public void onPreInit(GamePreInitializationEvent e){
		instance = this;
		
		log.info("Initializing Spongy Protection Stones!");
		
		configManager = new ConfigurationManager(this);
		configManager.load();
		
		protectionManager = new ProtectionManager(this);
		protectionManager.loadStones();

		limitsManager = new LimitsManager();
		limitsManager.loadLimits();

		// Init DB
		try {
			// Currently only mysql support
			databaseManager = new DatabaseManager(new MySQLAdapter(configManager.getNode("storage", "mysql", "host").getString(), configManager.getNode("storage", "mysql", "port").getInt(), configManager.getNode("storage", "mysql", "database").getString(), configManager.getNode("storage", "mysql", "username").getString(), configManager.getNode("storage", "mysql", "password").getString(), configManager.getNode("storage", "mysql", "protection-table").getString()));
			databaseManager.connect();
		} catch (Exception e2) {
			log.error("Error connecting to db:");
			e2.printStackTrace();
		}

		Map<String, Object> commandChoices = Arrays.asList(
				// Command Choices
				new String[] {"groups", "stones", "limits"}
			).stream().collect(Collectors.toMap(choice->choice, Function.identity()));
		
		CommandSpec psCommand = CommandSpec.builder()
				.description(Text.of("Main SPS command"))
				.permission("sps.command.use")
				.executor(new SPSCommand(this))
				.arguments(
							GenericArguments.choices(Text.of("subcommand"), commandChoices)
						)
				.build();
		
		Sponge.getCommandManager().register(this, psCommand, "ps", "sps");
		Sponge.getEventManager().registerListeners(this, new ProtectionPlaceEvent());
	}
}
