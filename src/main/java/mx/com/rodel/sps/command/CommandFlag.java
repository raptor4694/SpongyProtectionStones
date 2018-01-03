package mx.com.rodel.sps.command;

import java.util.Optional;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.api.SPSApi;
import mx.com.rodel.sps.config.LocaleFormat;
import mx.com.rodel.sps.protection.Protection;

public class CommandFlag implements ICommand{

	@Override
	public boolean onCommand(CommandSource source, String[] args) {
		Player player = getPlayer(source);
		if(player!=null){
			if(args.length>1){
				String flag = args[0];
				
				Object realValue = SpongyPS.getInstance().getFlagManager().getFlags().get(flag);
				if(realValue==null){
					// TODO
					player.sendMessage(SpongyPS.getInstance().getLangManager().translate(new LocaleFormat("flag-invalid").add("{flags}", SpongyPS.getInstance().getFlagManager().getFlags().entrySet().stream().filter(entry -> player.hasPermission("ps.flag."+entry.getKey())).map(entry -> "&6"+entry.getKey()+": &7"+entry.getValue()).collect(Collectors.joining("&a, "))), true));
				}
				
				Optional<Protection> oprotection = SPSApi.getProtection(player.getLocation());
				if(oprotection.isPresent()){
					Protection protection = oprotection.get();
					
					if(player.getUniqueId().equals(protection.getOwner())){
						
					}else{
						player.sendMessage(SpongyPS.getInstance().getLangManager().translate("no-owner", true));
					}
				}else{
					player.sendMessage(SpongyPS.getInstance().getLangManager().translate("info-nostone", true));
				}
			}else{
				return false;
			}
		}
		return true;
	}

	@Override
	public String getName() {
		return "flag";
	}

	@Override
	public String getUsage() {
		return "<flag> <value>";
	}

	@Override
	public String getDescription() {
		return SpongyPS.getInstance().getLangManager().localize("commands-flag-description");
	}
}
