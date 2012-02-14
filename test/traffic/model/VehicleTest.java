/*******************************************************************************
 * Copyright (C) 2011 - 2012 Jochen Wuttke, Jonathan Ramaswamy
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
 *    Jonathan Ramaswamy (ramaswamyj12@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Feb 14, 2012
 */

package traffic.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;


/**
 * 
 * Test for the vehicle class
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 */

public class VehicleTest {

	@Test
	public void startNodeTest() throws FileNotFoundException, ConfigurationException {
		TrafficSimulator sim = SimulationXMLReader.buildSimulator( new File( "resources/test/config.xml" ) );
		List<Vehicle> vehicles = sim.getVehicles();
		assertEquals(vehicles.get(0).getStartNode().getID(), 0);
		assertEquals(vehicles.get(2).getStartNode().getID(), 3);
		assertEquals(vehicles.get(3).getStartNode().getID(), 8);
		vehicles.get(3).setStartNode(vehicles.get(2).getCurrentPosition());
		assertEquals(vehicles.get(3).getStartNode().getID(), 3);

	}
}
