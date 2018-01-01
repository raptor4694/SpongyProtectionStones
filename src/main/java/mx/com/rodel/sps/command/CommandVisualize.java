package mx.com.rodel.sps.command;

import java.util.Optional;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.protection.Protection;

public class CommandVisualize implements ICommand{

	@Override
	public boolean onCommand(CommandSource source, String[] args) {
		Player player = getPlayer(source);
		if(player!=null){
			Optional<Protection> op = SpongyPS.getInstance().getProtectionManager().isRegion(player.getLocation());
			if(op.isPresent()){
				Protection protection = op.get();

				source.sendMessage(SpongyPS.getInstance().getLangManager().translate("visualize", false));
				protection.visualize(player);
			}else{
				source.sendMessage(SpongyPS.getInstance().getLangManager().translate("info-nostone", false));
			}
		}
		return true;
	}

	@Override
	public String getName() {
		return "visualize";
	}

	@Override
	public String getUsage() {
		return null;
	}

	@Override
	public String getDescription() {
		return SpongyPS.getInstance().getLangManager().localize("commands-visualize-description");
	}

}
