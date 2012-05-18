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

package adasim.model.internal;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;

import pjunit.ProtectedAccessor;

import adasim.LoggingTest;
import adasim.agent.AdasimAgent;
import adasim.model.AdasimMap;
import adasim.model.ConfigurationException;
import adasim.model.RoadSegment;
import adasim.model.TrafficSimulator;
import adasim.model.Vehicle;
import adasim.model.internal.SimulationXMLBuilder;
import adasim.model.internal.VehicleManager;
import adasim.util.ReflectionException;
import adasim.util.ReflectionUtils;


/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class VehicleManagerTest extends LoggingTest {
	
	private VehicleManager manager;
	private AdasimMap graph;
	
	@Before
	public void setUp() {
		manager = new VehicleManager();
	}
	
	@Test
	public void addVehicle() throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		int pre_size = getQueue(manager).size();
		manager.addVehicle( new Vehicle(null, null, null, 1), 5);
		assertEquals( pre_size + 1, getQueue(manager).size() );
		assertNotNull( getQueue(manager).get(5L) );
		assertNotNull( getQueue(manager).get(5L).get(0) );
	}

	@Test
	public void cycleVehiclesIntoGraph() throws JDOMException, IOException, ConfigurationException, NoSuchMethodException, ReflectionException, SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		SAXBuilder parser = new SAXBuilder( false );
		SimulationXMLBuilder builder = new SimulationXMLBuilder();
		Document doc = parser.build( new StringReader( "<graph default_strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\" default_capacity=\"0\">" +
				"<node id=\"1\" neighbors=\"1 2 3 4\" delay=\"2\" capacity=\"5\" strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\"/>" +
				"<node id=\"2\" neighbors=\"3\" delay=\"2\" capacity=\"5\" strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\"/>" +
				"<node id=\"4\" neighbors=\"2 4\" delay=\"2\" strategy=\"adasim.algorithm.delay.QuadraticTrafficDelayFunction\"/>" +
				"<node id=\"5\" neighbors=\"2 4\" delay=\"2\" strategy=\"adasim.algorithm.delay.LinearTrafficDelayFunction\"/>" +
				"</graph>" ) );
		graph = builder.buildGraph( doc.getRootElement(), new FilterMap() );
		new TrafficSimulator(graph, manager, (List<AdasimAgent>)new ArrayList<AdasimAgent>() );
		assertEquals( 2, ReflectionUtils.getProperty(graph.getRoadSegment(5), "getCurrentDelay") );
		RoadSegment start = graph.getRoadSegment(5);
		manager.addVehicle( new Vehicle(start, null, null, 1), 5);
		manager.takeSimulationStep(4);
		assertEquals( 2, ReflectionUtils.getProperty(graph.getRoadSegment(5), "getCurrentDelay") );
		manager.takeSimulationStep(5);
		assertEquals( 3, ReflectionUtils.getProperty(graph.getRoadSegment(5), "getCurrentDelay") );
		assertEquals( 0, getQueue(manager).size() );
	}
	
	private Map<Long, List<Vehicle>> getQueue( VehicleManager manager ) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		return ProtectedAccessor.invoke(manager, "getQueue", new Object[0] );
	}

}
