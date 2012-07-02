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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;

import adasim.agent.AbstractAdasimAgent;
import adasim.algorithm.delay.LinearTrafficDelayFunction;
import adasim.algorithm.delay.QuadraticTrafficDelayFunction;
import adasim.algorithm.routing.AlwaysRecomputeRoutingAlgorithm;
import adasim.algorithm.routing.ShortestPathRoutingAlgorithm;
import adasim.filter.AdasimFilter;
import adasim.filter.FakeFilter;
import adasim.filter.IdentityFilter;
import adasim.model.AdasimMap;
import adasim.model.ConfigurationException;
import adasim.model.RoadSegment;
import adasim.model.Vehicle;
import adasim.util.ReflectionException;
import adasim.util.ReflectionUtils;


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
	public void nodeNoOptionals() throws JDOMException, IOException, NoSuchMethodException, ReflectionException {
		Document doc = parser.build( new StringReader( "<node id=\"27\" neighbors=\"1 2 3\" delay=\"1\"/>" ) );
		RoadSegment node = builder.buildNode( doc.getRootElement(), new FilterMap() );
		assertEquals( 27, node.getID() );
		assertTrue( node.getNeighbors().isEmpty() );
		assertNull( node.getSpeedStrategy() );
		assertEquals(-1, ReflectionUtils.getProperty(node, "getCapacity") );
		assertEquals(1, ReflectionUtils.getProperty(node, "getDelay") ); //1 is the default delay
	}
	
	@Test
	public void nodeNoAllOptionals() throws JDOMException, IOException, NoSuchMethodException, ReflectionException {
		Document doc = parser.build( new StringReader( "<node id=\"27\" neighbors=\"1 2 3\" delay=\"2\" capacity=\"5\" strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\" uncertainty_filter=\"adasim.filter.IdentityFilter\"/>" ) );
		RoadSegment node = builder.buildNode( doc.getRootElement(), new FilterMap() );
		assertEquals( 27, node.getID() );
		assertTrue( node.getNeighbors().isEmpty() );
		assertTrue( node.getSpeedStrategy() instanceof LinearTrafficDelayFunction );
		assertEquals(5, ReflectionUtils.getProperty(node, "getCapacity") );
		assertEquals(2, ReflectionUtils.getProperty(node, "getDelay") );
		assertNotNull( "No uncertainty filter assigned", node.getUncertaintyFilter() );
		assertTrue( "Uncertainty filter has wrong type", node.getUncertaintyFilter() instanceof IdentityFilter );
	}
	
	@Test
	public void graphWithDefaults() throws JDOMException, IOException, ConfigurationException, NoSuchMethodException, ReflectionException {
		Document doc = parser.build( new StringReader( "<graph default_strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\" default_capacity=\"0\">" +
				"<node id=\"1\" neighbors=\"1 2 3 4\" delay=\"2\" capacity=\"5\"/>" +
				"<node id=\"2\" neighbors=\"3\" delay=\"2\" capacity=\"5\" strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\"/>" +
				"<node id=\"4\" neighbors=\"2 4\" delay=\"2\" strategy=\"adasim.algorithm.delay.QuadraticTrafficDelayFunction\"/>" +
				"</graph>" ) );
		AdasimMap graph = builder.buildGraph( doc.getRootElement(), new FilterMap() );
		assertEquals( 3, graph.getRoadSegments().size() );
		RoadSegment node = graph.getRoadSegment( 1 );
		assertEquals( 3, node.getNeighbors().size() );
		assertNotNull( "No default speed strategy assigned", node.getSpeedStrategy() );
		assertTrue( "Default speed strategy has wrong type", node.getSpeedStrategy() instanceof LinearTrafficDelayFunction );
		assertNotNull( "No uncertainty filter assigned", node.getUncertaintyFilter() );
		assertTrue( "Uncertainty filter has wrong type", node.getUncertaintyFilter() instanceof IdentityFilter );
		node = graph.getRoadSegment(4);
		assertEquals(0, ReflectionUtils.getProperty(node, "getCapacity") );
		assertTrue( node.getSpeedStrategy() instanceof QuadraticTrafficDelayFunction );
	}

	
	@Test
	public void graphWithUncertaintyFilter() throws JDOMException, IOException, ConfigurationException, NoSuchMethodException, ReflectionException {
		Document doc = parser.build( new StringReader( "<graph default_strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\" default_capacity=\"0\" >" +
				"<filters>" +
				"<filter type=\"uncertainty\" filter=\"adasim.filter.FakeFilter\"/>" +
				"</filters>" +
				"<node id=\"1\" neighbors=\"1 2 3 4\" delay=\"2\" capacity=\"5\"/>" +
				"<node id=\"2\" neighbors=\"3\" delay=\"2\" capacity=\"5\" strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\"/>" +
				"<node id=\"4\" neighbors=\"2 4\" delay=\"2\" strategy=\"adasim.algorithm.delay.QuadraticTrafficDelayFunction\">" +
				"<filters>" +
				"<filter type=\"uncertainty\" filter=\"adasim.filter.IdentityFilter\" />" +
				"</filters>" +
				"</node>" +
				"</graph>" ) );
		AdasimMap graph = builder.buildGraph( doc.getRootElement(), new FilterMap() );
		assertEquals( 3, graph.getRoadSegments().size() );
		RoadSegment node = graph.getRoadSegment( 1 );
		assertEquals( 3, node.getNeighbors().size() );
		assertNotNull( "No default speed strategy assigned", node.getSpeedStrategy() );
		assertTrue( "Default speed strategy has wrong type", node.getSpeedStrategy() instanceof LinearTrafficDelayFunction );
		assertNotNull( "No uncertainty filter assigned", node.getUncertaintyFilter() );
		assertTrue( "Uncertainty filter has wrong type " + node.getUncertaintyFilter().getClass(), node.getUncertaintyFilter() instanceof FakeFilter );
		node = graph.getRoadSegment(4);
		assertEquals(0, ReflectionUtils.getProperty(node, "getCapacity") );
		assertTrue( node.getSpeedStrategy() instanceof QuadraticTrafficDelayFunction );
		assertNotNull( "No uncertainty filter assigned", node.getUncertaintyFilter() );
		assertTrue( "Uncertainty filter has wrong type", node.getUncertaintyFilter() instanceof IdentityFilter );
	}

	@Test
	public void graphWithUncertaintyFilterHookup() throws JDOMException, IOException, ConfigurationException, NoSuchMethodException, ReflectionException {
		//test that the uncertainty filter gets called correctly.
		Document doc = parser.build( new StringReader( "<graph default_strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\" default_capacity=\"0\" >" +
				"<filters>" +
				"<filter type=\"uncertainty\" filter=\"adasim.filter.FakeFilter\"/>" +
				"</filters>" +
				"<node id=\"1\" neighbors=\"1 2 3 4\" delay=\"2\" capacity=\"5\"/>" +
				"<node id=\"2\" neighbors=\"3\" delay=\"2\" capacity=\"5\" strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\"/>" +
				"<node id=\"4\" neighbors=\"2 4\" delay=\"2\" strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\" />" +
				"</graph>" ) );
		AdasimMap graph = builder.buildGraph( doc.getRootElement(), new FilterMap() );
		assertEquals( 3, graph.getRoadSegments().size() );
		RoadSegment node = graph.getRoadSegment(4);
		assertNotNull( "No uncertainty filter assigned", node.getUncertaintyFilter() );
		assertTrue( "Uncertainty filter has wrong type", node.getUncertaintyFilter() instanceof FakeFilter );
		//this "fake" node has a capacity of -1, because it has not passed validation yet
		assertEquals( 0, ReflectionUtils.getProperty(node, "getCapacity") );
		assertEquals( 2, ReflectionUtils.getProperty(node, "getDelay") );	//pulls the unfiltered delay
		assertEquals( 3, node.getCurrentDelay( this.getClass() ) );			//expected: delay + 1 + 0 (configured delay + FakeFilter + IdentityFilter)
		assertEquals( 1, node.numVehiclesAtNode() );
		assertEquals( 4, node.getID() );
		assertFalse( node.isClosed() ); 
	}

	@Test
	public void graphWithPrivacyFilterElement() throws JDOMException, IOException, ConfigurationException, NoSuchMethodException, ReflectionException {
		Document doc = parser.build( new StringReader( "<graph default_strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\" default_capacity=\"0\">" +
				"<filters>" +
				"<filter type=\"privacy\" filter=\"adasim.filter.FakeFilter\" criterion=\"adasim.model.internal.SimulationXMLBuilderTest\"/>" +
				"</filters>" +
				"<node id=\"1\" neighbors=\"1 2 3 4\" delay=\"2\" capacity=\"5\"/>" +
				"<node id=\"2\" neighbors=\"3\" delay=\"2\" capacity=\"5\" strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\"/>" +
				"<node id=\"4\" neighbors=\"2 4\" delay=\"2\" strategy=\"adasim.algorithm.delay.QuadraticTrafficDelayFunction\" >" +
				"<filters>" +
				"<filter type=\"privacy\" filter=\"adasim.filter.IdentityFilter\" criterion=\"adasim.model.internal.SimulationXMLBuilderTest\"/>" +
				"</filters>" +
				"</node>" +
				"</graph>" ) );
		AdasimMap graph = builder.buildGraph( doc.getRootElement(), new FilterMap() );
		assertEquals( 3, graph.getRoadSegments().size() );
		RoadSegment node = graph.getRoadSegment( 1 );
		assertEquals( 3, node.getNeighbors().size() );
		assertNotNull( "No default speed strategy assigned", node.getSpeedStrategy() );
		assertTrue( "Default speed strategy has wrong type", node.getSpeedStrategy() instanceof LinearTrafficDelayFunction );
		assertNotNull( "No privacy filter assigned", node.getPrivacyFilter(this.getClass()) );
		assertTrue( "Privacy filter has wrong type " + node.getPrivacyFilter(this.getClass()).getClass().getCanonicalName() , node.getPrivacyFilter(this.getClass()) instanceof FakeFilter );
		node = graph.getRoadSegment(4);
		assertEquals(0, ReflectionUtils.getProperty(node, "getCapacity") );
		assertTrue( node.getSpeedStrategy() instanceof QuadraticTrafficDelayFunction );
		assertNotNull( "No privacy filter assigned", node.getPrivacyFilter(this.getClass()) );
		assertTrue( "Privacy filter has wrong type", node.getPrivacyFilter(this.getClass()) instanceof IdentityFilter );
	}
	
	@Test
	public void carNoOptionals() throws JDOMException, IOException, NoSuchMethodException, ReflectionException {
		Document doc = parser.build( new StringReader( "<car id=\"27\" start=\"1\" end=\"1\"/>" ) );
		Vehicle car = builder.buildVehicle( doc.getRootElement() );
		assertEquals( 27, car.getID() );
		assertNull( ReflectionUtils.getProperty( car, "getStartNode") );
		assertNull( ReflectionUtils.getProperty( car, "getEndNode") );
		assertNull( car.getStrategy() );
	}
	
	@Test
	public void carAllOptionals() throws JDOMException, IOException, NoSuchMethodException, ReflectionException {
		Document doc = parser.build( new StringReader( "<car id=\"27\" start=\"1\" end=\"1\" strategy=\"adasim.algorithm.routing.ShortestPathRoutingAlgorithm\" />" ) );
		Vehicle car = builder.buildVehicle( doc.getRootElement() );
		assertEquals( 27, car.getID() );
		assertNull( ReflectionUtils.getProperty( car, "getStartNode") );
		assertNull( ReflectionUtils.getProperty( car, "getEndNode") );
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
		assertFalse( "Routing strategies are ==", cars.get(0).getStrategy() == cars.get(2).getStrategy() );
	}
	
	@Test
	public void agentNoOptionals() throws JDOMException, IOException, ConfigurationException {
		Document doc = parser.build( new StringReader( "<agent id=\"27\" class=\"adasim.model.internal.FakeAgent\" />" ) );
		builder.buildAgent( doc.getRootElement(), new FilterMap() );
	}

	@Test
	public void agentAllOptionals() throws JDOMException, IOException, ConfigurationException {
		Document doc = parser.build( new StringReader( "<agent id=\"27\" class=\"adasim.model.internal.FakeAgent\" parameters=\"blabab\">" + 
				"<filters>" +
				"<filter type=\"privacy\" filter=\"adasim.filter.FakeFilter\" criterion=\"adasim.model.internal.SimulationXMLBuilderTest\"/>" +
				"<filter type=\"uncertainty\" filter=\"adasim.filter.FakeFilter\" criterion=\"adasim.model.internal.SimulationXMLBuilderTest\"/>" +
				"</filters>" +
				"</agent>"
		));
		AbstractAdasimAgent agent = (AbstractAdasimAgent)builder.buildAgent( doc.getRootElement(), new FilterMap() );
		assertNotNull( "No default privacy filter assigned", agent.getPrivacyFilter(Object.class) );
		assertTrue( "Privacy filter has wrong type", agent.getPrivacyFilter(Object.class) instanceof IdentityFilter );
		assertNotNull( "No privacy filter assigned for THIS", agent.getPrivacyFilter(this.getClass()) );
		assertTrue( "Privacy filter has wrong type " + agent.getPrivacyFilter(this.getClass()).getClass(), agent.getPrivacyFilter(this.getClass()) instanceof FakeFilter );
		assertNotNull( "No uncertainty filter assigned", agent.getUncertaintyFilter() );
		assertTrue( "Uncertaitny filter has wrong type", agent.getUncertaintyFilter() instanceof FakeFilter );
	}
	
	@Test
	public void filtersWithoutAgentDeclaration() throws JDOMException, IOException {
		Document doc = parser.build( new StringReader( "<filters>" +
				"<filter type=\"privacy\" filter=\"adasim.filter.FakeFilter\" criterion=\"adasim.model.internal.SimulationXMLBuilderTest\"/>" +
				"<filter type=\"uncertainty\" filter=\"adasim.filter.FakeFilter\" criterion=\"adasim.model.internal.SimulationXMLBuilderTest\"/>" +
				"</filters>" 
		));
		FilterMap fm = builder.buildFilters(doc.getRootElement(), new FilterMap(), Object.class );
		assertNotNull( "Default agent has no uncertainty filter", fm.get( Object.class ) );
		assertEquals( "Default agent has wrong uncertainty filter type", FakeFilter.class, fm.get( Object.class).uncertaintyFilter.getClass() );
	}

}

