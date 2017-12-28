package mx.com.rodel.sps.utils;

public class WorldNotFoundException extends Exception{
	private static final long serialVersionUID = 1857029809909492336L;
	
	public WorldNotFoundException(String world) {
		super("Cannot find world: "+world);
	}
}
