package mx.com.rodel.sps.listener;

import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableMap;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.protection.ProtectionManager;
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
				if(ostone.isPresent()){
					// Protection placing code
					ProtectionStone stone = ostone.get();
					System.out.println(stone.toString());
					
					ImmutableMap<ProtectionStone, Integer> limits = SpongyPS.getInstance().getLimitsManager().getLimits(player);
					Integer limit = limits.get(stone);
					if(limit==null || limit.intValue()<1){
						System.out.println("No limits");
					}else{
						Vector3i[] nx = Helper.calculateCenter(stone.getRange()/2, block.getLocation().get());
						ProtectionManager.createProtection(player, nx[0], nx[1], block.getLocation().get(), stone.getName());
					}
				}
			}
			
		}
	}
}
