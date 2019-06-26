package mx.com.rodel.sps.command;

import java.util.Optional;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.protection.Protection;

public class CommandRename implements ICommand {

	@Override
	public boolean onCommand(CommandSource source, String[] args) {
		Player player = getPlayer(source);
		if(player != null) {
			Optional<Protection> op = SpongyPS.getInstance().getProtectionManager().isRegion(player.getLocation());
			if(op.isPresent()) {
				Protection protection = op.get();
				if(!protection.getOwner().equals(player.getUniqueId())) {
					source.sendMessage(SpongyPS.getInstance().getLangManager().translate("no-owner", false));
					return true;
				}
				if(args.length == 0) {
					protection.setName(null);
				}
			}
		}
		return true;
	}

	@Override
	public String getName() {
		return "rename";
	}

	@Override
	public String getUsage() {
		return null;
	}

	@Override
	public String getDescription() {
		return SpongyPS.getInstance().getLangManager().localize("commands-rename-description");
	}

}
