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
 * Created: Dec 6, 2011
 */

package traffic.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;
import org.junit.Test;

import traffic.graph.Graph;
import traffic.graph.GraphNode;
import traffic.strategy.LookaheadShortestPathCarStrategy;
import traffic.strategy.QuadraticSpeedStrategy;


/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class SimulationXMLReaderTest {
	@Test (expected=FileNotFoundException.class)
	public void testFileNotFound() throws JDOMException, IOException, ConfigurationException {
		SimulationXMLReader.buildSimulator( new File("bad config") );
	}
	
	@Test
	public void testStart() throws JDOMException, IOException, ConfigurationException {
		List<Car> cars = SimulationXMLReader.buildSimulator( new File("resources/test/config.xml" )).getCars();
		assertEquals(cars.get(0).getCurrentNode(), 0);
		assertEquals(cars.get(1).getCurrentNode(), 4);
		assertEquals(cars.get(2).getCurrentNode(), 3);
		assertEquals(cars.get(3).getCurrentNode(), 8);
		assertEquals(cars.get(4).getCurrentNode(), 3);
	}
	
	@Test
	public void testCarNum() throws JDOMException, IOException, ConfigurationException {
		List<Car> cars = SimulationXMLReader.buildSimulator( new File("resources/test/config.xml" )).getCars();
		assertEquals(cars.get(0).getID(), 0);
		assertEquals(cars.get(1).getID(), 1);
		assertEquals(cars.get(2).getID(), 2);
		assertEquals(cars.get(3).getID(), 3);
		assertEquals(cars.get(4).getID(), 4);
	}
	
	@Test
	public void invalidCarStrategyDefaultsCorrectly() throws JDOMException, IOException, ConfigurationException {
		List<Car> cars = SimulationXMLReader.buildSimulator( new File("resources/test/invalid-strategy.xml" )).getCars();
		assertEquals( LookaheadShortestPathCarStrategy.class, cars.get(1).getStrategy().getClass() );
	}
	
	@Test(expected=ConfigurationException.class)
	public void invalidDefaultCarStrategyThrows() throws JDOMException, IOException, ConfigurationException {
		List<Car> cars = SimulationXMLReader.buildSimulator( new File("resources/test/illegal-default-car-strategy.xml" )).getCars();
	}

	
	@Test(expected=ConfigurationException.class)
	public void noCarThrows() throws JDOMException, IOException, ConfigurationException {
		List<Car> cars = SimulationXMLReader.buildSimulator( new File("resources/test/no-car.xml" )).getCars();
		fail( "This should throw a meaningful exception to be handled by main()" );
	}

	@Test(expected=ConfigurationException.class)
	public void noCarsThrows() throws JDOMException, IOException, ConfigurationException {
		List<Car> cars = SimulationXMLReader.buildSimulator( new File("resources/test/no-cars.xml" )).getCars();
		fail( "This should throw a meaningful exception to be handled by main()" );
	}
	
	@Test
	public void invalidStartEndIsIgnored() throws JDOMException, IOException, ConfigurationException {
		List<Car> cars = SimulationXMLReader.buildSimulator( new File("resources/test/invalid-start.xml" )).getCars();
		assertEquals( 3, cars.size() );
		assertEquals( 0, cars.get(0).getID() );
		assertEquals( 3, cars.get(1).getID() );
		assertEquals( 4, cars.get(2).getID() );
	}
	
	@Test
	public void testStrategies() throws FileNotFoundException, ConfigurationException {
		TrafficSimulator sim = SimulationXMLReader.buildSimulator( new File("resources/test/config.xml" ) );
		Graph g = sim.getGraph();
		assertEquals(g.getNode(6).getCurrentDelay(), 1);
		g.addCarAtNode(sim.getCar(0), 6);
		g.addCarAtNode(sim.getCar(1), 6);
		assertEquals(3, g.getNode(6).getCurrentDelay()); //Tests Quadratic Speed Strategy
		assertEquals(1, g.getNode(1).getCurrentDelay());
		g.addCarAtNode(sim.getCar(2), 1);
		g.addCarAtNode(sim.getCar(3), 1);
		assertEquals(3, g.getNode(1).getCurrentDelay() ); //Tests Linear Speed Strategy
	}
	
	@Test
	public void testNeighbors() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/config.xml")).getGraph();
		List<GraphNode> neighbors = g.getNodes().get(2).getNeighbors();
		int first = neighbors.get(0).getID();
		assertEquals(first, 4);
		int second = neighbors.get(1).getID();
		assertEquals(second, 7);
		int third = neighbors.get(2).getID();
		assertEquals(third, 9);
	}
	
	@Test
	public void emptyNeighborListIsIgnored() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/unconnected-node.xml")).getGraph();
		assertEquals( g.getNodes().size(), 9);
	}
	
	@Test(expected=ConfigurationException.class)
	public void noNodesThrows() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationXMLReader.buildSimulator(new File("resources/test/no-nodes.xml")).getGraph();
	}

	@Test(expected=ConfigurationException.class)
	public void noGraphThrows() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/no-graph.xml" )).getGraph();
	}
	
	@Test
	public void invalidNeighborIsIgnored() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/invalid-neighbor.xml")).getGraph();
		List<GraphNode> neighbors = g.getNeighbors( 0 ); 
		assertEquals(1, neighbors.size() );
		assertEquals(4, neighbors.get(0).getID() );
		neighbors = g.getNeighbors( 1 ); 
		assertEquals(null, neighbors );
	}
	
	@Test
	public void invalidSpeedStrategyDefaultsCorrectly() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/invalid-strategy2.xml")).getGraph();
		assertEquals( QuadraticSpeedStrategy.class, g.getNodes().get(1).getSpeedStrategy().getClass() );
	}

	@Test
	public void loadsAutoGeneratedConfig() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/auto-generated.xml")).getGraph();
		//this passes when no unexpected exceptions are thrown
	}

	@Test
	public void setsDelayCorrectly() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/config-weights.xml")).getGraph();
		assertEquals( 7, g.getNodes().get(1).getDelay() );
	}

	@Test
	public void setsDelayCorrectly2() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test-weights.xml")).getGraph();
		assertEquals( 4, g.getNodes().get(3).getDelay() );
	}

	@Test (expected=ConfigurationException.class)
	public void invalidDelayDefaults() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/config-weights-invalid.xml")).getGraph();
	}

	@Test (expected=ConfigurationException.class)
	public void negativeDelayDefaults() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/config-weights-invalid2.xml")).getGraph();
	}

	@Test
	public void nodesHaveAllCars() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test-weights.xml")).getGraph();
		assertEquals( 3, g.getNode(2).numCarsAtNode() );
		
	}
}
