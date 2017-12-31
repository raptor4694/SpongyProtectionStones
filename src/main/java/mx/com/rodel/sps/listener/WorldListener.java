package mx.com.rodel.sps.listener;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.LoadWorldEvent;

import mx.com.rodel.sps.SpongyPS;

public class WorldListener {
	@Listener
	public void onWorldLoad(LoadWorldEvent e){
		SpongyPS.getInstance().getLogger().info("Loading protection stones in {}", e.getTargetWorld().getName());
		int size = SpongyPS.getInstance().getProtectionManager().loadProtections(e.getTargetWorld());
		SpongyPS.getInstance().getLogger().info("{} stones loaded!", size);
	}
}
