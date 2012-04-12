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

package traffic.model.internal;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;

import traffic.agent.AbstractAdasimAgent;
import traffic.agent.AdasimAgent;
import traffic.filter.AdasimFilter;
import traffic.filter.IdentityFilter;
import traffic.model.ConfigurationException;
import traffic.model.Graph;
import traffic.model.GraphNode;
import traffic.model.Vehicle;
import traffic.model.internal.SimulationXMLBuilder;
import traffic.strategy.AlwaysRecomputeVehicleStrategy;
import traffic.strategy.LinearSpeedStrategy;
import traffic.strategy.QuadraticSpeedStrategy;
import traffic.strategy.ShortestPathVehicleStrategy;

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
		GraphNode node = builder.buildNode( doc.getRootElement() );
		assertEquals( 27, node.getID() );
		assertTrue( node.getNeighbors().isEmpty() );
		assertNull( node.getSpeedStrategy() );
		assertEquals(-1, node.getCapacity() );
		assertEquals(1, node.getDelay() ); //1 is the default delay
	}
	
	@Test
	public void nodeNoAllOptionals() throws JDOMException, IOException {
		Document doc = parser.build( new StringReader( "<node id=\"27\" neighbors=\"1 2 3\" delay=\"2\" capacity=\"5\" strategy=\"traffic.strategy.LinearSpeedStrategy\" uncertainty_filter=\"traffic.filter.IdentityFilter\"/>" ) );
		GraphNode node = builder.buildNode( doc.getRootElement() );
		assertEquals( 27, node.getID() );
		assertTrue( node.getNeighbors().isEmpty() );
		assertTrue( node.getSpeedStrategy() instanceof LinearSpeedStrategy );
		assertEquals(5, node.getCapacity() );
		assertEquals(2, node.getDelay() );
		assertNotNull( "No uncertainty filter assigned", node.getUncertaintyFilter() );
		assertTrue( "Uncertainty filter has wrong type", node.getUncertaintyFilter() instanceof IdentityFilter );
	}
	
	@Test
	public void graphWithDefaults() throws JDOMException, IOException, ConfigurationException {
		Document doc = parser.build( new StringReader( "<graph default_strategy=\"traffic.strategy.LinearSpeedStrategy\" default_capacity=\"0\">" +
				"<node id=\"1\" neighbors=\"1 2 3 4\" delay=\"2\" capacity=\"5\"/>" +
				"<node id=\"2\" neighbors=\"3\" delay=\"2\" capacity=\"5\" strategy=\"traffic.strategy.LinearSpeedStrategy\"/>" +
				"<node id=\"4\" neighbors=\"2 4\" delay=\"2\" strategy=\"traffic.strategy.QuadraticSpeedStrategy\"/>" +
				"</graph>" ) );
		Graph graph = builder.buildGraph( doc.getRootElement() );
		assertEquals( 3, graph.getNodes().size() );
		GraphNode node = graph.getNode( 1 );
		assertEquals( 3, node.getNeighbors().size() );
		assertNotNull( "No default speed strategy assigned", node.getSpeedStrategy() );
		assertTrue( "Default speed strategy has wrong type", node.getSpeedStrategy() instanceof LinearSpeedStrategy );
		assertNotNull( "No uncertainty filter assigned", node.getUncertaintyFilter() );
		assertTrue( "Uncertainty filter has wrong type", node.getUncertaintyFilter() instanceof IdentityFilter );
		node = graph.getNode(4);
		assertEquals(0, node.getCapacity() );
		assertTrue( node.getSpeedStrategy() instanceof QuadraticSpeedStrategy );
	}

	
	@Test
	public void graphWithUncertaintyFilter() throws JDOMException, IOException, ConfigurationException {
		Document doc = parser.build( new StringReader( "<graph default_strategy=\"traffic.strategy.LinearSpeedStrategy\" default_capacity=\"0\" uncertainty_filter=\"traffic.model.internal.FakeFilter\">" +
				"<node id=\"1\" neighbors=\"1 2 3 4\" delay=\"2\" capacity=\"5\"/>" +
				"<node id=\"2\" neighbors=\"3\" delay=\"2\" capacity=\"5\" strategy=\"traffic.strategy.LinearSpeedStrategy\"/>" +
				"<node id=\"4\" neighbors=\"2 4\" delay=\"2\" strategy=\"traffic.strategy.QuadraticSpeedStrategy\" uncertainty_filter=\"traffic.filter.IdentityFilter\"/>" +
				"</graph>" ) );
		Graph graph = builder.buildGraph( doc.getRootElement() );
		assertEquals( 3, graph.getNodes().size() );
		GraphNode node = graph.getNode( 1 );
		assertEquals( 3, node.getNeighbors().size() );
		assertNotNull( "No default speed strategy assigned", node.getSpeedStrategy() );
		assertTrue( "Default speed strategy has wrong type", node.getSpeedStrategy() instanceof LinearSpeedStrategy );
		assertNotNull( "No uncertainty filter assigned", node.getUncertaintyFilter() );
		assertTrue( "Uncertainty filter has wrong type", node.getUncertaintyFilter() instanceof FakeFilter );
		node = graph.getNode(4);
		assertEquals(0, node.getCapacity() );
		assertTrue( node.getSpeedStrategy() instanceof QuadraticSpeedStrategy );
		assertNotNull( "No uncertainty filter assigned", node.getUncertaintyFilter() );
		assertTrue( "Uncertainty filter has wrong type", node.getUncertaintyFilter() instanceof IdentityFilter );
	}

	@Test
	public void graphWithUncertaintyFilterHookup() throws JDOMException, IOException, ConfigurationException {
		//test that the uncertainty filter gets called correctly.
		Document doc = parser.build( new StringReader( "<graph default_strategy=\"traffic.strategy.LinearSpeedStrategy\" default_capacity=\"0\" uncertainty_filter=\"traffic.model.FakeFilter\">" +
				"<node id=\"1\" neighbors=\"1 2 3 4\" delay=\"2\" capacity=\"5\"/>" +
				"<node id=\"2\" neighbors=\"3\" delay=\"2\" capacity=\"5\" strategy=\"traffic.strategy.LinearSpeedStrategy\"/>" +
				"<node id=\"4\" neighbors=\"2 4\" delay=\"2\" strategy=\"traffic.strategy.LinearSpeedStrategy\" uncertainty_filter=\"traffic.model.internal.FakeFilter\"/>" +
				"</graph>" ) );
		Graph graph = builder.buildGraph( doc.getRootElement() );
		assertEquals( 3, graph.getNodes().size() );
		GraphNode node = graph.getNode(4);
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
	public void graphWithPrivacyFilter() throws JDOMException, IOException, ConfigurationException {
		Document doc = parser.build( new StringReader( "<graph default_strategy=\"traffic.strategy.LinearSpeedStrategy\" default_capacity=\"0\" privacy_filter=\"traffic.model.internal.FakeFilter\">" +
				"<node id=\"1\" neighbors=\"1 2 3 4\" delay=\"2\" capacity=\"5\"/>" +
				"<node id=\"2\" neighbors=\"3\" delay=\"2\" capacity=\"5\" strategy=\"traffic.strategy.LinearSpeedStrategy\"/>" +
				"<node id=\"4\" neighbors=\"2 4\" delay=\"2\" strategy=\"traffic.strategy.QuadraticSpeedStrategy\" privacy_filter=\"traffic.filter.IdentityFilter\"/>" +
				"</graph>" ) );
		Graph graph = builder.buildGraph( doc.getRootElement() );
		assertEquals( 3, graph.getNodes().size() );
		GraphNode node = graph.getNode( 1 );
		assertEquals( 3, node.getNeighbors().size() );
		assertNotNull( "No default speed strategy assigned", node.getSpeedStrategy() );
		assertTrue( "Default speed strategy has wrong type", node.getSpeedStrategy() instanceof LinearSpeedStrategy );
		assertNotNull( "No privacy filter assigned", node.getPrivacyFilter() );
		assertTrue( "Privacy filter has wrong type", node.getPrivacyFilter() instanceof FakeFilter );
		node = graph.getNode(4);
		assertEquals(0, node.getCapacity() );
		assertTrue( node.getSpeedStrategy() instanceof QuadraticSpeedStrategy );
		assertNotNull( "No privacy filter assigned", node.getPrivacyFilter() );
		assertTrue( "Privacy filter has wrong type", node.getPrivacyFilter() instanceof IdentityFilter );
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
		Document doc = parser.build( new StringReader( "<car id=\"27\" start=\"1\" end=\"1\" strategy=\"traffic.strategy.ShortestPathVehicleStrategy\" />" ) );
		Vehicle car = builder.buildVehicle( doc.getRootElement() );
		assertEquals( 27, car.getID() );
		assertNull( car.getStartNode() );
		assertNull( car.getEndNode() );
		assertTrue( car.getStrategy() instanceof ShortestPathVehicleStrategy );
	}

	@Test
	public void carListWithDefaults() throws JDOMException, IOException, ConfigurationException {
		Document doc = parser.build( new StringReader( "<cars default_strategy=\"traffic.strategy.AlwaysRecomputeVehicleStrategy\">" +
				"<car id=\"1\" start=\"1\" end=\"1\" />" +
				"<car id=\"2\" start=\"1\" end=\"1\" strategy=\"traffic.strategy.ShortestPathVehicleStrategy\" />" +
				"<car id=\"3\" start=\"1\" end=\"1\" />" +
				"</cars>" ) );
		List<Vehicle> cars = builder.buildVehicles( doc.getRootElement() );
		assertEquals( 3, cars.size() );
		Vehicle car = cars.get(0);	//this should be the car with id 1
		assertEquals( 1, car.getID() );
		assertTrue( car.getStrategy() instanceof AlwaysRecomputeVehicleStrategy );
		car = cars.get(1);
		assertEquals( 2, car.getID() );
		assertTrue( car.getStrategy() instanceof ShortestPathVehicleStrategy );
	}
	
	@Test
	public void agentNoOptionals() throws JDOMException, IOException, ConfigurationException {
		Document doc = parser.build( new StringReader( "<agent id=\"27\" class=\"traffic.model.internal.FakeAgent\" />" ) );
		AdasimAgent agent = builder.buildAgent( doc.getRootElement() );
	}

	@Test
	public void agentAllOptionals() throws JDOMException, IOException, ConfigurationException {
		Document doc = parser.build( new StringReader( "<agent id=\"27\" class=\"traffic.model.internal.FakeAgent\" parameters=\"blabab\"/>" ) );
		AdasimAgent agent = builder.buildAgent( doc.getRootElement() );
	}

}

