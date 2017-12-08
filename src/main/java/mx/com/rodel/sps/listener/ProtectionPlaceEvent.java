package mx.com.rodel.sps.listener;

import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import com.flowpowered.math.vector.Vector3i;

import mx.com.rodel.sps.utils.Helper;

public class ProtectionPlaceEvent {
	@Listener
	public void onBlockPlace(ChangeBlockEvent.Place e){
		Optional<Player> player = Helper.playerCause(e.getCause());
		if(player.isPresent()){
			BlockSnapshot block = e.getTransactions().get(0).getFinal();
			for(Vector3i loc : Helper.calculateCenter(block.getLocation().get())){
				player.get().sendBlockChange(loc, BlockTypes.GLASS.getDefaultState());
			}
		}
	}
}
