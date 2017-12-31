package mx.com.rodel.sps.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.config.LocaleFormat;
import mx.com.rodel.sps.limits.Group;
import mx.com.rodel.sps.protection.Protection;
import mx.com.rodel.sps.protection.ProtectionManager;
import mx.com.rodel.sps.protection.ProtectionStone;
import mx.com.rodel.sps.utils.Helper;

public class SPSCommand implements CommandExecutor {
	
	private SpongyPS pl;
	
	public Map<String, Object> commandChoices;
	
	public SPSCommand(SpongyPS pl) {
		this.pl = pl;
		commandChoices = Arrays.asList(
				// Command Choices
				new String[] {"groups", "stones", "limits", "reload", "info", "sreload", "visualize"}
			).stream().collect(Collectors.toMap(choice->choice, Function.identity()));
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String subcommand = (String)args.getOne(Text.of("subcommand")).get();
		PaginationList.Builder pagination = PaginationList.builder();
		
		Player player = null;
		if(src instanceof Player){
			player = (Player) src;
		}
		
		switch (subcommand) {
			case "groups":
				if(testPermission(src, "sps.command.groups")){
					pagination.title(Text.of("Groups"));
					
					List<Group> groups = pl.getLimitsManager().getGroups();
					pagination.contents(groups.stream().map(Group::toText).collect(Collectors.toList()));
					
					pagination.build().sendTo(src);
					
				}
				break;
			case "stones":
				if(testPermission(src, "sps.command.stones")){
					pagination.title(Text.of("Stones"));
					
					List<ProtectionStone> protectionStone = pl.getProtectionManager().getStones();
					pagination.contents(protectionStone.stream().map(ProtectionStone::toText).collect(Collectors.toList()));
					
					pagination.build().sendTo(src);
				}
				break;
			case "limits":
				if(testPermission(src, "sps.command.limits")){
					if(player!=null){
						pagination.title(pl.getLangManager().translate("limits", false));
						
						Map<ProtectionStone, Integer> limits = pl.getLimitsManager().getLimits(player);
						pagination.contents(limits.entrySet().stream().map(entry -> Helper.chatColor("&6"+entry.getKey().getDisplayName()+": &7"+entry.getValue())).collect(Collectors.toList()));
						
						pagination.build().sendTo(src);
					}else{
						src.sendMessage(Helper.chatColor("&cOnly players can execute this command"));
					}
				}
				break;
			case "reload":
				if(testPermission(src, "sps.command.reload")){
					pl.reload(src);
				}
				break;
			case "sreload":
				if(testPermission(src, "sps.command.sreload")){
					SpongyPS.getInstance().getProtectionManager().reload();
					src.sendMessage(Helper.chatColor("&aStones reloaded (Chunks: "+SpongyPS.getInstance().getProtectionManager().chunks()+")..."));
				}
				break;
			case "info":
				if(testPermission(src, "sps.command.info")){
					if(player!=null){
						Optional<Protection> oprotection = ProtectionManager.isRegion(player.getLocation());
						if(oprotection.isPresent()){
							List<Text> info = new ArrayList<>();
							Protection protection = oprotection.get();
							
							if(SpongyPS.getInstance().getConfigManger().getNode("info", "show-id").getBoolean()){
								info.add(SpongyPS.getInstance().getLangManager().translate(new LocaleFormat("info-id").add("{id}", String.valueOf(protection.getID())), false));
							}
							
							info.add(SpongyPS.getInstance().getLangManager().translate(new LocaleFormat("info-owner").add("{owner}", protection.getOwnerName()), false));
							info.add(SpongyPS.getInstance().getLangManager().translate(
									new LocaleFormat("info-center")
									.add("{x}", String.valueOf(protection.getCenter().getBlockX()))
									.add("{y}", String.valueOf(protection.getCenter().getBlockY()))
									.add("{z}", String.valueOf(protection.getCenter().getBlockZ())), false));
							
							PaginationList.builder().contents(info).title(pl.getLangManager().translate("info-title", false)).sendTo(src);
						}else{
							src.sendMessage(SpongyPS.getInstance().getLangManager().translate("info-nostone", false));	
						}
					}else{
						src.sendMessage(Helper.chatColor("&cOnly players can execute this command"));
					}
				}
				break;
			case "visualize":
				if(testPermission(src, "sps.command.visualize")){
					if(player!=null){
						Optional<Protection> protection = ProtectionManager.isRegion(player.getLocation());
						if(protection.isPresent()){
							protection.get().visualize(player);
						}else{
							src.sendMessage(SpongyPS.getInstance().getLangManager().translate("info-nostone", true));	
						}
					}else{
						src.sendMessage(Helper.chatColor("&cOnly players can execute this command"));
					}
				}
				break;
			default:
				return CommandResult.empty();
		}
		return CommandResult.success();
	}
	
	public boolean testPermission(CommandSource src, String permission){
		if(src.hasPermission(permission)){
			return true;
		}
		
		src.sendMessage(SpongyPS.getInstance().getLangManager().translate("no-permission", true));
		return false;
	}
}
