package mx.com.rodel.sps.listener;

import java.sql.SQLException;
import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector2i;
import com.google.common.collect.ImmutableMap;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.api.SPSApi;
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
			BlockSnapshot block = e.getTransactions().get(0).getFinal();

			if(!block.getLocation().isPresent()){
				return;
			}
			
			Location<World> blockLoc = block.getLocation().get();
			
			// Check permissions
			if(checkBuild(blockLoc, player)){
				e.setCancelled(true);
				return;
			}
			
			// Sneaking?
			if(player.get(Keys.IS_SNEAKING).orElse(false)){
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
	
	@Listener
	public void onBlockBreak(ChangeBlockEvent.Break e){
		Optional<Player> oplayer = Helper.playerCause(e.getCause());
		if(oplayer.isPresent()){
			Player player = oplayer.get();
			BlockSnapshot block = e.getTransactions().get(0).getFinal();

			if(!block.getLocation().isPresent()){
				return;
			}
			
			Location<World> blockLoc = block.getLocation().get();
			
			// Check permissions
			if(checkBuild(blockLoc, player)){
				e.setCancelled(true);
				return;
			}
		}
	}
	
	public boolean checkBuild(Location<World> blockLoc, Player player){
		Optional<Protection> op = SPSApi.getProtection(blockLoc);
		if(op.isPresent()){
			Protection protection = op.get();
			
			if(protection.getFlag("prevent-build", Boolean.class).orElse(true) && !protection.hasPermission(player.getUniqueId())){
				player.sendMessage(SpongyPS.getInstance().getLangManager().translate("no-build", true));
				return true;
			}
		}
		return false;
	}
}
