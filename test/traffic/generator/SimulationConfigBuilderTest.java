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
 * Created: Dec 3, 2011
 */

package traffic.generator;

import java.util.Arrays;

import org.jdom.Element;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class SimulationConfigBuilderTest {

	private SimulationConfigBuilder builder = new SimulationConfigBuilder( 42 ); //we need to provide the random see to be able to reproduce tests
	
	@Test
	public void buildCar() {
		Element car = builder.buildCar( Arrays.asList( "traffic.strategy.LookaheadDijkstraCarStrategy" ), 42, 56 );
		assertEquals( "Returned wrong node type", "car", car.getName() );
		assertEquals( "Returned wrong node id", "42", car.getAttribute( "id" ).getValue() );
		assertEquals("Returned wrong car strategy", "traffic.strategy.LookaheadDijkstraCarStrategy" , car.getAttribute( "strategy" ).getValue() );
	}
}
