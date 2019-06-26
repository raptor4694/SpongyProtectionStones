package mx.com.rodel.sps.listener;

import java.sql.SQLException;
import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.AttackEntityEvent;
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
	public void onInteract(InteractBlockEvent e) {
		if(!SpongyPS.getInstance().getConfigManger().areContainersProtected())
			return;
		BlockSnapshot block = e.getTargetBlock();
		Optional<Location<World>> optional = block.getLocation();
		if(optional.isPresent()) {
			Location<World> location = optional.get();
			Optional<TileEntity> tileEntityOpt = location.getTileEntity();
			if(tileEntityOpt.isPresent()) {
				TileEntity tileEntity = tileEntityOpt.get();
				if(tileEntity instanceof TileEntityCarrier) {
					Optional<Player> playerOpt = e.getCause().first(Player.class);
					if(playerOpt.isPresent()) {
						Player player = playerOpt.get();
						if(checkBuild(location, player)) {
							e.setCancelled(true);
							return;
						}
					}
				}
			}
		}
	}
	
	@Listener
	public void onAttackEntity(AttackEntityEvent e) {
		if(!SpongyPS.getInstance().getConfigManger().areEntitiesProtected())
			return;
		DamageSource source = e.getCause().get(AttackEntityEvent.SOURCE, DamageSource.class).get();
		if(source instanceof EntityDamageSource) {
			Entity entity = ((EntityDamageSource)source).getSource();
			if(entity instanceof Player) {
				if(checkBuild(e.getTargetEntity().getLocation(), (Player)entity)) {
					e.setCancelled(true);
				}
			}
		}
	}
	
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
				Optional<ProtectionStone> ostone = SpongyPS.getInstance().getProtectionManager().getStoneByBlockState(block.getState());
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
			
			Optional<Protection> ps = SPSApi.getProtection(blockLoc);
			
			if(ps.isPresent()){
				Protection p = ps.get();
				if(p.getCenter().equals(blockLoc)){
					SpongyPS.getInstance().getProtectionManager().deleteProtection(p);
					player.sendMessage(SpongyPS.getInstance().getLangManager().translate("stone-break", true));
				}
			}
		}
	}
	
	public boolean checkBuild(Location<World> blockLoc, Player player){
		if(player.hasPermission("ps.bypass.build")){
			return false;
		}
		
		Optional<Protection> op = SPSApi.getProtection(blockLoc);
		if(op.isPresent()){
			Protection protection = op.get();
			
			if(protection.getFlag("prevent-build", Boolean.class).orElse(true) && !protection.hasPermission(player.getUniqueId())
					|| !SpongyPS.getInstance().getConfigManger().canMembersBreakProtectionStone()
						&& protection.getCenter().equals(blockLoc) && !protection.getOwner().equals(player.getUniqueId())){
				player.sendMessage(SpongyPS.getInstance().getLangManager().translate("no-build", true));
				return true;
			}
		}
		return false;
	}
}
