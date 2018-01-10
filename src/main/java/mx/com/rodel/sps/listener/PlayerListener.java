package mx.com.rodel.sps.listener;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.api.SPSApi;
import mx.com.rodel.sps.protection.Protection;
import mx.com.rodel.sps.utils.Helper;

public class PlayerListener {
	@Listener
	public void onJoin(ClientConnectionEvent.Join e){
		Sponge.getScheduler().createTaskBuilder().async().execute(()->{
			SpongyPS.getInstance().getDatabaseManger().updatePlayerName(e.getTargetEntity());
		}).submit(SpongyPS.getInstance());
	}

	@Listener
	public void onPlayerMove(MoveEntityEvent e){
		if(e.getTargetEntity() instanceof Player){
			Location<World> from = e.getFromTransform().getLocation();
			Location<World> to = e.getToTransform().getLocation();

			int fX = from.getBlockX();
			int fY = from.getBlockY();
			int fZ = from.getBlockZ();
			int tX = to.getBlockX();
			int tY = to.getBlockY();
			int tZ = to.getBlockZ();
			

			// Check if player moves one block
			if(fX!=tX || fY!=tY || fZ!=tZ){
				
				// Find protection
				Optional<Protection> pFrom = SPSApi.getProtection(new Location<World>(from.getExtent(), from.getBlockPosition()));
				Optional<Protection> pTo = SPSApi.getProtection(new Location<World>(to.getExtent(), to.getBlockPosition()));

				if(pTo.isPresent() && (!pFrom.isPresent() || pFrom.get().getID()!=pTo.get().getID())){
					Optional<String> message = pTo.get().getFlag("welcome-message", String.class);
					if(message.isPresent() && !message.get().trim().isEmpty()){
						Player player = (Player) e.getTargetEntity();
						player.sendMessage(Helper.chatColor(message.get()));
					}
					return;
				}
				
				if(pFrom.isPresent() && (!pTo.isPresent() || pTo.get().getID()!=pFrom.get().getID())){
					Optional<String> message = pFrom.get().getFlag("farewell-message", String.class);
					if(message.isPresent() && !message.get().trim().isEmpty()){
						Player player = (Player) e.getTargetEntity();
						player.sendMessage(Helper.chatColor(message.get()));
					}
				}
			}
		}
	}
}