class FakeAgent extends AbstractAdasimAgent {
	
	public FakeAgent(String s) {
		
	}

	/* (non-Javadoc)
	 * @see traffic.model.AdasimAgent#takeSimulationStep()
	 */
	@Override
	public void takeSimulationStep( long cycle) {}

	/* (non-Javadoc)
	 * @see traffic.agent.AdasimAgent#setUncertaintyFilter(traffic.filter.AdasimFilter)
	 */
	@Override
	public void setUncertaintyFilter(AdasimFilter filter) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see traffic.agent.AdasimAgent#setPrivacyFilter(traffic.filter.AdasimFilter)
	 */
	@Override
	public void setPrivacyFilter(AdasimFilter filter) {
		// TODO Auto-generated method stub
		
	}
}

class FakeFilter implements AdasimFilter {
	
	/* (non-Javadoc)
	 * @see traffic.filter.AdasimFilter#filter(byte)
	 */
	@Override
	public byte filter(byte b) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see traffic.filter.AdasimFilter#filter(char)
	 */
	@Override
	public char filter(char b) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see traffic.filter.AdasimFilter#filter(short)
	 */
	@Override
	public short filter(short b) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see traffic.filter.AdasimFilter#filter(int)
	 */
	@Override
	public int filter(int b) {
		return b+1;
	}
	
	/* (non-Javadoc)
	 * @see traffic.filter.AdasimFilter#filter(long)
	 */
	@Override
	public long filter(long b) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see traffic.filter.AdasimFilter#filter(float)
	 */
	@Override
	public float filter(float b) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see traffic.filter.AdasimFilter#filter(double)
	 */
	@Override
	public double filter(double b) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see traffic.filter.AdasimFilter#filter(boolean)
	 */
	@Override
	public boolean filter(boolean b) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/* (non-Javadoc)
	 * @see traffic.filter.AdasimFilter#filter(java.lang.Object)
	 */
	@Override
	public <T> T filter(T b) {
		// TODO Auto-generated method stub
		return null;
	}
	
}