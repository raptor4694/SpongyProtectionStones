package mx.com.rodel.sps.utils;

import java.util.Map.Entry;

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
	
	public static String format(String string, Object... arguments){
		for (int i = 0; i < arguments.length; i++) {
			System.out.println(arguments[i]);
			string = string.replace("{"+i+"}", arguments[i].toString());
		}
		return string;
	}
}
