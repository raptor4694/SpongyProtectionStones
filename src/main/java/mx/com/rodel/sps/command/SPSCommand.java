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
		
		commands.put("info", new CommandInfo());
	}
	
	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		String[] args = arguments.trim().split(" ");

		if(args.length>0){
			ICommand command = commands.get(args[0]);
			
			if(command!=null){
				String[] mArgs = new String[Math.max(args.length-1, 0)];
				
				for (int i = 0; i < args.length; i++) {
					if(i==0){
						continue;
					}
					mArgs[i] = args[i];
				}
				
				if(command.testPermission(source, true)){
					if(!command.onCommand(source, mArgs)){
						source.sendMessage(Helper.chatColor("/ps "+command.getName()+command.getHelp()));
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
				source.sendMessage(Helper.chatColor("/ps "+command.getValue().getName()+command.getValue().getHelp()));
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
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Optional<Text> getHelp(CommandSource source) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Text getUsage(CommandSource source) {
		
		return null;
	}

	
//		switch (subcommand) {
//			case "groups":
//				if(testPermission(src, "sps.command.groups")){
//					pagination.title(Text.of("Groups"));
//					
//					List<Group> groups = pl.getLimitsManager().getGroups();
//					pagination.contents(groups.stream().map(Group::toText).collect(Collectors.toList()));
//					
//					pagination.build().sendTo(src);
//					
//				}
//				break;
//			case "stones":
//				if(testPermission(src, "sps.command.stones")){
//					pagination.title(Text.of("Stones"));
//					
//					List<ProtectionStone> protectionStone = pl.getProtectionManager().getStones();
//					pagination.contents(protectionStone.stream().map(ProtectionStone::toText).collect(Collectors.toList()));
//					
//					pagination.build().sendTo(src);
//				}
//				break;
//			case "limits":
//				if(testPermission(src, "sps.command.limits")){
//					if(player!=null){
//						pagination.title(pl.getLangManager().translate("limits", false));
//						
//						Map<ProtectionStone, Integer> limits = pl.getLimitsManager().getLimits(player);
//						pagination.contents(limits.entrySet().stream().map(entry -> Helper.chatColor("&6"+entry.getKey().getDisplayName()+": &7"+entry.getValue())).collect(Collectors.toList()));
//						
//						pagination.build().sendTo(src);
//					}else{
//						src.sendMessage(Helper.chatColor("&cOnly players can execute this command"));
//					}
//				}
//				break;
//			case "reload":
//				if(testPermission(src, "sps.command.reload")){
//					pl.reload(src);
//				}
//				break;
//			case "sreload":
//				if(testPermission(src, "sps.command.sreload")){
//					SpongyPS.getInstance().getProtectionManager().reload();
//					src.sendMessage(Helper.chatColor("&aStones reloaded (Chunks: "+SpongyPS.getInstance().getProtectionManager().chunks()+")..."));
//				}
//				break;
//			case "info":
//				if(testPermission(src, "sps.command.info")){
//					if(player!=null){
//						Optional<Protection> oprotection = ProtectionManager.isRegion(player.getLocation());
//						if(oprotection.isPresent()){
//							List<Text> info = new ArrayList<>();
//							Protection protection = oprotection.get();
//							
//							if(SpongyPS.getInstance().getConfigManger().getNode("info", "show-id").getBoolean()){
//								info.add(SpongyPS.getInstance().getLangManager().translate(new LocaleFormat("info-id").add("{id}", String.valueOf(protection.getID())), false));
//							}
//							
//							info.add(SpongyPS.getInstance().getLangManager().translate(new LocaleFormat("info-owner").add("{owner}", protection.getOwnerName()), false));
//							info.add(SpongyPS.getInstance().getLangManager().translate(
//									new LocaleFormat("info-center")
//									.add("{x}", String.valueOf(protection.getCenter().getBlockX()))
//									.add("{y}", String.valueOf(protection.getCenter().getBlockY()))
//									.add("{z}", String.valueOf(protection.getCenter().getBlockZ())), false));
//							
//							PaginationList.builder().contents(info).title(pl.getLangManager().translate("info-title", false)).sendTo(src);
//						}else{
//							src.sendMessage(SpongyPS.getInstance().getLangManager().translate("info-nostone", false));	
//						}
//					}else{
//						src.sendMessage(Helper.chatColor("&cOnly players can execute this command"));
//					}
//				}
//				break;
//			case "visualize":
//				if(testPermission(src, "sps.command.visualize")){
//					if(player!=null){
//						Optional<Protection> protection = ProtectionManager.isRegion(player.getLocation());
//						if(protection.isPresent()){
//							protection.get().visualize(player);
//						}else{
//							src.sendMessage(SpongyPS.getInstance().getLangManager().translate("info-nostone", true));	
//						}
//					}else{
//						src.sendMessage(Helper.chatColor("&cOnly players can execute this command"));
//					}
//				}
//				break;
//			default:
//				return CommandResult.empty();
//		}
	
	public boolean testPermission(CommandSource src, String permission){
		if(src.hasPermission(permission)){
			return true;
		}
		
		src.sendMessage(SpongyPS.getInstance().getLangManager().translate("no-permission", true));
		return false;
	}

}
