package mx.com.rodel.sps.command;

import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.pagination.PaginationList;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.protection.ProtectionStone;
import mx.com.rodel.sps.utils.Helper;

public class CommandStones implements ICommand{
	@Override
	public boolean onCommand(CommandSource source, String[] args) {
		List<ProtectionStone> groups = SpongyPS.getInstance().getProtectionManager().getStones();
		PaginationList.builder().title(Helper.chatColor("&6Stones")).contents(groups.stream().map(ProtectionStone::toText).collect(Collectors.toList())).build().sendTo(source);
		return true;
	}

	@Override
	public String getName() {
		return "stones";
	}

	@Override
	public String getUsage() {
		return null;
	}

	@Override
	public String getDescription() {
		return "List of all stone types";
	}

}
