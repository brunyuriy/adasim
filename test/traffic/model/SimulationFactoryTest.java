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
import traffic.strategy.DijkstraCarStrategy;
import traffic.strategy.QuadraticSpeedStrategy;


/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class SimulationFactoryTest {
	@Test (expected=FileNotFoundException.class)
	public void testFileNotFound() throws JDOMException, IOException, ConfigurationException {
		SimulationFactory.buildSimulator( new File("bad config") );
	}
	
	@Test
	public void testBadConfig() throws JDOMException, IOException, ConfigurationException {
		assertEquals(SimulationFactory.buildSimulator( new File("resources/test/badconfig.xml") ), null);
	}
	
	@Test
	public void testStart() throws JDOMException, IOException, ConfigurationException {
		List<Car> cars = SimulationFactory.buildSimulator( new File("resources/test/config.xml" )).getCars();
		assertEquals(cars.get(0).getCurrent(), 0);
		assertEquals(cars.get(1).getCurrent(), 4);
		assertEquals(cars.get(2).getCurrent(), 3);
		assertEquals(cars.get(3).getCurrent(), 8);
		assertEquals(cars.get(4).getCurrent(), 3);
	}
	
	@Test
	public void testCarNum() throws JDOMException, IOException, ConfigurationException {
		List<Car> cars = SimulationFactory.buildSimulator( new File("resources/test/config.xml" )).getCars();
		assertEquals(cars.get(0).getCarNumber(), 0);
		assertEquals(cars.get(1).getCarNumber(), 1);
		assertEquals(cars.get(2).getCarNumber(), 2);
		assertEquals(cars.get(3).getCarNumber(), 3);
		assertEquals(cars.get(4).getCarNumber(), 4);
	}
	
	@Test
	public void invalidCarStrategyDefaultsCorrectly() throws JDOMException, IOException, ConfigurationException {
		List<Car> cars = SimulationFactory.buildSimulator( new File("resources/test/invalid-strategy.xml" )).getCars();
		assertEquals( DijkstraCarStrategy.class, cars.get(1).getStrategy().getClass() );
	}
	
	@Test(expected=ConfigurationException.class)
	public void noCarThrows() throws JDOMException, IOException, ConfigurationException {
		List<Car> cars = SimulationFactory.buildSimulator( new File("resources/test/no-car.xml" )).getCars();
		fail( "This should throw a meaningful exception to be handled by main()" );
	}

	@Test(expected=ConfigurationException.class)
	public void noCarsThrows() throws JDOMException, IOException, ConfigurationException {
		List<Car> cars = SimulationFactory.buildSimulator( new File("resources/test/no-cars.xml" )).getCars();
		fail( "This should throw a meaningful exception to be handled by main()" );
	}
	
	@Test
	public void invalidStartEndIsIgnored() throws JDOMException, IOException, ConfigurationException {
		List<Car> cars = SimulationFactory.buildSimulator( new File("resources/test/invalid-start.xml" )).getCars();
		assertEquals( 3, cars.size() );
		assertEquals( 0, cars.get(0).getCarNumber() );
		assertEquals( 3, cars.get(1).getCarNumber() );
		assertEquals( 4, cars.get(2).getCarNumber() );
	}
	
	@Test
	public void testStrategies() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationFactory.buildSimulator( new File("resources/test/config.xml" ) ).getGraph();
		assertEquals(g.getDelayAtNode(6), 0);
		g.addCarAtNode(0, 6);
		g.addCarAtNode(1, 6);
		assertEquals(g.getDelayAtNode(6), 4); //Tests Quadratic Speed Strategy
		assertEquals(g.getDelayAtNode(1), 0);
		g.addCarAtNode(2, 1);
		g.addCarAtNode(3, 1);
		assertEquals(g.getDelayAtNode(1), 2); //Tests Linear Speed Strategy
	}
	
	@Test
	public void testNeighbors() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationFactory.buildSimulator( new File("resources/test/config.xml")).getGraph();
		List<Integer> neighbors = g.getNodes().get(2).getNeighbors();
		int first = neighbors.get(0);
		assertEquals(first, 4);
		int second = neighbors.get(1);
		assertEquals(second, 7);
		int third = neighbors.get(2);
		assertEquals(third, 9);
	}
	
	@Test
	public void emptyNeighborListIsIgnored() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationFactory.buildSimulator( new File("resources/test/unconnected-node.xml")).getGraph();
		assertEquals( g.getNodes().size(), 9);
	}
	
	@Test(expected=ConfigurationException.class)
	public void noNodesThrows() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationFactory.buildSimulator(new File("resources/test/no-nodes.xml")).getGraph();
	}

	@Test(expected=ConfigurationException.class)
	public void noGraphThrows() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationFactory.buildSimulator( new File("resources/test/no-graph.xml" )).getGraph();
	}
	
	@Test
	public void invalidNeighborIsIgnored() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationFactory.buildSimulator( new File("resources/test/invalid-neighbor.xml")).getGraph();
		List<Integer> neighbors = g.getNeighbors( 0 ); 
		assertEquals(1, neighbors.size() );
		assertEquals(4, (int)neighbors.get(0) );
		neighbors = g.getNeighbors( 1 ); 
		assertEquals(3, neighbors.size() );
	}
	
	@Test
	public void invalidSpeedStrategyDefaultsCorrectly() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationFactory.buildSimulator( new File("resources/test/invalid-strategy2.xml")).getGraph();
		assertEquals( QuadraticSpeedStrategy.class, g.getNodes().get(1).getSpeedStrategy().getClass() );
	}

	@Test
	public void loadsAutoGeneratedConfig() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationFactory.buildSimulator( new File("resources/test/auto-generated.xml")).getGraph();
		//this passes when no unexpected exceptions are thrown
	}

}
