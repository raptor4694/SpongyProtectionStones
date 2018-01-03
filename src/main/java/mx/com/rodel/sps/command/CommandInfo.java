package mx.com.rodel.sps.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;

import com.google.common.base.Joiner;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.config.LocaleFormat;
import mx.com.rodel.sps.protection.Protection;

public class CommandInfo implements ICommand{

	@Override
	public boolean onCommand(CommandSource source, String[] args) {
		Player player = getPlayer(source);
		if(player!=null){
			Optional<Protection> op = SpongyPS.getInstance().getProtectionManager().isRegion(player.getLocation());
			if(op.isPresent()){
				List<Text> info = new ArrayList<>();
				Protection protection = op.get();
				
				if(SpongyPS.getInstance().getConfigManger().getNode("info", "show-id").getBoolean()){
					info.add(SpongyPS.getInstance().getLangManager().translate(new LocaleFormat("info-id").add("{id}", String.valueOf(protection.getID())), false));
				}
				
				info.add(SpongyPS.getInstance().getLangManager().translate(new LocaleFormat("info-owner").add("{owner}", protection.getOwnerName()), false));
				info.add(SpongyPS.getInstance().getLangManager().translate(
						new LocaleFormat("info-center")
						.add("{x}", String.valueOf(protection.getCenter().getBlockX()))
						.add("{y}", String.valueOf(protection.getCenter().getBlockY()))
						.add("{z}", String.valueOf(protection.getCenter().getBlockZ())), false));
				info.add(SpongyPS.getInstance().getLangManager().translate(new LocaleFormat("info-members").add("{members}", String.join(", ", protection.getMembers().values())), false));
				info.add(SpongyPS.getInstance().getLangManager().translate(new LocaleFormat("info-flags").add("{flags}", "&6"+Joiner.on(", &6").withKeyValueSeparator(":&7 ").join(protection.getFlags().getFlags())), false));
				
				PaginationList.builder().contents(info).title(SpongyPS.getInstance().getLangManager().translate("info-title", false)).sendTo(source);
			}else{
				source.sendMessage(SpongyPS.getInstance().getLangManager().translate("info-nostone", false));
			}
		}
		return true;
	}

	@Override
	public String getName() {
		return "info";
	}

	@Override
	public String getUsage() {
		return null;
	}

	@Override
	public String getDescription() {
		return SpongyPS.getInstance().getLangManager().localize("commands-info-description");
	}
}
