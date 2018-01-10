package mx.com.rodel.sps.command;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.api.SPSApi;
import mx.com.rodel.sps.config.LocaleFormat;
import mx.com.rodel.sps.flags.FlagManager.Types;
import mx.com.rodel.sps.protection.Protection;
import mx.com.rodel.sps.utils.Helper;

public class CommandFlag implements ICommand{
	@Override
	public boolean onCommand(CommandSource source, String[] args) {
		Player player = getPlayer(source);
		if(player!=null){
			if(args.length>1){
				String flag = args[0];
				
				String value = String.join(" ", Arrays.asList(args).subList(1, args.length));
				
				
				Object defaultValue = SpongyPS.getInstance().getFlagManager().getFlags().get(flag);
				if(defaultValue!=null){
					// Check permission
					if(!source.hasPermission("ps.flag."+flag)){
						player.sendMessage(SpongyPS.getInstance().getLangManager().translate("flag-nopermission", true));
						return true;
					}
					
					// Valid name flag, then check the value

					Object obj = null;

					// bool
					if(value.equals("true")) obj = true;
					if(value.equals("false")) obj = false;
					
					// int
					if(Helper.isNumber(value)) obj = Integer.parseInt(value);
					
					// string
					if(obj==null) obj = value;
					
					if(defaultValue.getClass().equals(obj.getClass())){
						// Check if valid protection
						Optional<Protection> oprotection = SPSApi.getProtection(player.getLocation());
						if(oprotection.isPresent()){
							Protection protection = oprotection.get();
							
							if(player.getUniqueId().equals(protection.getOwner())){
								// Lets update the flag value
								protection.setFlag(flag, obj);
							}else{
								player.sendMessage(SpongyPS.getInstance().getLangManager().translate("no-owner", true));
							}
						}else{
							player.sendMessage(SpongyPS.getInstance().getLangManager().translate("info-nostone", true));
						}
					}else{
						player.sendMessage(SpongyPS.getInstance().getLangManager().translate(Types.getByClass(defaultValue.getClass()).message, true));
					}
				}else{
					// If the flag is invalid
					player.sendMessage(SpongyPS.getInstance().getLangManager().translate(new LocaleFormat("flag-invalid").add("{flags}", "&6"+SpongyPS.getInstance().getFlagManager().getFlags().entrySet().stream().filter(entry -> player.hasPermission("ps.flag."+entry.getKey())).map(entry -> entry.getKey()).collect(Collectors.joining(", "))), true));
				}
			}else{
				return false;
			}
		}
		return true;
	}

	@Override
	public String getName() {
		return "flag";
	}

	@Override
	public String getUsage() {
		return "<flag> <value>";
	}

	@Override
	public String getDescription() {
		return SpongyPS.getInstance().getLangManager().localize("commands-flag-description");
	}
}
