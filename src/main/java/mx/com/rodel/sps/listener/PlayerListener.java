package mx.com.rodel.sps.listener;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import mx.com.rodel.sps.SpongyPS;

public class PlayerListener {
	@Listener
	public void onJoin(ClientConnectionEvent.Join e){
		Sponge.getScheduler().createTaskBuilder().async().execute(()->{
			SpongyPS.getInstance().getDatabaseManger().updatePlayerName(e.getTargetEntity());
		}).submit(SpongyPS.getInstance());
	}
}