class FakeAgent extends AbstractAdasimAgent {
	
	public FakeAgent(String s) {}

	/* (non-Javadoc)
	 * @see adasim.model.AdasimAgent#takeSimulationStep()
	 */
	@Override
	public void takeSimulationStep( long cycle) {}

	/* (non-Javadoc)
	 * @see adasim.agent.AdasimAgent#getID()
	 */
}

class FakeFilter2 implements AdasimFilter {

	public FakeFilter2() {
	}

	@Override
	public byte filter(byte b) {
		return 0;
	}

	@Override
	public char filter(char b) {
		return 0;
	}

	@Override
	public short filter(short b) {
		return 0;
	}

	@Override
	public int filter(int b) {
		return 0;
	}

	@Override
	public long filter(long b) {
		return 0;
	}

	@Override
	public float filter(float b) {
		return 0;
	}

	@Override
	public double filter(double b) {
		return 0;
	}

	@Override
	public boolean filter(boolean b) {
		return false;
	}

	@Override
	public <T> T filter(T b) {
		return null;
	}
}

class FakeFilter3 implements AdasimFilter {

	public FakeFilter3() {
	}

	@Override
	public byte filter(byte b) {
		return 0;
	}

	@Override
	public char filter(char b) {
		return 0;
	}

	@Override
	public short filter(short b) {
		return 0;
	}

	@Override
	public int filter(int b) {
		return 0;
	}

	@Override
	public long filter(long b) {
		return 0;
	}

	@Override
	public float filter(float b) {
		return 0;
	}

	@Override
	public double filter(double b) {
		return 0;
	}

	@Override
	public boolean filter(boolean b) {
		return false;
	}

	@Override
	public <T> T filter(T b) {
		return null;
	}
}

class FakeFilter4 implements AdasimFilter {

	public FakeFilter4() {
	}

	@Override
	public byte filter(byte b) {
		return 0;
	}

	@Override
	public char filter(char b) {
		return 0;
	}

	@Override
	public short filter(short b) {
		return 0;
	}

	@Override
	public int filter(int b) {
		return 0;
	}

	@Override
	public long filter(long b) {
		return 0;
	}

	@Override
	public float filter(float b) {
		return 0;
	}

	@Override
	public double filter(double b) {
		return 0;
	}

	@Override
	public boolean filter(boolean b) {
		return false;
	}

	@Override
	public <T> T filter(T b) {
		return null;
	}
}