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
 * Created: Dec 12, 2011
 */

package traffic.generator;

import org.junit.Test;


/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class SimulationBuilderTest {
	
	@Test//(timeout=10000)
	public void testBuildTerminates() throws Exception {
		ConfigurationOptions opts = ConfigurationOptions.parse( new String[] { "-N", "1000", "-C", "500", 
				"-D", "4", "-o", "random.xml", "-d", "3:6", "-S", 
				"traffic.algorithm.LookaheadShortestPathRoutingAlgorithm", "--one-way-prob", "0.05", "--node-capacity", "0"});
		new SimulationBuilder().build(opts);
		//passes when it doesn't crash
	}
	
	@Test
	public void loadGraphFile() throws Exception {
		ConfigurationOptions opts = ConfigurationOptions.parse( new String[] { "-N", "1000", "-C", "500", 
				"-D", "4", "-o", "random.xml", "-d", "3:6", "-S", 
				"traffic.algorithm.LookaheadShortestPathRoutingAlgorithm", "--one-way-prob", "0.05", "--node-capacity", "0",
				"--graph", "resources/xml/city-map.xml"});
		new SimulationBuilder().build(opts);
		//for now this passes if it doesn't crash
	}

}
