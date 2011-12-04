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
 * Created: Nov 22, 2011
 */

package traffic.strategy;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import traffic.factory.GraphFactory;
import traffic.graph.Graph;


/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class LookaheadDijkstraCarStrategyTest {
	
	private LookaheadDijkstraCarStrategy strategy;
	
	@Before
	public void setUp() {
		strategy = new LookaheadDijkstraCarStrategy(3);
	}
	
	@Test
	public void findShortestPathFromStart() {
		Graph g = GraphFactory.loadGraph( "lookahead-test.xml" );
		List<Integer> path = strategy.getPath(g, 1, 5);
		assertNotNull( "No path found", path );
		assertEquals( "Path too short", 4, path.size() );
		assertEquals( 6, (int)path.get(3) );
	}

}
