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

import traffic.graph.Graph;
import traffic.graph.GraphNode;
import traffic.model.ConfigurationException;
import traffic.model.SimulationXMLReader;


/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class LookaheadShortestPathCarStrategyTest {
	
	private LookaheadShortestPathCarStrategy strategy;
	
	@Before
	public void setUp() {
		strategy = new LookaheadShortestPathCarStrategy(0);
	}
	
	@Test
	public void findShortestPathFromStartNoWeights() throws JDOMException, IOException, ConfigurationException {
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test.xml") ).getGraph();
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
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test-random-ids.xml") ).getGraph();
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
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test-weights.xml") ).getGraph();
		strategy.setGraph(g);
		List<GraphNode> path = strategy.getPath(g.getNode(6), g.getNode(4));
		assertNotNull( "No path found", path );
		assertEquals( "Path has wrong length", 3, path.size() );
		assertEquals( 1, (int)path.get(0).getID() );
		assertEquals( 2, (int)path.get(1).getID() );
	}

	@Test
	public void findShortestPathFromStartWithWeightsLookahead1() throws JDOMException, IOException, ConfigurationException {
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test-weights.xml") ).getGraph();
		strategy = new LookaheadShortestPathCarStrategy(1);
		strategy.setGraph(g);
		List<GraphNode> path = strategy.getPath(g.getNode(6), g.getNode(4));
		assertNotNull( "No path found", path );
		assertEquals( "Path has wrong length", 3, path.size() );
		assertEquals( 1, (int)path.get(0).getID() );
		assertEquals( 2, (int)path.get(1).getID() );
	}

	@Test
	public void findShortestPathFromStartWithWeightsLookahead2() throws JDOMException, IOException, ConfigurationException {
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test-weights.xml") ).getGraph();
		strategy = new LookaheadShortestPathCarStrategy(2);
		strategy.setGraph(g);
		List<GraphNode> path = strategy.getPath(g.getNode(6), g.getNode(4));
		assertNotNull( "No path found", path );
		assertEquals( "Path has wrong length", 2, path.size() );
		assertEquals( 5, (int)path.get(0).getID() );
		assertEquals( 4, (int)path.get(1).getID() );
	}

	@Test
	public void recomputeShortestPathFromStartWithWeightsLookahead1() throws JDOMException, IOException, ConfigurationException {
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test-weights.xml") ).getGraph();
		strategy = new LookaheadShortestPathCarStrategy(1);
		strategy.setGraph(g);
		List<GraphNode> path = strategy.getPath(g.getNode(6), g.getNode(4));
		assertNotNull( "No path found", path );
		assertEquals( "Path has wrong length", 3, path.size() );
		assertEquals( 1, (int)path.get(0).getID() );
		assertEquals( 2, (int)path.get(1).getID() );
	}
}
