/*******************************************************************************
 * Copyright (c) 2011 - Jochen Wuttke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jochen Wuttke (wuttkej@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Dec 13, 2011
 */

package traffic.graph;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import traffic.model.Vehicle;
import traffic.strategy.LookaheadShortestPathVehicleStrategy;


/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class NodeVehicleQueueTest {
	
	private NodeVehicleQueue queue;
	
	@Before
	public void setUp() {
		queue = new NodeVehicleQueue();
	}
	
	@Test
	public void parkNonContainedCar() {
		assertTrue( queue.isEmpty() );
		queue.park( new Vehicle( null, null, new LookaheadShortestPathVehicleStrategy(0), 99) );
		assertTrue( queue.isEmpty() );		
	}
	
	@Test
	public void parkContainedCar() {
		Vehicle c = new Vehicle(null, null, new LookaheadShortestPathVehicleStrategy(0), 99);
		queue.enqueue(c, 4);
		assertFalse(queue.isEmpty());
		assertEquals(1, queue.size() );
		queue.park(c);
		assertTrue(queue.isEmpty());
		assertEquals(0, queue.size());
	}
	
	@Test
	public void moveMultipleDelays() {
		Vehicle c0 = new Vehicle(null, null, new LookaheadShortestPathVehicleStrategy(0), 0);
		Vehicle c1 = new Vehicle(null, null, new LookaheadShortestPathVehicleStrategy(0), 1);
		Vehicle c3 = new Vehicle(null, null, new LookaheadShortestPathVehicleStrategy(0), 3);
		Vehicle c33 = new Vehicle(null, null, new LookaheadShortestPathVehicleStrategy(0), 3);
		Vehicle c5 = new Vehicle(null, null, new LookaheadShortestPathVehicleStrategy(0), 5);
		queue.enqueue(c0, 0);
		queue.enqueue(c1, 1);
		queue.enqueue(c3, 3);
		queue.enqueue(c33, 3);
		queue.enqueue(c5, 5);
		assertFalse(queue.isEmpty());
		Set<Vehicle> s = queue.moveCars();
		assertEquals(1, s.size());
		assertEquals( c0, s.iterator().next() );
		s = queue.moveCars();
		assertEquals(1, s.size());
		s = queue.moveCars();
		assertNull( "No cars expected.", s );
		s = queue.moveCars();
		assertEquals( 2, s.size() );
		s = queue.moveCars();
		assertNull( s );
		s = queue.moveCars();
		assertEquals(1, s.size());
		assertTrue( queue.isEmpty() );
	}

}
