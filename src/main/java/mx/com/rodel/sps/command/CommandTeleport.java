package mx.com.rodel.sps.command;

import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.property.block.PassableProperty;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.protection.Protection;

public class CommandTeleport implements ICommand {
	
	private static final Pattern COORDS_REGEX;
	
	static {
		String dec = "[-+]?(\\.\\d+|\\d+(\\.\\d*)?)([eE][-+]?\\d+)?";
		COORDS_REGEX = Pattern.compile(String.format("(?<x>%1$s),(?<x>%1$s),(?<x>%1$s)", dec));
	}

	@Override
	public boolean onCommand(CommandSource source, String[] args) {
		Player player = getPlayer(source);
		if(player != null && args.length != 0) {
			Collection<Protection> protections = SpongyPS.getInstance().getProtectionManager().getProtectionsOwnedBy(player);
			
			String name = String.join(" ", args);
			
			boolean isCoords;
			Matcher m = COORDS_REGEX.matcher(name);
			double x, y, z;
			if(isCoords = m.matches()) {
				x = Double.parseDouble(m.group("x"));
				y = Double.parseDouble(m.group("y"));
				z = Double.parseDouble(m.group("z"));
			}
			
			for(Protection protection : protections) {
				Vector3d center = protection.getCenter().getPosition();	
				if(protection.getName().equals(name) || isCoords && (center.getX() == x || center.getFloorX() == x) && (center.getY() == y || center.getFloorY() == y) && (center.getZ() == z || center.getFloorZ() == z)) {
					teleport(player, protection);
					return true;
				}
			}
			
			source.sendMessage(SpongyPS.getInstance().getLangManager().translate("no-protection", false));
		}
		return true;
	}
	
	private static final Vector3i UP = Vector3i.from(0, 1, 0);
	
	private void teleport(Player player, Protection protection) {
		if(!player.setLocationSafely(protection.getCenter().add(UP))) {
			player.sendMessage(SpongyPS.getInstance().getLangManager().translate("no-teleport", false));
		}
	}

	@Override
	public String getName() {
		return "teleport";
	}

	@Override
	public String getUsage() {
		return null;
	}

	@Override
	public String getDescription() {
		return SpongyPS.getInstance().getLangManager().localize("commands-teleport-description");
	}

}
