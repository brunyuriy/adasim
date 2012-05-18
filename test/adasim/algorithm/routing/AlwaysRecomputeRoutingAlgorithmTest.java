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
 * Created: Jan 19, 2012
 */

package adasim.algorithm.routing;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;

import adasim.LoggingTest;
import adasim.model.ConfigurationException;
import adasim.model.TrafficSimulator;
import adasim.model.internal.SimulationXMLReader;


/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class AlwaysRecomputeRoutingAlgorithmTest extends LoggingTest {
	
	@Test(timeout=10000)
	public void cityMapLifelock() throws FileNotFoundException, ConfigurationException {
		TrafficSimulator sim = SimulationXMLReader.buildSimulator( new File("resources/test/168-1280-0.xml") );
		sim.run();
		//this test passes if it does not time out. We take this as an indication that there is no lifelock.

		//		strategy.setGraph(g);
//		List<RoadSegment> path = strategy.getPath( g.getNode(1), g.getNode(5));
//		assertNotNull( "No path found", path );
//		assertEquals( "Path too short", 2, path.size() );
//		assertEquals( 6, path.get(0).getID() );
//		path = strategy.getPath(g.getNode(1), g.getNode(4));
//		assertNotNull( "No path found", path );
//		assertEquals( "Path too short", 2, path.size() );
//		assertTrue( 3 == (int)path.get(0).getID() || 2 == (int)path.get(0).getID() );
//		path = strategy.getPath( g.getNode(0), g.getNode(4));
//		assertNotNull( "No path found", path );
//		assertEquals( "Path too short", 3, path.size() );
//		assertTrue( 3 == (int)path.get(1).getID() || 5 == (int)path.get(1).getID() );	
	}
	
	@Test
	public void cityMapLifelock2() throws FileNotFoundException, ConfigurationException {
		TrafficSimulator sim = SimulationXMLReader.buildSimulator( new File("resources/test/lifelock-city-1.xml") );
		sim.run();
	}


}
