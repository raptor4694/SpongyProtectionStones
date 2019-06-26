package mx.com.rodel.sps.protection;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockState.StateMatcher;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.text.Text;

import com.google.common.base.Objects;

import mx.com.rodel.sps.utils.Helper;

public class ProtectionStone {
	private String name;
	private StateMatcher stateMatcher;
	private BlockState template;
	private int range;
	private String displayName;
	
	public ProtectionStone(String name, BlockState template, StateMatcher stateMatcher, int range, String displayName) {
		this.name = name;
		this.stateMatcher = stateMatcher;
		this.range = range;
		this.displayName = displayName;
	}
	
	@Deprecated
	public ProtectionStone(String name, BlockType blockType, int range, String displayName) {
		this(name, blockType.getDefaultState(), BlockState.matcher(blockType).build(), range, displayName);
	}

	public String getName(){
		return name;
	}
	
	public StateMatcher getStateMatcher() {
		return stateMatcher;
	}
	
	public BlockType getBlockType() {
		return template.getType();
	}
	
	public BlockState getTemplate() {
		return template;
	}
	
	public int getRange(){
		return range;
	}
	
	public String getDisplayName(){
		return displayName;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("name", name)
				.add("stateMatcher", stateMatcher)
				.add("range", range)
				.add("displayName", displayName)
				.toString();
	}
	
	public Text toText(){
		return Helper.chatColor("&6"+displayName+"\n&c>> Name: &a"+name+"\n&c>> BlockState: &a"+stateMatcher+"\n&c>> Range: &a"+range);
	}
}