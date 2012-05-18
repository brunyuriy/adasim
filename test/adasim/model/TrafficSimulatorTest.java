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
 * Created: Nov 29, 2011
 */

package adasim.model;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;
import org.junit.Test;

import adasim.LoggingTest;
import adasim.algorithm.routing.LookaheadShortestPathRoutingAlgorithm;
import adasim.algorithm.routing.RoutingAlgorithm;
import adasim.model.ConfigurationException;
import adasim.model.TrafficSimulator;
import adasim.model.internal.SimulationXMLReader;



/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class TrafficSimulatorTest extends LoggingTest {
	
//	@Test
//	public void zeroLengthPathTest() throws FileNotFoundException, ConfigurationException {
//		TrafficSimulator sim = SimulationXMLReader.buildSimulator( new File( "resources/test/random.xml" ) );
//		sim.run();
//	}
	
	@Test
	public void basicConfigTest() throws FileNotFoundException, ConfigurationException {
		TrafficSimulator sim = SimulationXMLReader.buildSimulator( new File( "resources/test/shortest-path-test-weights.xml" ) );
		sim.run();
	}
	
	@Test
	public void addingVehiclesTest() throws FileNotFoundException, ConfigurationException {
		TrafficSimulator sim = SimulationXMLReader.buildSimulator( new File( "resources/test/only-delayed-cars.xml" ) );
		sim.run();
	}

	@Test
	public void loadingAgentsTest() throws FileNotFoundException, ConfigurationException {
		TrafficSimulator sim = SimulationXMLReader.buildSimulator( new File( "resources/test/config-with-closure.xml" ) );
		sim.run();
	}

	@Test
	public void stepByStepSimulationRun() throws JDOMException, IOException, ConfigurationException {
		TrafficSimulator sim = SimulationXMLReader.buildSimulator( new File("resources/test/end-to-end.xml") );
		AdasimMap g = sim.getMap();
		RoutingAlgorithm strategy = new LookaheadShortestPathRoutingAlgorithm(0);
		strategy.setMap(g);
		List<RoadSegment> path = strategy.getPath(g.getRoadSegment(0), g.getRoadSegment(2));
		assertNotNull( "No path found", path );
		assertEquals( "Path has wrong length", 4, path.size() );
		assertEquals( 7, (int)path.get(0).getID() );
		assertEquals( 1, (int)path.get(1).getID() );
		assertEquals( 5, (int)path.get(2).getID() );
		assertEquals( 2, (int)path.get(3).getID() );
		path = strategy.getPath(g.getRoadSegment(4), g.getRoadSegment(7));
		assertNotNull( "No path found", path );
		assertEquals( "Path has wrong length", 4, path.size() );
		assertEquals( 6, (int)path.get(0).getID() );
		assertEquals( 5, (int)path.get(1).getID() );
		assertEquals( 0, (int)path.get(2).getID() );
		assertEquals( 7, (int)path.get(3).getID() );
		path = strategy.getPath(g.getRoadSegment(3), g.getRoadSegment(9));
		assertNotNull( "No path found", path );
		assertEquals( "Path has wrong length", 3, path.size() );
		assertEquals( 1, (int)path.get(0).getID() );
		assertEquals( 5, (int)path.get(1).getID() );
		assertEquals( 9, (int)path.get(2).getID() );
		path = strategy.getPath(g.getRoadSegment(10), g.getRoadSegment(5));
		assertNull( "There should be no path from 10 to 5, but I found " + path, path );

		assertFalse( "Vehicle ready to stop", sim.getVehicle(0).isFinished() );
		assertFalse( "Vehicle ready to stop", sim.getVehicle(1).isFinished() );
		assertFalse( "Vehicle ready to stop", sim.getVehicle(2).isFinished() );
		assertFalse( "Vehicle ready to stop", sim.getVehicle(4).isFinished() );
		sim.takeSimulationStep();	//cycle 1
		sim.takeSimulationStep();	//cycle 2
		assertFalse( "Vehicle ready to stop", sim.getVehicle(0).isFinished() );
		assertFalse( "Vehicle ready to stop", sim.getVehicle(1).isFinished() );
		assertFalse( "Vehicle ready to stop", sim.getVehicle(2).isFinished() );
		assertTrue( "Vehicle not ready to stop", sim.getVehicle(4).isFinished() );
		sim.takeSimulationStep();	//cycle 3
		assertFalse("Simulator stopped", sim.isFinished() );
		sim.takeSimulationStep();	//cycle 4
		assertFalse("Simulator stopped", sim.isFinished() );
		sim.takeSimulationStep();	//cycle 5
		assertFalse("Simulator stopped", sim.isFinished() );
		sim.takeSimulationStep();	//cycle 6
		assertFalse("Simulator stopped", sim.isFinished() );
		sim.takeSimulationStep();	//cycle 7
		assertFalse("Simulator stopped", sim.isFinished() );
		sim.takeSimulationStep();	//cycle 8
		assertTrue("Simulator not stopping", sim.isFinished() );
	}
}
