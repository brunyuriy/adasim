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
 * Created: Nov 29, 2011
 */

package traffic.model;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;


/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class TrafficSimulatorTest {
	
	@Test
	public void zeroLengthPathTest() throws FileNotFoundException, ConfigurationException {
		TrafficSimulator sim = SimulationXMLReader.buildSimulator( new File( "resources/test/random.xml" ) );
		sim.run();
	}
	
	@Test
	public void basicConfigTest() throws FileNotFoundException, ConfigurationException {
		TrafficSimulator sim = SimulationXMLReader.buildSimulator( new File( "resources/test/shortest-path-test-weights.xml" ) );
		sim.run();
	}
	
	@Test
	public void addingVehiclesTest() throws FileNotFoundException, ConfigurationException {
		TrafficSimulator sim = SimulationXMLReader.buildSimulator( new File( "resources/test/only-delayed-cars.xml" ) );
		sim.run();
	}
	
}
