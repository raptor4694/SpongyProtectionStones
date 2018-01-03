package mx.com.rodel.sps.listener;

import java.sql.SQLException;
import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import com.flowpowered.math.vector.Vector2i;
import com.google.common.collect.ImmutableMap;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.config.LocaleFormat;
import mx.com.rodel.sps.protection.Protection;
import mx.com.rodel.sps.protection.ProtectionStone;
import mx.com.rodel.sps.utils.Helper;

public class BlockListener {
	@Listener
	public void onBlockPlace(ChangeBlockEvent.Place e){
		Optional<Player> oplayer = Helper.playerCause(e.getCause());
		if(oplayer.isPresent()){
			Player player = oplayer.get();
			
			// Sneaking?
			if(player.get(Keys.IS_SNEAKING).orElse(false)){
				BlockSnapshot block = e.getTransactions().get(0).getFinal();
				Optional<ProtectionStone> ostone = SpongyPS.getInstance().getProtectionManager().getStoneByBlock(block.getState().getType());
				if(ostone.isPresent()){
					// START Protection placing code
					ProtectionStone stone = ostone.get();
					
					ImmutableMap<ProtectionStone, Integer> limits = SpongyPS.getInstance().getLimitsManager().getLimits(player);
					Integer limit = limits.get(stone);
					if(limit==null || limit.intValue()<1){
						player.sendMessage(SpongyPS.getInstance().getLangManager().translate("stone-nopermission", true));
					}else{
						Protection mock = new Protection(player.getUniqueId(), player.getName(), player.getWorld(), block.getPosition(), stone);
						
						for(Vector2i chunk : mock.getParentChunks()){
							for(Protection protection : SpongyPS.getInstance().getProtectionManager().getProtectionsInChunk(player.getWorld().getUniqueId(), chunk)){
								if(protection.intersects(mock)){
									player.sendMessage(SpongyPS.getInstance().getLangManager().translate("stone-overlapping", true)); // Yep.. its overlapping
									return;
								}
							}
						}
						
						try {
							SpongyPS.getInstance().getProtectionManager().saveProtection(mock);
							mock.visualize(player);
							player.sendMessage(SpongyPS.getInstance().getLangManager().translate(new LocaleFormat("stone-place").add("{stone}", stone.getDisplayName()), true));
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
					// END Protection placing code
				}
			}
			
		}
	}
}
