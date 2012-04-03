/*******************************************************************************
 * Copyright (C) 2011 - 2012 Jochen Wuttke
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.

 *
 * Contributors:
 *    Jochen Wuttke (wuttkej@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Mar 27, 2012
 */

package traffic.model.internal;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;

import traffic.agent.AdasimAgent;
import traffic.graph.Graph;
import traffic.graph.GraphNode;
import traffic.model.ConfigurationException;
import traffic.model.SimulationXMLBuilder;
import traffic.model.TrafficSimulator;
import traffic.model.Vehicle;

/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class VehicleManagerTest {
	
	private VehicleManager manager;
	private Graph graph;
	
	@Before
	public void setUp() {
		manager = new VehicleManager();
	}
	
	@Test
	public void addVehicle() {
		int pre_size = manager.getQueue().size();
		manager.addVehicle( new Vehicle(null, null, null, 1), 5);
		assertEquals( pre_size + 1, manager.getQueue().size() );
		assertNotNull( manager.getQueue().get(5L) );
		assertNotNull( manager.getQueue().get(5L).get(0) );
	}

	@Test
	public void cycleVehiclesIntoGraph() throws JDOMException, IOException, ConfigurationException {
		SAXBuilder parser = new SAXBuilder( false );
		SimulationXMLBuilder builder = new SimulationXMLBuilder();
		Document doc = parser.build( new StringReader( "<graph default_strategy=\"traffic.strategy.LinearSpeedStrategy\" default_capacity=\"0\">" +
				"<node id=\"1\" neighbors=\"1 2 3 4\" delay=\"2\" capacity=\"5\" strategy=\"traffic.strategy.LinearSpeedStrategy\"/>" +
				"<node id=\"2\" neighbors=\"3\" delay=\"2\" capacity=\"5\" strategy=\"traffic.strategy.LinearSpeedStrategy\"/>" +
				"<node id=\"4\" neighbors=\"2 4\" delay=\"2\" strategy=\"traffic.strategy.QuadraticSpeedStrategy\"/>" +
				"<node id=\"5\" neighbors=\"2 4\" delay=\"2\" strategy=\"traffic.strategy.LinearSpeedStrategy\"/>" +
				"</graph>" ) );
		graph = builder.buildGraph( doc.getRootElement() );
		new TrafficSimulator(graph, manager, (List<AdasimAgent>)new ArrayList<AdasimAgent>() );
		assertEquals( 2, graph.getNode(5).getCurrentDelay() );
		GraphNode start = graph.getNode(5);
		manager.addVehicle( new Vehicle(start, null, null, 1), 5);
		manager.takeSimulationStep(4);
		assertEquals( 2, graph.getNode(5).getCurrentDelay() );
		manager.takeSimulationStep(5);
		assertEquals( 3, graph.getNode(5).getCurrentDelay() );
		assertEquals( 0, manager.getQueue().size() );
	}

}
