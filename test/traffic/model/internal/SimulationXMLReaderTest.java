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

package traffic.model.internal;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;
import org.junit.Test;

import traffic.model.ConfigurationException;
import traffic.model.Graph;
import traffic.model.GraphNode;
import traffic.model.TrafficSimulator;
import traffic.model.Vehicle;
import traffic.model.internal.SimulationXMLReader;
import traffic.strategy.LookaheadShortestPathVehicleStrategy;
import traffic.strategy.QuadraticSpeedStrategy;


/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class SimulationXMLReaderTest {
	@Test (expected=ConfigurationException.class)
	public void testFileNotFound() throws JDOMException, IOException, ConfigurationException {
		SimulationXMLReader.buildSimulator( new File("bad config") );
	}
	
	@Test
	public void testStart() throws JDOMException, IOException, ConfigurationException {
		List<Vehicle> cars = (List<Vehicle>)SimulationXMLReader.buildSimulator( new File("resources/test/config.xml" )).getAgents(Vehicle.class);
		assertEquals(cars.get(0).getCurrentPosition().getID(), 0);
		assertEquals(cars.get(1).getCurrentPosition().getID(), 4);
		assertEquals(cars.get(2).getCurrentPosition().getID(), 3);
		assertEquals(cars.get(3).getCurrentPosition().getID(), 8);
		assertEquals(cars.get(4).getCurrentPosition().getID(), 3);
	}
	
	@Test
	public void testVehicleNum() throws JDOMException, IOException, ConfigurationException {
		List<Vehicle> cars = SimulationXMLReader.buildSimulator( new File("resources/test/config.xml" )).getAgents(Vehicle.class);
		assertEquals(cars.get(0).getID(), 0);
		assertEquals(cars.get(1).getID(), 1);
		assertEquals(cars.get(2).getID(), 2);
		assertEquals(cars.get(3).getID(), 3);
		assertEquals(cars.get(4).getID(), 4);
	}
	
	@Test
	public void invalidVehicleStrategyDefaultsCorrectly() throws JDOMException, IOException, ConfigurationException {
		List<Vehicle> cars = SimulationXMLReader.buildSimulator( new File("resources/test/invalid-strategy.xml" )).getAgents(Vehicle.class);
		assertEquals( LookaheadShortestPathVehicleStrategy.class, cars.get(1).getStrategy().getClass() );
	}
	
	@Test(expected=ConfigurationException.class)
	public void invalidDefaultVehicleStrategyThrows() throws JDOMException, IOException, ConfigurationException {
		SimulationXMLReader.buildSimulator( new File("resources/test/illegal-default-car-strategy.xml" )).getAgents(Vehicle.class);
		fail( "This should throw a meaningful exception to be handled by main()" );
	}

	
	@Test(expected=ConfigurationException.class)
	public void noVehicleThrows() throws JDOMException, IOException, ConfigurationException {
		SimulationXMLReader.buildSimulator( new File("resources/test/no-car.xml" )).getAgents(Vehicle.class);
		fail( "This should throw a meaningful exception to be handled by main()" );
	}

	@Test(expected=ConfigurationException.class)
	public void noVehiclesThrows() throws JDOMException, IOException, ConfigurationException {
		SimulationXMLReader.buildSimulator( new File("resources/test/no-cars.xml" )).getAgents(Vehicle.class);
		fail( "This should throw a meaningful exception to be handled by main()" );
	}
	
	@Test(expected=ConfigurationException.class)
	public void carNegativeTimeThrows() throws JDOMException, IOException, ConfigurationException {
		SimulationXMLReader.buildSimulator( new File("resources/test/negative-time.xml" ));
		fail( "Should throw a ConfigurationException" );
	}

	@Test
	public void carValidTimeDoesNotThrow() throws JDOMException, IOException, ConfigurationException {
		TrafficSimulator sim = SimulationXMLReader.buildSimulator( new File("resources/test/valid-time.xml" ));
		assertEquals( 1, sim.getGraph().getNode(4).getCurrentDelay() );
		sim.takeSimulationStep(); //cycle 1
		sim.takeSimulationStep(); //cycle 2, this should add the new car
		assertEquals( 2, sim.getGraph().getNode(4).getCurrentDelay() );
	}
	
	@Test
	public void invalidStartEndIsIgnored() throws JDOMException, IOException, ConfigurationException {
		List<Vehicle> cars = SimulationXMLReader.buildSimulator( new File("resources/test/invalid-start.xml" )).getAgents(Vehicle.class);
		assertEquals( 3, cars.size() );
		assertEquals( 0, cars.get(0).getID() );
		assertEquals( 3, cars.get(1).getID() );
		assertEquals( 4, cars.get(2).getID() );
	}
	
	@Test
	public void testStrategies() throws FileNotFoundException, ConfigurationException {
		TrafficSimulator sim = SimulationXMLReader.buildSimulator( new File("resources/test/config.xml" ) );
		Graph g = sim.getGraph();
		assertEquals(1, g.getNode(6).getCurrentDelay());
		g.addVehicleAtNode(sim.getVehicle(0), 6);
		g.addVehicleAtNode(sim.getVehicle(1), 6);
		assertEquals(3, g.getNode(6).getCurrentDelay()); //Tests Quadratic Speed Strategy
		assertEquals(1, g.getNode(1).getCurrentDelay());
		g.addVehicleAtNode(sim.getVehicle(2), 1);
		g.addVehicleAtNode(sim.getVehicle(3), 1);
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
	public void emptyNeighborListIsNotIgnored() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/unconnected-node.xml")).getGraph();
		assertEquals( g.getNodes().size(), 10);
	}
	
	@Test(expected=ConfigurationException.class)
	public void noNodesThrows() throws FileNotFoundException, ConfigurationException {
		SimulationXMLReader.buildSimulator(new File("resources/test/no-nodes.xml")).getGraph();
		fail( "This should throw a meaningful exception to be handled by main()" );
	}
	
	@Test(expected=ConfigurationException.class)
	public void oneNodesIsOK() throws FileNotFoundException, ConfigurationException {
		SimulationXMLReader.buildSimulator(new File("resources/test/one-node.xml")).getGraph();
		fail( "This should throw a meaningful exception to be handled by main()" );
	}


	@Test(expected=ConfigurationException.class)
	public void noGraphThrows() throws FileNotFoundException, ConfigurationException {
		SimulationXMLReader.buildSimulator( new File("resources/test/no-graph.xml" )).getGraph();
		fail( "This should throw a meaningful exception to be handled by main()" );
	}
	
	@Test
	public void invalidNeighborIsIgnored() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/invalid-neighbor.xml")).getGraph();
		List<GraphNode> neighbors = g.getNode( 0 ).getNeighbors(); 
		assertEquals(1, neighbors.size() );
		assertEquals(4, neighbors.get(0).getID() );
		neighbors = g.getNode( 1 ).getNeighbors(); 
		assertTrue( neighbors.isEmpty() );
	}
	
	@Test
	public void invalidSpeedStrategyDefaultsCorrectly() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/invalid-strategy2.xml")).getGraph();
		assertEquals( QuadraticSpeedStrategy.class, g.getNodes().get(1).getSpeedStrategy().getClass() );
	}

	@Test
	public void loadsAutoGeneratedConfig() throws FileNotFoundException, ConfigurationException {
		SimulationXMLReader.buildSimulator( new File("resources/test/auto-generated.xml")).getGraph();
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
		SimulationXMLReader.buildSimulator( new File("resources/test/config-weights-invalid.xml")).getGraph();
		fail( "This should throw a meaningful exception to be handled by main()" );
	}

	@Test (expected=ConfigurationException.class)
	public void negativeDelayDefaults() throws FileNotFoundException, ConfigurationException {
		SimulationXMLReader.buildSimulator( new File("resources/test/config-weights-invalid2.xml")).getGraph();
		fail( "This should throw a meaningful exception to be handled by main()" );
	}

	@Test
	public void nodesHaveAllVehicles() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test-weights.xml")).getGraph();
		assertEquals( 3, g.getNode(2).numVehiclesAtNode() );		
	}
}
