package mx.com.rodel.sps.command;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.api.SPSApi;
import mx.com.rodel.sps.protection.Protection;
import mx.com.rodel.sps.utils.Helper;

public class CommandSetOwner implements ICommand{

	@Override
	public boolean onCommand(CommandSource source, String[] args) {
		if(args.length>0){
			Player player = getPlayer(source);
			if(player!=null){
				
				Optional<Player> target = Sponge.getServer().getPlayer(args[0]);
				if(target.isPresent()){
					Optional<Protection> protection = SPSApi.getProtection(player.getLocation());
					
					if(protection.isPresent()){
						protection.get().setOwner(target.get().getUniqueId(), target.get().getName());
						source.sendMessage(Helper.chatColor("&aOwner updated..."));
					}else{
						player.sendMessage(SpongyPS.getInstance().getLangManager().translate("info-nostone", true));
					}
				}else{
					source.sendMessage(SpongyPS.getInstance().getLangManager().translate("no-player", false));
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return "setowner";
	}

	@Override
	public String getUsage() {
		return "<owner>";
	}

	@Override
	public String getDescription() {
		return "Sets a new owner";
	}
}
