package mx.com.rodel.sps.protection;

import org.spongepowered.api.block.BlockType;

public interface ProtectionStone {
	/**
	 * The block that will need to be placed to protect the zone with defined {@link ProtectionStone#getRange()}
	 * 
	 * @return
	 */
	public BlockType getBlockType();
	
	/**
	 * If the range its 11x11 then the stone will protect, 5 block on each side and one in the center <pre>5+5+1 = 11</pre>
	 * 
	 * @return
	 */
	public int getRange();
	
	public String getDisplayName();
}
