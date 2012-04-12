/*******************************************************************************
 * Copyright (c) 2011 - Jonathan Ramaswamy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jonathan Ramaswamy (ramaswamyj12@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Jan 31, 2012
 */

package traffic.model;


import java.util.List;

import org.junit.Before;
import org.junit.Test;

import traffic.algorithm.AbstractVehicleStrategy;
import traffic.algorithm.delay.LinearTrafficDelayFunction;
import traffic.model.RoadSegment;
import traffic.model.Vehicle;
import static org.junit.Assert.*;


/**
 * Tests the RoadSegment class
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 */

public class RoadSegmentTest {

	private RoadSegment node, node2;
	
	@Before
	public void setUp() {
		node = new RoadSegment(0, new LinearTrafficDelayFunction(), 1, 0);
		node2 = new RoadSegment(1, new LinearTrafficDelayFunction(), 1,0 ) ;
		node.addEdge( node2 );
	}
	
	@Test
	public void testNodeDelay() {
		Vehicle c = new Vehicle(null, null, null, 0);
		node.enterNode(c);
		c = new Vehicle(null, null, null, 1);
		node.enterNode(c);
		c = new Vehicle(null, null, null, 2);
		node.enterNode(c);
		//Number < Capacity
		assertEquals(1, node.getDelay());
		assertEquals(4, node.getCurrentDelay());
		//Number = Capacity
		node.setCapacity(3);
		assertEquals(1, node.getDelay());
		assertEquals(1, node.getCurrentDelay() );
		//Number > Capacity
		Vehicle c2 = new Vehicle(null, null, null, 3);
		node.enterNode(c2);
		assertEquals(1, node.getDelay());
		assertEquals(2, node.getCurrentDelay());
	}
	
	@Test
	public void invalidRoutingPrevented() {
		Vehicle v = new Vehicle( node, null, new AbstractVehicleStrategy() {
			
			@Override
			public List<RoadSegment> getPath(RoadSegment from, RoadSegment to) {
				return null;
			}
			
			@Override
			public RoadSegment getNextNode() {
				return new RoadSegment(2, null, 4);
			}
		}, 1 );
		
		node.enterNode(v);
		assertEquals( 2, node.getCurrentDelay() );	//this should imply that the car is still waiting
		node.takeSimulationStep(1);
		node.takeSimulationStep(2);
		assertEquals( 1, node.getCurrentDelay() );	//this should imply that the car has been removed
	}

	@Test
	public void validRoutingConfirmed() {
		Vehicle v = new Vehicle( node, null, new AbstractVehicleStrategy() {
			
			@Override
			public List<RoadSegment> getPath(RoadSegment from, RoadSegment to) {
				return null;
			}
			
			@Override
			public RoadSegment getNextNode() {
				return node2;
			}
		}, 1 );
		
		node.enterNode(v);
		assertEquals( 2, node.getCurrentDelay() );	//this should imply that the car is still waiting
		assertEquals( 1, node2.getCurrentDelay() );	//this should imply that the car is still waiting
		node.takeSimulationStep(1);
		node.takeSimulationStep(2);
		assertEquals( 1, node.getCurrentDelay() );	//this should imply that the car has been removed
		assertEquals( 2, node2.getCurrentDelay() );	//this should imply that the car is still waiting
	}
}
