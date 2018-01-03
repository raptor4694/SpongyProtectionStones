package mx.com.rodel.sps.command;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.config.LocaleFormat;
import mx.com.rodel.sps.protection.Protection;

public class CommandAdd implements ICommand{

	@Override
	public boolean onCommand(CommandSource source, String[] args) {
		Player player = getPlayer(source);
		if(player!=null && args.length>0){
			Optional<Player> oplayer = Sponge.getServer().getPlayer(args[0]);
			if(oplayer.isPresent()){
				Player p = oplayer.get();
				if(p.getUniqueId().equals(player.getUniqueId())){ // It it trying to add itself?
					source.sendMessage(SpongyPS.getInstance().getLangManager().translate("member-yourself", true));
					return true;
				}
				Optional<Protection> op = SpongyPS.getInstance().getProtectionManager().isRegion(player.getLocation());
				if(op.isPresent()){
					Protection protection = op.get();
					
					if(protection.getOwner().equals(player.getUniqueId())){ // Its owner?
						if(protection.getMembers().get(p.getUniqueId())==null){ // The player isn't added already?
							protection.addMember(p.getUniqueId(), p.getName());
							source.sendMessage(SpongyPS.getInstance().getLangManager().translate(new LocaleFormat("member-add").add("{member}", p.getName()), true));
						}else{
							source.sendMessage(SpongyPS.getInstance().getLangManager().translate("member-already", true));
						}
					}else{
						source.sendMessage(SpongyPS.getInstance().getLangManager().translate(new LocaleFormat("no-owner"), true));
					}
					
				}else{
					source.sendMessage(SpongyPS.getInstance().getLangManager().translate("info-nostone", false));
				}
			}else{
				source.sendMessage(SpongyPS.getInstance().getLangManager().translate("no-player", false));
			}
			return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return "add";
	}

	@Override
	public String getUsage() {
		return "<name>";
	}

	@Override
	public String getDescription() {
		return "Add a member into the protection";
	}
}
