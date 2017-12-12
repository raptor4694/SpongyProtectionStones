package mx.com.rodel.sps.limits;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import mx.com.rodel.sps.config.ConfigurationManager;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class LimitsManager {
	
	private ConcurrentMap<String, Group> groups = Maps.newConcurrentMap();

	public void loadLimits(){
		groups.clear();
		Map<Object, ? extends CommentedConfigurationNode> childs = ConfigurationManager.getNode("limits").getChildrenMap();
		
		for(Entry<Object, ? extends CommentedConfigurationNode> child : childs.entrySet()){
			Group group = new Group(child.getKey().toString());
			
			Map<Object, ? extends CommentedConfigurationNode> limits = child.getValue().getChildrenMap();
			
			for(Entry<Object, ? extends CommentedConfigurationNode> limit : limits.entrySet()){
				group.addLimit(limit.getKey().toString(), limit.getValue().getInt());
			}
			
			groups.put(group.getName(), group);
		}
	}
	
	public ImmutableList<Group> getGroups(){
		return ImmutableList.copyOf(groups.values());
	}
}
