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
 * Created: Dec 9, 2011
 */

package traffic.strategy;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.BeforeClass;
import org.junit.Test;

import traffic.graph.Graph;
import traffic.model.ConfigurationException;
import traffic.model.SimulationFactory;


/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class DijkstraCarStrategyTest {

	private static DijkstraCarStrategy strategy;
	
	@BeforeClass
	public static void beforeClass() {
		strategy = new DijkstraCarStrategy();
	}
	
	@Test
	public void blowsUpWithNonConsecutiveIDs() throws FileNotFoundException, ConfigurationException {
		Graph g = SimulationFactory.buildSimulator( new File("resources/test/invalid-neighbor.xml") ).getGraph();
		strategy.getPath(g, 5, 9 );
	}
}
