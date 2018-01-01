package mx.com.rodel.sps.command;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.protection.Protection;

public class CommandAdd implements ICommand{

	@Override
	public boolean onCommand(CommandSource source, String[] args) {
		Player player = getPlayer(source);
		if(player!=null && args.length>0){
			Optional<Player> oplayer = Sponge.getServer().getPlayer(args[0]);
			if(oplayer.isPresent()){
				Optional<Protection> op = SpongyPS.getInstance().getProtectionManager().isRegion(player.getLocation());
				if(op.isPresent()){
					Protection protection = op.get();
					protection.addMember(oplayer.get().getUniqueId(), oplayer.get().getName());
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
