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

package traffic.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;

import traffic.graph.Graph;
import traffic.graph.GraphNode;
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
		Document doc = parser.build( new StringReader( "<node id=\"27\" neighbors=\"1 2 3\" delay=\"2\" capacity=\"5\" strategy=\"traffic.strategy.LinearSpeedStrategy\"/>" ) );
		GraphNode node = builder.buildNode( doc.getRootElement() );
		assertEquals( 27, node.getID() );
		assertTrue( node.getNeighbors().isEmpty() );
		assertTrue( node.getSpeedStrategy() instanceof LinearSpeedStrategy );
		assertEquals(5, node.getCapacity() );
		assertEquals(2, node.getDelay() );
	}
	
	@Test
	public void graphWithDefaults() throws JDOMException, IOException, ConfigurationException {
		//node 2 will be discarded at it has no valid neighbors
		Document doc = parser.build( new StringReader( "<graph default_strategy=\"traffic.strategy.LinearSpeedStrategy\" default_capacity=\"0\">" +
				"<node id=\"1\" neighbors=\"1 2 3 4\" delay=\"2\" capacity=\"5\" strategy=\"traffic.strategy.LinearSpeedStrategy\"/>" +
				"<node id=\"2\" neighbors=\"3\" delay=\"2\" capacity=\"5\" strategy=\"traffic.strategy.LinearSpeedStrategy\"/>" +
				"<node id=\"4\" neighbors=\"2 4\" delay=\"2\" strategy=\"traffic.strategy.QuadraticSpeedStrategy\"/>" +
				"</graph>" ) );
		Graph graph = builder.buildGraph( doc.getRootElement() );
		assertEquals( 2, graph.getNodes().size() );
		GraphNode node = graph.getNode( 1 );
		assertEquals( 2, node.getNeighbors().size() );
		node = graph.getNode(4);
		assertEquals(0, node.getCapacity() );
		assertTrue( node.getSpeedStrategy() instanceof QuadraticSpeedStrategy );
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
		Document doc = parser.build( new StringReader( "<agent id=\"27\" class=\"traffic.model.FakeAgent\" />" ) );
		AdasimAgent agent = builder.buildAgent( doc.getRootElement() );
	}

	@Test
	public void agentAllOptionals() throws JDOMException, IOException, ConfigurationException {
		Document doc = parser.build( new StringReader( "<agent id=\"27\" class=\"traffic.model.FakeAgent\" parameters=\"blabab\"/>" ) );
		AdasimAgent agent = builder.buildAgent( doc.getRootElement() );
	}

}

class FakeAgent implements AdasimAgent {
	
	public FakeAgent(String s) {
		
	}
	
	/* (non-Javadoc)
	 * @see traffic.model.AdasimAgent#takeSimulationStep()
	 */
	@Override
	public void takeSimulationStep() {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see traffic.model.AdasimAgent#setParameters(java.lang.String)
	 */
	@Override
	public void setParameters(String params) {
		// TODO Auto-generated method stub
		
	}
	
}