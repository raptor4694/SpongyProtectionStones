package mx.com.rodel.sps.protection;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.common.collect.Maps;

import mx.com.rodel.sps.SpongyPS;
import mx.com.rodel.sps.config.ConfigurationManager;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class ProtectionManager {
	// It can be changed while players are placing stones
	private ConcurrentMap<BlockType, ProtectionStone> stoneTypes = Maps.newConcurrentMap();
	
	private SpongyPS pl;
	
	public ProtectionManager(SpongyPS pl) {
		this.pl = pl;
	}
	
	public void loadStones(){
		stoneTypes.clear();
		Map<Object, ? extends CommentedConfigurationNode> childs = ConfigurationManager.getNode("stones").getChildrenMap();

		for(Entry<Object, ? extends CommentedConfigurationNode> node : childs.entrySet()){
			try {
				ProtectionStone stone = getStone(node.getValue());
				stoneTypes.put(stone.getBlockType(), stone);
			} catch (Exception e) {
				pl.getLogger().warn("Error loading protection stone {}:", node.getKey());
				e.printStackTrace();
			}
		}
		
	}
	
	private ProtectionStone getStone(CommentedConfigurationNode node) throws Exception{
		int range = node.getNode("range").getInt();
		String displayName = node.getNode("display-name").getString();
		
		String stype = node.getNode("block-type").getString();
		Optional<BlockType> optional = Sponge.getRegistry().getType(BlockType.class, stype);
		BlockType type = optional.orElseThrow(() -> new Exception("Cannot found block id "+stype));
	
		return new ProtectionStone() {
			@Override
			public int getRange() {
				return range;
			}
			
			@Override
			public BlockType getBlockType() {
				return type;
			}
			
			@Override
			public String getDisplayName() {
				return displayName;
			}
		};
		
	}
	
	public void isRegion(Location<World> world){
		
	}
}
