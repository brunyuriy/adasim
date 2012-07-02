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

package adasim.model.internal;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;
import org.junit.Test;

import adasim.agent.AbstractAdasimAgent;
import adasim.algorithm.delay.QuadraticTrafficDelayFunction;
import adasim.algorithm.routing.LookaheadShortestPathRoutingAlgorithm;
import adasim.filter.FakeFilter;
import adasim.filter.IdentityFilter;
import adasim.model.AdasimMap;
import adasim.model.ConfigurationException;
import adasim.model.RoadSegment;
import adasim.model.TrafficSimulator;
import adasim.model.Vehicle;
import adasim.model.internal.SimulationXMLReader;
import adasim.util.ReflectionException;
import adasim.util.ReflectionUtils;



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
	public void testStart() throws JDOMException, IOException, ConfigurationException, NoSuchMethodException, ReflectionException {
		List<Vehicle> cars = (List<Vehicle>)SimulationXMLReader.buildSimulator( new File("resources/test/config.xml" )).getAgents(Vehicle.class);
		assertEquals(((AbstractAdasimAgent) ReflectionUtils.getProperty(cars.get(0), "getCurrentPosition")).getID(), 0);
		assertEquals(((AbstractAdasimAgent) ReflectionUtils.getProperty(cars.get(1), "getCurrentPosition")).getID(), 4);
		assertEquals(((AbstractAdasimAgent) ReflectionUtils.getProperty(cars.get(2), "getCurrentPosition")).getID(), 3);
		assertEquals(((AbstractAdasimAgent) ReflectionUtils.getProperty(cars.get(3), "getCurrentPosition")).getID(), 8);
		assertEquals(((AbstractAdasimAgent) ReflectionUtils.getProperty(cars.get(4), "getCurrentPosition")).getID(), 3);
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
		assertEquals( LookaheadShortestPathRoutingAlgorithm.class, cars.get(1).getStrategy().getClass() );
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
	public void carValidTimeDoesNotThrow() throws JDOMException, IOException, ConfigurationException, NoSuchMethodException, ReflectionException {
		TrafficSimulator sim = SimulationXMLReader.buildSimulator( new File("resources/test/valid-time.xml" ));
		assertEquals( 1, ReflectionUtils.getProperty(sim.getMap().getRoadSegment(4), "getCurrentDelay") );
		sim.takeSimulationStep(); //cycle 1
		sim.takeSimulationStep(); //cycle 2, this should add the new car
		assertEquals( 2, ReflectionUtils.getProperty(sim.getMap().getRoadSegment(4), "getCurrentDelay") );
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
	public void testStrategies() throws FileNotFoundException, ConfigurationException, NoSuchMethodException, ReflectionException {
		TrafficSimulator sim = SimulationXMLReader.buildSimulator( new File("resources/test/config.xml" ) );
		AdasimMap g = sim.getMap();
		assertEquals(1, ReflectionUtils.getProperty(g.getRoadSegment(6), "getCurrentDelay"));
		g.addVehicleAtSegment(sim.getVehicle(0), 6);
		g.addVehicleAtSegment(sim.getVehicle(1), 6);
		assertEquals(3, ReflectionUtils.getProperty(g.getRoadSegment(6), "getCurrentDelay")); //Tests Quadratic Speed Strategy
		assertEquals(1, ReflectionUtils.getProperty(g.getRoadSegment(1), "getCurrentDelay"));
		g.addVehicleAtSegment(sim.getVehicle(2), 1);
		g.addVehicleAtSegment(sim.getVehicle(3), 1);
		assertEquals(3, ReflectionUtils.getProperty(g.getRoadSegment(1), "getCurrentDelay") ); //Tests Linear Speed Strategy
	}
	
	@Test
	public void testNeighbors() throws FileNotFoundException, ConfigurationException {
		AdasimMap g = SimulationXMLReader.buildSimulator( new File("resources/test/config.xml")).getMap();
		List<RoadSegment> neighbors = g.getRoadSegments().get(2).getNeighbors();
		int first = neighbors.get(0).getID();
		assertEquals(first, 4);
		int second = neighbors.get(1).getID();
		assertEquals(second, 7);
		int third = neighbors.get(2).getID();
		assertEquals(third, 9);
	}
	
	@Test
	public void emptyNeighborListIsNotIgnored() throws FileNotFoundException, ConfigurationException {
		AdasimMap g = SimulationXMLReader.buildSimulator( new File("resources/test/unconnected-node.xml")).getMap();
		assertEquals( g.getRoadSegments().size(), 10);
	}
	
	@Test(expected=ConfigurationException.class)
	public void noNodesThrows() throws FileNotFoundException, ConfigurationException {
		SimulationXMLReader.buildSimulator(new File("resources/test/no-nodes.xml")).getMap();
		fail( "This should throw a meaningful exception to be handled by main()" );
	}
	
	@Test(expected=ConfigurationException.class)
	public void oneNodesIsOK() throws FileNotFoundException, ConfigurationException {
		SimulationXMLReader.buildSimulator(new File("resources/test/one-node.xml")).getMap();
		fail( "This should throw a meaningful exception to be handled by main()" );
	}


	@Test(expected=ConfigurationException.class)
	public void noGraphThrows() throws FileNotFoundException, ConfigurationException {
		SimulationXMLReader.buildSimulator( new File("resources/test/no-graph.xml" )).getMap();
		fail( "This should throw a meaningful exception to be handled by main()" );
	}
	
	@Test
	public void invalidNeighborIsIgnored() throws FileNotFoundException, ConfigurationException {
		AdasimMap g = SimulationXMLReader.buildSimulator( new File("resources/test/invalid-neighbor.xml")).getMap();
		List<RoadSegment> neighbors = g.getRoadSegment( 0 ).getNeighbors(); 
		assertEquals(1, neighbors.size() );
		assertEquals(4, neighbors.get(0).getID() );
		neighbors = g.getRoadSegment( 1 ).getNeighbors(); 
		assertTrue( neighbors.isEmpty() );
	}
	
	@Test
	public void invalidSpeedStrategyDefaultsCorrectly() throws FileNotFoundException, ConfigurationException {
		AdasimMap g = SimulationXMLReader.buildSimulator( new File("resources/test/invalid-strategy2.xml")).getMap();
		assertEquals( QuadraticTrafficDelayFunction.class, g.getRoadSegments().get(1).getSpeedStrategy().getClass() );
	}

	@Test
	public void loadsAutoGeneratedConfig() throws FileNotFoundException, ConfigurationException {
		SimulationXMLReader.buildSimulator( new File("resources/test/auto-generated.xml")).getMap();
		//this passes when no unexpected exceptions are thrown
	}

	@Test
	public void setsDelayCorrectly() throws FileNotFoundException, ConfigurationException, NoSuchMethodException, ReflectionException {
		AdasimMap g = SimulationXMLReader.buildSimulator( new File("resources/test/config-weights.xml")).getMap();
		assertEquals( 7, ReflectionUtils.getProperty(g.getRoadSegments().get(1), "getDelay" ) );
	}

	@Test
	public void setsDelayCorrectly2() throws FileNotFoundException, ConfigurationException, NoSuchMethodException, ReflectionException {
		AdasimMap g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test-weights.xml")).getMap();
		assertEquals( 4, ReflectionUtils.getProperty(g.getRoadSegments().get(3), "getDelay" ) );
	}

	@Test (expected=ConfigurationException.class)
	public void invalidDelayDefaults() throws FileNotFoundException, ConfigurationException {
		SimulationXMLReader.buildSimulator( new File("resources/test/config-weights-invalid.xml")).getMap();
		fail( "This should throw a meaningful exception to be handled by main()" );
	}

	@Test (expected=ConfigurationException.class)
	public void negativeDelayDefaults() throws FileNotFoundException, ConfigurationException {
		SimulationXMLReader.buildSimulator( new File("resources/test/config-weights-invalid2.xml")).getMap();
		fail( "This should throw a meaningful exception to be handled by main()" );
	}

	@Test
	public void nodesHaveAllVehicles() throws FileNotFoundException, ConfigurationException {
		AdasimMap g = SimulationXMLReader.buildSimulator( new File("resources/test/shortest-path-test-weights.xml")).getMap();
		assertEquals( 3, g.getRoadSegment(2).numVehiclesAtNode() );		
	}
	
	@Test
	public void filterConfigHierarchyForRoads() throws FileNotFoundException, ConfigurationException {
		TrafficSimulator sim = 	SimulationXMLReader.buildSimulator( new File("resources/test/default-filter-test.xml"));
		AdasimMap map = sim.getMap();
		RoadSegment road = map.getRoadSegment(0);
		assertNotNull( "No uncertainty filter configured", road.getUncertaintyFilter() );
		assertEquals( "Uncertainty filter has wrong type", FakeFilter2.class, road.getUncertaintyFilter().getClass() );
		assertNotNull( "No privacy filter for adasim.model.internal.FakeAgent configured", road.getPrivacyFilter( FakeAgent.class ) );
		assertEquals( "Privacy filter for adasim.model.internal.FakeAgent has wrong type", FakeFilter3.class, road.getPrivacyFilter( FakeAgent.class ).getClass() );		
		assertNotNull( "No default privacy filter configured", road.getPrivacyFilter( this.getClass() ) );
		assertEquals( "Default privacy filter has wrong type", FakeFilter.class, road.getPrivacyFilter( this.getClass() ).getClass() );		
		
		road = map.getRoadSegment(3);
		assertNotNull( "No uncertainty filter configured", road.getUncertaintyFilter() );
		assertEquals( "Uncertainty filter has wrong type", FakeFilter3.class, road.getUncertaintyFilter().getClass() );
		assertNotNull( "No privacy filter for adasim.model.internal.FakeAgent configured", road.getPrivacyFilter( FakeAgent.class ) );
		assertEquals( "Privacy filter for adasim.model.internal.FakeAgent has wrong type", FakeFilter4.class, road.getPrivacyFilter( FakeAgent.class ).getClass() );		
		assertNotNull( "No default privacy filter configured", road.getPrivacyFilter( this.getClass() ) );
		assertEquals( "Default privacy filter has wrong type", IdentityFilter.class, road.getPrivacyFilter( this.getClass() ).getClass() );		
	}
	
	@Test
	public void filterConfigHierarchyForAgents() throws FileNotFoundException, ConfigurationException {
		TrafficSimulator sim = 	SimulationXMLReader.buildSimulator( new File("resources/test/default-filter-test.xml"));
		AbstractAdasimAgent agent = (AbstractAdasimAgent)sim.getAgent(101);
		assertNotNull( "No uncertainty filter configured", agent.getUncertaintyFilter() );
		assertEquals( "Uncertainty filter has wrong type", FakeFilter4.class, agent.getUncertaintyFilter().getClass() );
		assertNotNull( "No privacy filter for adasim.model.internal.FakeAgent configured", agent.getPrivacyFilter( FakeAgent.class ) );
		assertEquals( "Privacy filter for adasim.model.internal.FakeAgent has wrong type", FakeFilter.class, agent.getPrivacyFilter( FakeAgent.class ).getClass() );		
		assertNotNull( "No default privacy filter configured", agent.getPrivacyFilter( this.getClass() ) );
		assertEquals( "Default privacy filter has wrong type", FakeFilter.class, agent.getPrivacyFilter( this.getClass() ).getClass() );		
		
		agent = (AbstractAdasimAgent)sim.getAgent(102);
		assertNotNull( "No uncertainty filter configured", agent.getUncertaintyFilter() );
		assertEquals( "Uncertainty filter has wrong type", FakeFilter3.class, agent.getUncertaintyFilter().getClass() );
		assertNotNull( "No privacy filter for adasim.model.internal.FakeAgent configured", agent.getPrivacyFilter( FakeAgent.class ) );
		assertEquals( "Privacy filter for adasim.model.internal.FakeAgent has wrong type", FakeFilter4.class, agent.getPrivacyFilter( FakeAgent.class ).getClass() );		
		assertNotNull( "No default privacy filter configured", agent.getPrivacyFilter( this.getClass() ) );
		assertEquals( "Default privacy filter has wrong type", IdentityFilter.class, agent.getPrivacyFilter( this.getClass() ).getClass() );		
	}

}
