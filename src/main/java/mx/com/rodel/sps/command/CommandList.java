package mx.com.rodel.sps.command;

import java.util.Collection;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.protection.Protection;

public class CommandList implements ICommand {

	@Override
	public boolean onCommand(CommandSource source, String[] args) {
		Player player = getPlayer(source);
		if(player != null) {
			PaginationList.Builder builder = PaginationList.builder().title(SpongyPS.getInstance().getLangManager().translate("list", false));
			
			Collection<Protection> protections = SpongyPS.getInstance().getProtectionManager().getProtectionsOwnedBy(player);
			
			builder.contents(protections.stream().map(Protection::toText).collect(Collectors.toList())).sendTo(player);
		}
		return true;
	}

	@Override
	public String getName() {
		return "list";
	}

	@Override
	public String getUsage() {
		return null;
	}

	@Override
	public String getDescription() {
		return SpongyPS.getInstance().getLangManager().localize("commands-list-description");
	}

}
