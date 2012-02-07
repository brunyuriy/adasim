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

package traffic.graph;


import org.junit.Before;
import org.junit.Test;

import traffic.model.Vehicle;
import traffic.strategy.LinearSpeedStrategy;
import static org.junit.Assert.*;


/**
 * Tests the GraphNode class
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 */

public class GraphNodeTest {

	private GraphNode node;
	
	@Before
	public void setUp() {
		int delay = 3;
		int capacity = 5;
		node = new GraphNode(0, new LinearSpeedStrategy(), delay, capacity);
		Vehicle c = new Vehicle(null, null, null, 0);
		node.enterNode(c);
		c = new Vehicle(null, null, null, 1);
		node.enterNode(c);
		c = new Vehicle(null, null, null, 2);
		node.enterNode(c);
	}
	
	@Test
	public void testNodeDelay() {
		//Number < Capacity
		assertEquals(node.getDelay(), 3);
		assertEquals(node.getCurrentDelay(), 3);
		//Number = Capacity
		node.setCapacity(3);
		assertEquals(node.getDelay(), 3);
		assertEquals(node.getCurrentDelay(), 3);
		//Number > Capacity
		Vehicle c = new Vehicle(null, null, null, 3);
		node.enterNode(c);
		assertEquals(node.getDelay(), 3);
		assertEquals(node.getCurrentDelay(), 4);
	}
	
	

}
