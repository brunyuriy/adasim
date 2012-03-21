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

package traffic.strategy;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

import traffic.graph.Graph;
import traffic.graph.GraphNode;
import traffic.model.ConfigurationException;
import traffic.model.SimulationXMLReader;
import traffic.model.TrafficSimulator;

/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class AlwaysRecomputeVehicleStrategyTest {
	
	private VehicleStrategy strategy = new AlwaysRecomputeVehicleStrategy();

	@Test(timeout=10000)
	public void cityMapLifelock() throws FileNotFoundException, ConfigurationException {
		TrafficSimulator sim = SimulationXMLReader.buildSimulator( new File("resources/test/168-1280-0.xml") );
		sim.run();

		//		strategy.setGraph(g);
//		List<GraphNode> path = strategy.getPath( g.getNode(1), g.getNode(5));
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
