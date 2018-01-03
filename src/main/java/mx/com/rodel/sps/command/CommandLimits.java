package mx.com.rodel.sps.command;

import java.util.Map;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.protection.ProtectionStone;
import mx.com.rodel.sps.utils.Helper;

public class CommandLimits implements ICommand{

	@Override
	public boolean onCommand(CommandSource source, String[] args) {
		Player player = getPlayer(source);
		if(player!=null){
			PaginationList.Builder builder = PaginationList.builder().title(SpongyPS.getInstance().getLangManager().translate("limits", false));
			
			Map<ProtectionStone, Integer> limits = SpongyPS.getInstance().getLimitsManager().getLimits(player);
			
			builder.contents(limits.entrySet().stream().map(entry -> Helper.chatColor("&6"+entry.getKey().getDisplayName()+": &7"+entry.getValue())).collect(Collectors.toList())).sendTo(player);
		}
		return true;
	}

	@Override
	public String getName() {
		return "limits";
	}

	@Override
	public String getUsage() {
		return null;
	}

	@Override
	public String getDescription() {
		return SpongyPS.getInstance().getLangManager().localize("commands-limits-description");
	}
}
