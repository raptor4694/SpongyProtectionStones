package mx.com.rodel.sps.listener;

import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.protection.ProtectionStone;
import mx.com.rodel.sps.utils.Helper;

public class ProtectionPlaceEvent {
	@Listener
	public void onBlockPlace(ChangeBlockEvent.Place e){
		Optional<Player> oplayer = Helper.playerCause(e.getCause());
		if(oplayer.isPresent()){
			Player player = oplayer.get();
			
			if(player.get(Keys.IS_SNEAKING).orElse(false)){
				BlockSnapshot block = e.getTransactions().get(0).getFinal();
				Optional<ProtectionStone> ostone = SpongyPS.getInstance().getProtectionManager().getStoneByBlock(block.getState().getType());
				System.out.println(ostone);
				if(ostone.isPresent()){
//					ostone.get();
				}
//				Helper.calculateCenter(block.getLocation().get());
			}
			
		}
	}
}
