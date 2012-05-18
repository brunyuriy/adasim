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
 * Created: Nov 22, 2011
 */

package adasim.algorithm.routing;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;

import adasim.LoggingTest;
import adasim.algorithm.routing.LookaheadShortestPathRoutingAlgorithm;
import adasim.model.AdasimMap;
import adasim.model.ConfigurationException;
import adasim.model.RoadSegment;
import adasim.model.Vehicle;
import adasim.model.internal.SimulationXMLReader;
import static org.junit.Assert.*;



/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class LookaheadShortestPathRoutingAlgorithmTest extends LoggingTest {
	
	private LookaheadShortestPathRoutingAlgorithm strategy;
	
	@Before
	public void setUp() {
		strategy = new LookaheadShortestPathRoutingAlgorithm(0);
	}
	
	@Test
	public void findShortestPathFromStartNoWeights() throws JDOMException, IOException, ConfigurationException {
		AdasimMap g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test.xml") ).getMap();
		strategy.setMap(g);
		List<RoadSegment> path = strategy.getPath( g.getRoadSegment(1), g.getRoadSegment(5));
		assertNotNull( "No path found", path );
		assertEquals( "Path too short", 2, path.size() );
		assertEquals( 6, path.get(0).getID() );
		path = strategy.getPath(g.getRoadSegment(1), g.getRoadSegment(4));
		assertNotNull( "No path found", path );
		assertEquals( "Path too short", 2, path.size() );
		assertTrue( 3 == (int)path.get(0).getID() || 2 == (int)path.get(0).getID() );
		path = strategy.getPath( g.getRoadSegment(0), g.getRoadSegment(4));
		assertNotNull( "No path found", path );
		assertEquals( "Path too short", 3, path.size() );
		assertTrue( 3 == (int)path.get(1).getID() || 5 == (int)path.get(1).getID() );	
	}
	
	@Test
	public void findShortestPathFromStartNoWeightsRandom() throws JDOMException, IOException, ConfigurationException {
		AdasimMap g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test-random-ids.xml") ).getMap();
		strategy.setMap(g);
		List<RoadSegment> path = strategy.getPath( g.getRoadSegment(21), g.getRoadSegment(5));
		assertNotNull( "No path found", path );
		assertEquals( "Path too short", 2, path.size() );
		assertEquals( 60, (int)path.get(0).getID() );
		path = strategy.getPath(g.getRoadSegment(21), g.getRoadSegment(8));
		assertNotNull( "No path found", path );
		assertEquals( "Path too short", 2, path.size() );
		assertTrue( 3 == (int)path.get(0).getID() || 12 == (int)path.get(0).getID() );
		path = strategy.getPath(g.getRoadSegment(0), g.getRoadSegment(8));
		assertNotNull( "No path found", path );
		assertEquals( "Path too short", 3, path.size() );
		assertTrue( 3 == (int)path.get(1).getID() || 5 == (int)path.get(1).getID() );	
	}
	
	@Test
	public void findShortestPathFromStartWithWeights() throws JDOMException, IOException, ConfigurationException {
		AdasimMap g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test-weights.xml") ).getMap();
		strategy.setMap(g);
		List<RoadSegment> path = strategy.getPath(g.getRoadSegment(6), g.getRoadSegment(4));
		assertNotNull( "No path found", path );
		assertEquals( "Path has wrong length", 3, path.size() );
		assertEquals( 1, (int)path.get(0).getID() );
		assertEquals( 2, (int)path.get(1).getID() );
	}

	@Test
	public void findShortestPathFromStartWithWeightsLookahead1() throws JDOMException, IOException, ConfigurationException {
		AdasimMap g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test-weights.xml") ).getMap();
		strategy = new LookaheadShortestPathRoutingAlgorithm(1);
		strategy.setMap(g);
		List<RoadSegment> path = strategy.getPath(g.getRoadSegment(6), g.getRoadSegment(4));
		assertNotNull( "No path found", path );
		assertEquals( "Path has wrong length", 3, path.size() );
		assertEquals( 1, (int)path.get(0).getID() );
		assertEquals( 2, (int)path.get(1).getID() );
	}

	@Test
	public void findShortestPathFromStartWithWeightsLookahead2() throws JDOMException, IOException, ConfigurationException {
		AdasimMap g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test-weights.xml") ).getMap();
		strategy = new LookaheadShortestPathRoutingAlgorithm(2);
		strategy.setMap(g);
		List<RoadSegment> path = strategy.getPath(g.getRoadSegment(6), g.getRoadSegment(4));
		assertNotNull( "No path found", path );
		assertEquals( "Path has wrong length", 2, path.size() );
		assertEquals( 5, (int)path.get(0).getID() );
		assertEquals( 4, (int)path.get(1).getID() );
	}

	@Test
	public void recomputeShortestPathFromStartWithWeightsLookahead1() throws JDOMException, IOException, ConfigurationException {
		AdasimMap g = SimulationXMLReader.buildSimulator( new File("resources/test/lookahead-recompute-test.xml") ).getMap();
		strategy = new LookaheadShortestPathRoutingAlgorithm(1);	//update after every step
		strategy.setMap(g);
		strategy.setVehicle( new Vehicle(null, null, null, 0) );
		strategy.setStartRoad(g.getRoadSegment(0));
		strategy.setEndRoad(g.getRoadSegment(4));
		
		List<RoadSegment> firstPath = strategy.getPath(g.getRoadSegment(0), g.getRoadSegment(4));
		assertEquals( 6, firstPath.get(0).getID() );
		assertEquals( 1, firstPath.get(1).getID() );
		assertEquals( 2, firstPath.get(2).getID() );
		assertEquals( 4, firstPath.get(3).getID() );
		
		//now we load the graph with some cars to force a new path, these will make the firstPath too expensive
		g.addVehicleAtSegment( new Vehicle( g.getRoadSegment(1), null, new LookaheadShortestPathRoutingAlgorithm(), 42 ), 1 );
		g.addVehicleAtSegment( new Vehicle( g.getRoadSegment(1), null, new LookaheadShortestPathRoutingAlgorithm(), 43 ), 1 );
		
		RoadSegment next = strategy.getNextNode();	//returns the first node of the first path and updates
		assertEquals( 6, next.getID() );	//
		assertEquals( 5, strategy.getNextNode().getID() );	//this is the next node on the new updated path
	}
	
}
