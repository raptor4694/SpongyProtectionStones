package mx.com.rodel.sps.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;

public class Helper {
	public static boolean isInside(int x1, int y1, int z1, int x2, int y2, int z2, int x, int y, int z){
		int maxX = Math.max(x1, x2);
		int maxY = Math.max(y1, y2);
		int maxZ = Math.max(z1, z2);
		int minX = Math.min(x1, x2);
		int minY = Math.min(y1, y2);
		int minZ = Math.min(z1, z2);
		
		return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
	}
	
	public static <V, K> Entry<K, V> entry(K key, V value){
		return new Entry<K, V>() {
			private V val = value;
			
			@Override
			public K getKey() {
				return key;
			}
			
			@Override
			public V getValue() {
				return value;
			}
			
			@Override
			public V setValue(V value) {
				V old = val;
				val = value;
				return old;
			}
		};
	}
	
	
	public static List<Vector3i> calculateCenter(Location<World> center){
		Vector3i max = clampCoords(center.copy().sub(5, 5, 5).getBlockPosition());
		Vector3i min = clampCoords(center.copy().sub(-5, -5, -5).getBlockPosition());
		Vector3i rmax = max.max(min);
		Vector3i rmin = max.min(min);
		return Arrays.asList(new Vector3i[] {rmax, rmin});
	}
	
	public static Vector3i clampCoords(Vector3i vec){
		return new Vector3i(vec.getX(), clamp(vec.getY(), 0, 255), vec.getZ());
		
	}
	
	public static int clamp(int val, int min, int max) {
	    return Math.max(min, Math.min(max, val));
	}
	
	public static double clamp(double val, double min, double max) {
	    return Math.max(min, Math.min(max, val));
	}
	
	public static String format(String string, Object... arguments){
		for (int i = 0; i < arguments.length; i++) {
			System.out.println(arguments[i]);
			string = string.replace("{"+i+"}", arguments[i].toString());
		}
		return string;
	}
	
	public static Optional<Player> playerCause(Cause cause){
		return cause.first(Player.class);
	}
	
	public static Text chatColor(String text){
		return TextSerializers.FORMATTING_CODE.deserialize(text);
	}
}
