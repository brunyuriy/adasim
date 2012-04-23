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
 * Created: Jan 25, 2012
 */

package adasim.model.internal;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;

import adasim.agent.AbstractAdasimAgent;
import adasim.agent.AdasimAgent;
import adasim.algorithm.delay.LinearTrafficDelayFunction;
import adasim.algorithm.delay.QuadraticTrafficDelayFunction;
import adasim.algorithm.routing.AlwaysRecomputeRoutingAlgorithm;
import adasim.algorithm.routing.ShortestPathRoutingAlgorithm;
import adasim.filter.FakeFilter;
import adasim.filter.IdentityFilter;
import adasim.model.AdasimMap;
import adasim.model.ConfigurationException;
import adasim.model.RoadSegment;
import adasim.model.Vehicle;
import adasim.model.internal.SimulationXMLBuilder;


/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class SimulationXMLBuilderTest {
	
	private SAXBuilder parser;  //we use this builder to set up our input elements
	private SimulationXMLBuilder builder;
	
	@Before
	public void setUp() {
		parser = new SAXBuilder( false );
		builder = new SimulationXMLBuilder();
	}

	@Test
	public void nodeNoOptionals() throws JDOMException, IOException {
		Document doc = parser.build( new StringReader( "<node id=\"27\" neighbors=\"1 2 3\"/>" ) );
		RoadSegment node = builder.buildNode( doc.getRootElement() );
		assertEquals( 27, node.getID() );
		assertTrue( node.getNeighbors().isEmpty() );
		assertNull( node.getSpeedStrategy() );
		assertEquals(-1, node.getCapacity() );
		assertEquals(1, node.getDelay() ); //1 is the default delay
	}
	
	@Test
	public void nodeNoAllOptionals() throws JDOMException, IOException {
		Document doc = parser.build( new StringReader( "<node id=\"27\" neighbors=\"1 2 3\" delay=\"2\" capacity=\"5\" strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\" uncertainty_filter=\"adasim.filter.IdentityFilter\"/>" ) );
		RoadSegment node = builder.buildNode( doc.getRootElement() );
		assertEquals( 27, node.getID() );
		assertTrue( node.getNeighbors().isEmpty() );
		assertTrue( node.getSpeedStrategy() instanceof LinearTrafficDelayFunction );
		assertEquals(5, node.getCapacity() );
		assertEquals(2, node.getDelay() );
		assertNotNull( "No uncertainty filter assigned", node.getUncertaintyFilter() );
		assertTrue( "Uncertainty filter has wrong type", node.getUncertaintyFilter() instanceof IdentityFilter );
	}
	
	@Test
	public void graphWithDefaults() throws JDOMException, IOException, ConfigurationException {
		Document doc = parser.build( new StringReader( "<graph default_strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\" default_capacity=\"0\">" +
				"<node id=\"1\" neighbors=\"1 2 3 4\" delay=\"2\" capacity=\"5\"/>" +
				"<node id=\"2\" neighbors=\"3\" delay=\"2\" capacity=\"5\" strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\"/>" +
				"<node id=\"4\" neighbors=\"2 4\" delay=\"2\" strategy=\"adasim.algorithm.delay.QuadraticTrafficDelayFunction\"/>" +
				"</graph>" ) );
		AdasimMap graph = builder.buildGraph( doc.getRootElement() );
		assertEquals( 3, graph.getRoadSegments().size() );
		RoadSegment node = graph.getRoadSegment( 1 );
		assertEquals( 3, node.getNeighbors().size() );
		assertNotNull( "No default speed strategy assigned", node.getSpeedStrategy() );
		assertTrue( "Default speed strategy has wrong type", node.getSpeedStrategy() instanceof LinearTrafficDelayFunction );
		assertNotNull( "No uncertainty filter assigned", node.getUncertaintyFilter() );
		assertTrue( "Uncertainty filter has wrong type", node.getUncertaintyFilter() instanceof IdentityFilter );
		node = graph.getRoadSegment(4);
		assertEquals(0, node.getCapacity() );
		assertTrue( node.getSpeedStrategy() instanceof QuadraticTrafficDelayFunction );
	}

	
	@Test
	public void graphWithUncertaintyFilter() throws JDOMException, IOException, ConfigurationException {
		Document doc = parser.build( new StringReader( "<graph default_strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\" default_capacity=\"0\" uncertainty_filter=\"adasim.filter.FakeFilter\">" +
				"<node id=\"1\" neighbors=\"1 2 3 4\" delay=\"2\" capacity=\"5\"/>" +
				"<node id=\"2\" neighbors=\"3\" delay=\"2\" capacity=\"5\" strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\"/>" +
				"<node id=\"4\" neighbors=\"2 4\" delay=\"2\" strategy=\"adasim.algorithm.delay.QuadraticTrafficDelayFunction\" uncertainty_filter=\"adasim.filter.IdentityFilter\"/>" +
				"</graph>" ) );
		AdasimMap graph = builder.buildGraph( doc.getRootElement() );
		assertEquals( 3, graph.getRoadSegments().size() );
		RoadSegment node = graph.getRoadSegment( 1 );
		assertEquals( 3, node.getNeighbors().size() );
		assertNotNull( "No default speed strategy assigned", node.getSpeedStrategy() );
		assertTrue( "Default speed strategy has wrong type", node.getSpeedStrategy() instanceof LinearTrafficDelayFunction );
		assertNotNull( "No uncertainty filter assigned", node.getUncertaintyFilter() );
		assertTrue( "Uncertainty filter has wrong type", node.getUncertaintyFilter() instanceof FakeFilter );
		node = graph.getRoadSegment(4);
		assertEquals(0, node.getCapacity() );
		assertTrue( node.getSpeedStrategy() instanceof QuadraticTrafficDelayFunction );
		assertNotNull( "No uncertainty filter assigned", node.getUncertaintyFilter() );
		assertTrue( "Uncertainty filter has wrong type", node.getUncertaintyFilter() instanceof IdentityFilter );
	}

	@Test
	public void graphWithUncertaintyFilterHookup() throws JDOMException, IOException, ConfigurationException {
		//test that the uncertainty filter gets called correctly.
		Document doc = parser.build( new StringReader( "<graph default_strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\" default_capacity=\"0\" uncertainty_filter=\"adasim.filter.FakeFilter\">" +
				"<node id=\"1\" neighbors=\"1 2 3 4\" delay=\"2\" capacity=\"5\"/>" +
				"<node id=\"2\" neighbors=\"3\" delay=\"2\" capacity=\"5\" strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\"/>" +
				"<node id=\"4\" neighbors=\"2 4\" delay=\"2\" strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\" uncertainty_filter=\"adasim.filter.FakeFilter\"/>" +
				"</graph>" ) );
		AdasimMap graph = builder.buildGraph( doc.getRootElement() );
		assertEquals( 3, graph.getRoadSegments().size() );
		RoadSegment node = graph.getRoadSegment(4);
		assertNotNull( "No uncertainty filter assigned", node.getUncertaintyFilter() );
		assertTrue( "Uncertainty filter has wrong type", node.getUncertaintyFilter() instanceof FakeFilter );
		//this "fake" node has a capacity of -1, because it has not passed validation yet
		assertEquals( 0, node.getCapacity() );
		assertEquals( 3, node.getDelay() );
		assertEquals( 4, node.getCurrentDelay() );
		assertEquals( 1, node.numVehiclesAtNode() );
		assertEquals( 4, node.getID() );
		assertFalse( node.isClosed() ); 
	}

	@Test
	public void graphWithPrivacyFilterElement() throws JDOMException, IOException, ConfigurationException {
		Document doc = parser.build( new StringReader( "<graph default_strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\" default_capacity=\"0\">" +
				"<node id=\"1\" neighbors=\"1 2 3 4\" delay=\"2\" capacity=\"5\"/>" +
				"<node id=\"2\" neighbors=\"3\" delay=\"2\" capacity=\"5\" strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\"/>" +
				"<node id=\"4\" neighbors=\"2 4\" delay=\"2\" strategy=\"adasim.algorithm.delay.QuadraticTrafficDelayFunction\" >" +
				"<filters>" +
				"<filter type=\"privacy\" filter=\"traffic.filter.IdentityFilter\" criterion=\"traffic.model.internal.SimulationXMLBuilderTest\"/>" +
				"</filters>" +
				"</node>" +
				"</graph>" ) );
		AdasimMap graph = builder.buildGraph( doc.getRootElement() );
		assertEquals( 3, graph.getRoadSegments().size() );
		RoadSegment node = graph.getRoadSegment( 1 );
		assertEquals( 3, node.getNeighbors().size() );
		assertNotNull( "No default speed strategy assigned", node.getSpeedStrategy() );
		assertTrue( "Default speed strategy has wrong type", node.getSpeedStrategy() instanceof LinearTrafficDelayFunction );
		assertNotNull( "No privacy filter assigned", node.getPrivacyFilter(this.getClass()) );
		assertTrue( "Privacy filter has wrong type", node.getPrivacyFilter(this.getClass()) instanceof FakeFilter );
		node = graph.getRoadSegment(4);
		assertEquals(0, node.getCapacity() );
		assertTrue( node.getSpeedStrategy() instanceof QuadraticTrafficDelayFunction );
		assertNotNull( "No privacy filter assigned", node.getPrivacyFilter(this.getClass()) );
		assertTrue( "Privacy filter has wrong type", node.getPrivacyFilter(this.getClass()) instanceof IdentityFilter );
	}
	
	@Test
	public void carNoOptionals() throws JDOMException, IOException {
		Document doc = parser.build( new StringReader( "<car id=\"27\" start=\"1\" end=\"1\"/>" ) );
		Vehicle car = builder.buildVehicle( doc.getRootElement() );
		assertEquals( 27, car.getID() );
		assertNull( car.getStartNode() );
		assertNull( car.getEndNode() );
		assertNull( car.getStrategy() );
	}
	
	@Test
	public void carAllOptionals() throws JDOMException, IOException {
		Document doc = parser.build( new StringReader( "<car id=\"27\" start=\"1\" end=\"1\" strategy=\"adasim.algorithm.routing.ShortestPathRoutingAlgorithm\" />" ) );
		Vehicle car = builder.buildVehicle( doc.getRootElement() );
		assertEquals( 27, car.getID() );
		assertNull( car.getStartNode() );
		assertNull( car.getEndNode() );
		assertTrue( car.getStrategy() instanceof ShortestPathRoutingAlgorithm );
	}

	@Test
	public void carListWithDefaults() throws JDOMException, IOException, ConfigurationException {
		Document doc = parser.build( new StringReader( "<cars default_strategy=\"adasim.algorithm.routing.AlwaysRecomputeRoutingAlgorithm\">" +
				"<car id=\"1\" start=\"1\" end=\"1\" />" +
				"<car id=\"2\" start=\"1\" end=\"1\" strategy=\"adasim.algorithm.routing.ShortestPathRoutingAlgorithm\" />" +
				"<car id=\"3\" start=\"1\" end=\"1\" />" +
				"</cars>" ) );
		List<Vehicle> cars = builder.buildVehicles( doc.getRootElement() );
		assertEquals( 3, cars.size() );
		Vehicle car = cars.get(0);	//this should be the car with id 1
		assertEquals( 1, car.getID() );
		assertTrue( car.getStrategy() instanceof AlwaysRecomputeRoutingAlgorithm );
		car = cars.get(1);
		assertEquals( 2, car.getID() );
		assertTrue( car.getStrategy() instanceof ShortestPathRoutingAlgorithm );
	}
	
	@Test
	public void agentNoOptionals() throws JDOMException, IOException, ConfigurationException {
		Document doc = parser.build( new StringReader( "<agent id=\"27\" class=\"adasim.model.internal.FakeAgent\" />" ) );
		AdasimAgent agent = builder.buildAgent( doc.getRootElement() );
	}

	@Test
	public void agentAllOptionals() throws JDOMException, IOException, ConfigurationException {
		Document doc = parser.build( new StringReader( "<agent id=\"27\" class=\"adasim.model.internal.FakeAgent\" parameters=\"blabab\"/>" ) );
		AdasimAgent agent = builder.buildAgent( doc.getRootElement() );
	}

}

class FakeAgent extends AbstractAdasimAgent {
	
	public FakeAgent(String s) {}

	/* (non-Javadoc)
	 * @see adasim.model.AdasimAgent#takeSimulationStep()
	 */
	@Override
	public void takeSimulationStep( long cycle) {}

}