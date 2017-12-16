package mx.com.rodel.sps.limits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import org.spongepowered.api.entity.living.player.Player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.config.ConfigurationManager;
import mx.com.rodel.sps.protection.ProtectionStone;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class LimitsManager {
	
	private ConcurrentMap<String, Group> groups = Maps.newConcurrentMap();

	public void loadLimits(){
		groups.clear();
		Map<Object, ? extends CommentedConfigurationNode> childs = ConfigurationManager.getNode("limits").getChildrenMap();
		
		for(Entry<Object, ? extends CommentedConfigurationNode> child : childs.entrySet()){
			Group group = new Group(child.getKey().toString(), child.getValue().getNode("priority").getInt(0));
			
			Map<Object, ? extends CommentedConfigurationNode> limits = child.getValue().getNode("stones").getChildrenMap();
			
			for(Entry<Object, ? extends CommentedConfigurationNode> limit : limits.entrySet()){
				group.addLimit(limit.getKey().toString(), limit.getValue().getInt());
			}
			
			groups.put(group.getName(), group);
		}
	}
	
	public ImmutableList<Group> getGroups(){
		return ImmutableList.copyOf(groups.values());
	}
	
	/**
	 * Note: This method access to DB, it should be called in async is its possible
	 * 
	 * @param player
	 * @return
	 */
	public ImmutableMap<ProtectionStone, Integer> getLimits(Player player){
		HashMap<ProtectionStone, Integer> limits = new HashMap<>();
		Optional<Group> ogroup = getPlayerGroup(player);
		
		for(ProtectionStone stone : SpongyPS.getInstance().getProtectionManager().getStones()){
			if(ogroup.isPresent()){
				limits.put(stone, ogroup.get().getLimit(stone.getName()).orElse(0)-SpongyPS.getInstance().getDatabaseManger().countProtectionsOfType(player.getUniqueId(), stone.getName()));
			}else{
				limits.put(stone, 0);
			}
		}
		return ImmutableMap.copyOf(limits);
	}
	
	public Optional<Group> getPlayerGroup(Player player){
		List<Group> results = new ArrayList<>();
		for(Entry<String, Group> group : groups.entrySet()){
			if(player.hasPermission(group.getValue().getPermission())){
				results.add(group.getValue());
			}
		}
		
		if(results.isEmpty()){
			return getDefaultGroup();
		}
		
		if(results.size()==1){
			return Optional.of(results.get(0));
		}
		
		// Order in desc by priority, the highest will be in the index 0
		results.sort((Group o1, Group o2)->Integer.compare(o2.getPriority(), o1.getPriority()));

		results.forEach(System.out::println);
		
		return Optional.of(results.get(0));
	}
	
	public Optional<Group> getDefaultGroup(){
		return Optional.ofNullable(groups.get("default"));
	}
	
	public int getStoneLimits(UUID player, String stoneName){
		return SpongyPS.getInstance().getDatabaseManger().countProtectionsOfType(player, stoneName);
	}
	
}
