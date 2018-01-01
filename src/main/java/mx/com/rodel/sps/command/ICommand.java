package mx.com.rodel.sps.command;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.utils.Helper;

public interface ICommand {
	public boolean onCommand(CommandSource source, String[] args);
	public String getName();
	public String getUsage();
	public String getDescription();
	
	default Player getPlayer(CommandSource source){
		if(source instanceof Player){
			return (Player) source;
		}
		
		source.sendMessage(Helper.chatColor("&cThis command its only for players"));
		return null;
	}
	
	default String getHelp(){
		String usage = getUsage();
		String description = getDescription();
		return (usage==null ? "" : " &a"+usage+" ")+(description==null ? "" : "&7("+description+")");
	}
	
	default boolean testPermission(CommandSource player, boolean message){
		if(player.hasPermission("ps.command."+getName())){
			return true;
		}
		
		if(message){
			player.sendMessage(SpongyPS.getInstance().getLangManager().translate("no-permission", true));
		}
		return false;
	}
}
