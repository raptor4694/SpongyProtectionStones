package mx.com.rodel.sps.command;

import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.pagination.PaginationList;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.limits.Group;
import mx.com.rodel.sps.utils.Helper;

public class CommandGroups implements ICommand{
	@Override
	public boolean onCommand(CommandSource source, String[] args) {
		List<Group> groups = SpongyPS.getInstance().getLimitsManager().getGroups();
		PaginationList.builder().title(Helper.chatColor("&6Groups")).contents(groups.stream().map(Group::toText).collect(Collectors.toList())).build().sendTo(source);
		return true;
	}

	@Override
	public String getName() {
		return "groups";
	}

	@Override
	public String getUsage() {
		return null;
	}

	@Override
	public String getDescription() {
		return "List of all group limits";
	}

}
