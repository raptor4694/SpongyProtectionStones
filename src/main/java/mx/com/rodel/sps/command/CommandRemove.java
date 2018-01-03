package mx.com.rodel.sps.command;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.config.LocaleFormat;
import mx.com.rodel.sps.protection.Protection;

public class CommandRemove implements ICommand{

	@Override
	public boolean onCommand(CommandSource source, String[] args) {
		Player player = getPlayer(source);
		if(player!=null && args.length>0){
				Optional<Protection> op = SpongyPS.getInstance().getProtectionManager().isRegion(player.getLocation());
				if(op.isPresent()){
					Protection protection = op.get();
					
					if(protection.getOwner().equals(player.getUniqueId())){ // Its owner?
						UUID remove = null;
						for(Entry<UUID, String> member : protection.getMembers().entrySet()){
							if(member.getValue().equalsIgnoreCase(args[0])){
								remove = member.getKey();
								source.sendMessage(SpongyPS.getInstance().getLangManager().translate(new LocaleFormat("member-remove").add("{member}", member.getValue()), true));
								break;
							}
						}
						
						if(remove!=null){
							protection.removeMember(remove);
						}else{
							source.sendMessage(SpongyPS.getInstance().getLangManager().translate("member-missing", true));
						}
						
//						if(protection.getMembers().get(p.getUniqueId())==null){ // The player isn't added already?
//							protection.removeMember(p.getUniqueId());
//						}else{
//						}
					}else{
						source.sendMessage(SpongyPS.getInstance().getLangManager().translate(new LocaleFormat("no-owner"), true));
					}
					
				}else{
					source.sendMessage(SpongyPS.getInstance().getLangManager().translate("info-nostone", false));
				}
			return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return "remove";
	}

	@Override
	public String getUsage() {
		return "<player>";
	}

	@Override
	public String getDescription() {
		return SpongyPS.getInstance().getLangManager().localize("commands-remove-description");
	}
}
