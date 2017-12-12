package mx.com.rodel.sps.protection;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.text.Text;

import mx.com.rodel.sps.utils.Helper;

public class ProtectionStone {
	private String name;
	private BlockType blockType;
	private int range;
	private String displayName;

	public ProtectionStone(String name, BlockType blockType, int range, String displayName) {
		this.name = name;
		this.blockType = blockType;
		this.range = range;
		this.displayName = displayName;

	}
	
	public String getName(){
		return name;
	}
	
	public BlockType getBlockType(){
		return blockType;
	}
	
	public int getRange(){
		return range;
	}
	
	public String getDisplayName(){
		return displayName;
	}
	
	public Text toText(){
		return Helper.chatColor("&6"+displayName+"\n&c>> Name: &a"+name+"\n&c>> Block: &a"+blockType.getId()+"\n&c>> Range: &a"+range);
	}
}