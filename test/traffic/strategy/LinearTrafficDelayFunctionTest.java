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

package traffic.strategy;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the Linear Speed Strategy class
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 */

public class LinearTrafficDelayFunctionTest {

	private LinearTrafficDelayFunction strategy;
	
	@Before
	public void setUp() {
		strategy = new LinearTrafficDelayFunction();
	}
	
	@Test
	public void testCarsBelowCapacity() {
		int weight = 3;
		int capacity = 5;
		int number = 2;
		assertEquals(strategy.getDelay(weight, capacity, number), 3);
		number = 4;
		assertEquals(strategy.getDelay(weight, capacity, number), 3);
		capacity = 3;
		number = 2;
		weight = 4;
		assertEquals(strategy.getDelay(weight, capacity, number), 4);
	}
	
	@Test
	public void testCarsAboveCapacity() {
		int weight = 3;
		int capacity = 2;
		int number = 5;
		assertEquals(strategy.getDelay(weight, capacity, number), 6);
		number = 4;
		weight = 8;
		assertEquals(strategy.getDelay(weight, capacity, number), 10);
		capacity = 3;
		number = 4;
		weight = 4;
		assertEquals(strategy.getDelay(weight, capacity, number), 5);
	}
	
	@Test
	public void testCarsAtCapacity() {
		int weight = 3;
		int capacity = 2;
		int number = 2;
		assertEquals(strategy.getDelay(weight, capacity, number), 3);
		number = 4;
		capacity = 4;
		assertEquals(strategy.getDelay(weight, capacity, number), 3);
		capacity = 3;
		number = 3;
		weight = 5;
		assertEquals(strategy.getDelay(weight, capacity, number), 5);
	}

}
