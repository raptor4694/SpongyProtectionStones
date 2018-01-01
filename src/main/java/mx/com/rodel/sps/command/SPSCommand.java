package mx.com.rodel.sps.command;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.utils.Helper;

public class SPSCommand implements CommandCallable {
	
	private SpongyPS pl;
	
	private Map<String, ICommand> commands = Maps.newHashMap();
	
//	public Map<String, Object> commandChoices;
	
	public SPSCommand(SpongyPS pl) {
		this.pl = pl;
//		commandChoices = Arrays.asList(
//				// Command Choices
//				new String[] {"groups", "stones", "limits", "reload", "info", "sreload", "visualize"}
//			).stream().collect(Collectors.toMap(choice->choice, Function.identity()));
		
		// User commands
		commands.put("info", new CommandInfo());
		commands.put("visualize", new CommandVisualize());
		
		// Admin commands
		commands.put("reload", new CommandReload());
		commands.put("stones", new CommandStones());
		commands.put("groups", new CommandGroups());
	}
	
	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		String[] args = arguments.trim().split(" ");

		if(args.length>0){
			ICommand command = commands.get(args[0]);
			
			if(command!=null){
				List<String> mArgs = Lists.newArrayList(args);
				mArgs.remove(0);
				
				if(command.testPermission(source, true)){
					if(!command.onCommand(source, mArgs.toArray(new String[] {}))){
						source.sendMessage(Helper.chatColor("&a/ps "+command.getName()+command.getHelp()));
					}
					return CommandResult.success();
				}
			}else{
				help(source);
			}
		}else{
			help(source);
		}
		
		return CommandResult.empty();
	}
	
	public void help(CommandSource source){
		for(Entry<String, ICommand> command : commands.entrySet()){
			if(command.getValue().testPermission(source, false)){
				source.sendMessage(Helper.chatColor("&a/ps "+command.getValue().getName()+command.getValue().getHelp()));
			}
		}
	}
	
	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition)
			throws CommandException {
		return commands.keySet().stream().collect(Collectors.toList());
	}
	
	@Override
	public boolean testPermission(CommandSource source) {
		return true;
	}
	
	@Override
	public Optional<Text> getShortDescription(CommandSource source) {
		return Optional.empty();
	}
	
	@Override
	public Optional<Text> getHelp(CommandSource source) {
		return Optional.empty();
	}
	
	@Override
	public Text getUsage(CommandSource source) {
		return null;
	}
	
	public boolean testPermission(CommandSource src, String permission){
		if(src.hasPermission(permission)){
			return true;
		}
		
		src.sendMessage(pl.getLangManager().translate("no-permission", true));
		return false;
	}

}
