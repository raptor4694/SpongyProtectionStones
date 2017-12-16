package mx.com.rodel.sps.command;

import java.util.List;
import java.util.Map;
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
import mx.com.rodel.sps.limits.Group;
import mx.com.rodel.sps.protection.ProtectionStone;
import mx.com.rodel.sps.utils.Helper;

public class SPSCommand implements CommandExecutor {
	
	private SpongyPS pl;
	
	public SPSCommand(SpongyPS pl) {
		this.pl = pl;
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
				pagination.title(Text.of("Groups"));
				
				List<Group> groups = pl.getLimitsManager().getGroups();
				pagination.contents(groups.stream().map(Group::toText).collect(Collectors.toList()));
				
				pagination.build().sendTo(src);
				break;
			case "stones":
				pagination.title(Text.of("Stones"));
				
				List<ProtectionStone> protectionStone = pl.getProtectionManager().getStones();
				pagination.contents(protectionStone.stream().map(ProtectionStone::toText).collect(Collectors.toList()));
				
				pagination.build().sendTo(src);
				break;
			case "limits":
				if(player!=null){
					pagination.title(Text.of("Limits"));
					
					Map<ProtectionStone, Integer> limits = pl.getLimitsManager().getLimits(player);
					pagination.contents(limits.entrySet().stream().map(entry -> Helper.chatColor("&6"+entry.getKey().getDisplayName()+": &7"+entry.getValue())).collect(Collectors.toList()));
					
					pagination.build().sendTo(src);
				}else{
					src.sendMessage(Helper.chatColor("&cOnly players can execute this command"));
				}
				break;
			default:
				return CommandResult.empty();
		}
		return CommandResult.success();
	}
}
