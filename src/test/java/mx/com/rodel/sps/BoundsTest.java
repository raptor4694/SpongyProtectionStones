package mx.com.rodel.sps;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.flowpowered.math.vector.Vector3d;

import mx.com.rodel.sps.utils.Helper;

public class BoundsTest {
	@Test
	public void boundsTest(){
		Vector3d p1 = new Vector3d(10, -10, 10);
		Vector3d p2 = new Vector3d(20, 10, 20);
		Vector3d p = new Vector3d(10, -10, 10);
		
		// Fixed Min & Max
		assertTrue(Helper.isInside(p1.getFloorX(), p1.getFloorY(), p1.getFloorZ(), p2.getFloorX(), p2.getFloorY(), p2.getFloorZ(), p.getFloorX(), p.getFloorY(), p.getFloorZ()));
		
		// Max & Min reversed
		assertTrue(Helper.isInside(p2.getFloorX(), p2.getFloorY(), p2.getFloorZ(), p1.getFloorX(), p1.getFloorY(), p1.getFloorZ(), p.getFloorX(), p.getFloorY(), p.getFloorZ()));
		
		// Outside down
		p = new Vector3d(10, -11, 10);
		assertFalse(Helper.isInside(p1.getFloorX(), p1.getFloorY(), p1.getFloorZ(), p2.getFloorX(), p2.getFloorY(), p2.getFloorZ(), p.getFloorX(), p.getFloorY(), p.getFloorZ()));
		
		// Outside up
		p = new Vector3d(10, 11, 10);
		assertFalse(Helper.isInside(p1.getFloorX(), p1.getFloorY(), p1.getFloorZ(), p2.getFloorX(), p2.getFloorY(), p2.getFloorZ(), p.getFloorX(), p.getFloorY(), p.getFloorZ()));
		
		// Outside corner
		p = new Vector3d(21, 0, 21);
		assertFalse(Helper.isInside(p1.getFloorX(), p1.getFloorY(), p1.getFloorZ(), p2.getFloorX(), p2.getFloorY(), p2.getFloorZ(), p.getFloorX(), p.getFloorY(), p.getFloorZ()));
	}
}
