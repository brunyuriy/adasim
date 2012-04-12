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

package traffic.strategy;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import traffic.model.AdasimMap;
import traffic.model.GraphNode;
import traffic.model.Vehicle;
import traffic.model.ConfigurationException;
import traffic.model.internal.SimulationXMLReader;


/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class LookaheadShortestPathVehicleStrategyTest {
	
	private LookaheadShortestPathVehicleStrategy strategy;
	
	@Before
	public void setUp() {
		strategy = new LookaheadShortestPathVehicleStrategy(0);
	}
	
	@Test
	public void findShortestPathFromStartNoWeights() throws JDOMException, IOException, ConfigurationException {
		AdasimMap g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test.xml") ).getGraph();
		strategy.setGraph(g);
		List<GraphNode> path = strategy.getPath( g.getNode(1), g.getNode(5));
		assertNotNull( "No path found", path );
		assertEquals( "Path too short", 2, path.size() );
		assertEquals( 6, path.get(0).getID() );
		path = strategy.getPath(g.getNode(1), g.getNode(4));
		assertNotNull( "No path found", path );
		assertEquals( "Path too short", 2, path.size() );
		assertTrue( 3 == (int)path.get(0).getID() || 2 == (int)path.get(0).getID() );
		path = strategy.getPath( g.getNode(0), g.getNode(4));
		assertNotNull( "No path found", path );
		assertEquals( "Path too short", 3, path.size() );
		assertTrue( 3 == (int)path.get(1).getID() || 5 == (int)path.get(1).getID() );	
	}
	
	@Test
	public void findShortestPathFromStartNoWeightsRandom() throws JDOMException, IOException, ConfigurationException {
		AdasimMap g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test-random-ids.xml") ).getGraph();
		strategy.setGraph(g);
		List<GraphNode> path = strategy.getPath( g.getNode(21), g.getNode(5));
		assertNotNull( "No path found", path );
		assertEquals( "Path too short", 2, path.size() );
		assertEquals( 60, (int)path.get(0).getID() );
		path = strategy.getPath(g.getNode(21), g.getNode(8));
		assertNotNull( "No path found", path );
		assertEquals( "Path too short", 2, path.size() );
		assertTrue( 3 == (int)path.get(0).getID() || 12 == (int)path.get(0).getID() );
		path = strategy.getPath(g.getNode(0), g.getNode(8));
		assertNotNull( "No path found", path );
		assertEquals( "Path too short", 3, path.size() );
		assertTrue( 3 == (int)path.get(1).getID() || 5 == (int)path.get(1).getID() );	
	}
	
	@Test
	public void findShortestPathFromStartWithWeights() throws JDOMException, IOException, ConfigurationException {
		AdasimMap g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test-weights.xml") ).getGraph();
		strategy.setGraph(g);
		List<GraphNode> path = strategy.getPath(g.getNode(6), g.getNode(4));
		assertNotNull( "No path found", path );
		assertEquals( "Path has wrong length", 3, path.size() );
		assertEquals( 1, (int)path.get(0).getID() );
		assertEquals( 2, (int)path.get(1).getID() );
	}

	@Test
	public void findShortestPathFromStartWithWeightsLookahead1() throws JDOMException, IOException, ConfigurationException {
		AdasimMap g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test-weights.xml") ).getGraph();
		strategy = new LookaheadShortestPathVehicleStrategy(1);
		strategy.setGraph(g);
		List<GraphNode> path = strategy.getPath(g.getNode(6), g.getNode(4));
		assertNotNull( "No path found", path );
		assertEquals( "Path has wrong length", 3, path.size() );
		assertEquals( 1, (int)path.get(0).getID() );
		assertEquals( 2, (int)path.get(1).getID() );
	}

	@Test
	public void findShortestPathFromStartWithWeightsLookahead2() throws JDOMException, IOException, ConfigurationException {
		AdasimMap g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test-weights.xml") ).getGraph();
		strategy = new LookaheadShortestPathVehicleStrategy(2);
		strategy.setGraph(g);
		List<GraphNode> path = strategy.getPath(g.getNode(6), g.getNode(4));
		assertNotNull( "No path found", path );
		assertEquals( "Path has wrong length", 2, path.size() );
		assertEquals( 5, (int)path.get(0).getID() );
		assertEquals( 4, (int)path.get(1).getID() );
	}

	@Test
	public void recomputeShortestPathFromStartWithWeightsLookahead1() throws JDOMException, IOException, ConfigurationException {
		AdasimMap g = SimulationXMLReader.buildSimulator( new File("resources/test/lookahead-recompute-test.xml") ).getGraph();
		strategy = new LookaheadShortestPathVehicleStrategy(1);	//update after every step
		strategy.setGraph(g);
		strategy.setStartNode(g.getNode(0));
		strategy.setEndNode(g.getNode(4));
		
		List<GraphNode> firstPath = strategy.getPath(g.getNode(0), g.getNode(4));
		assertEquals( 6, firstPath.get(0).getID() );
		assertEquals( 1, firstPath.get(1).getID() );
		assertEquals( 2, firstPath.get(2).getID() );
		assertEquals( 4, firstPath.get(3).getID() );
		
		//now we load the graph with some cars to force a new path, these will make the firstPath too expensive
		g.addVehicleAtNode( new Vehicle( g.getNode(1), null, new LookaheadShortestPathVehicleStrategy(), 42 ), 1 );
		g.addVehicleAtNode( new Vehicle( g.getNode(1), null, new LookaheadShortestPathVehicleStrategy(), 43 ), 1 );
		
		GraphNode next = strategy.getNextNode();	//returns the first node of the first path and updates
		assertEquals( 6, next.getID() );	//
		assertEquals( 5, strategy.getNextNode().getID() );	//this is the next node on the new updated path
	}
}
