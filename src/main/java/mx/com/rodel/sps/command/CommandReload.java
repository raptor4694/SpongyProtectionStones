package mx.com.rodel.sps.command;

import org.spongepowered.api.command.CommandSource;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.utils.Helper;

public class CommandReload implements ICommand{

	@Override
	public boolean onCommand(CommandSource source, String[] args) {
		SpongyPS.getInstance().reload(source);

		for(String arg : args){
			if(arg.equalsIgnoreCase("-s")){
				int count = SpongyPS.getInstance().getProtectionManager().reload();
				int chunks = SpongyPS.getInstance().getProtectionManager().chunks();
				source.sendMessage(Helper.chatColor("&aStone cache reloaded (&6Protections: &7"+count+"&a, &6Chunks: &7"+chunks+"&a)"));
				break;
			}
		}
		
		return true;
	}

	@Override
	public String getName() {
		return "reload";
	}

	@Override
	public String getUsage() {
		return "[-s (Reload protection stones cache)]";
	}

	@Override
	public String getDescription() {
		return "Reload configuration";
	}
}
