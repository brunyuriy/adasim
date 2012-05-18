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
 * Created: Apr 3, 2012
 */

package adasim.agent;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;

import adasim.LoggingTest;
import adasim.agent.RoadClosureAgent;
import adasim.model.ConfigurationException;
import adasim.model.RoadSegment;
import adasim.model.TrafficSimulator;
import adasim.model.internal.SimulationXMLReader;


/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class RoadClosureAgentTest extends LoggingTest {
	
	private RoadClosureAgent agent;
	
	@Before
	public void setUp() {
		agent = new RoadClosureAgent( "0.0:0", 27L );
	}

	@Test
	public void validParams() {
		agent.setParameters( "0.01:42" );
		assertEquals( 0.01, agent.getClosureProbability(), 1e-10 );
		assertEquals(42, agent.getClosureDuration() );
	}
	
	@Test
	public void validParamsWithSpaces() {
		agent.setParameters( " \t0.01 :42 " );
		assertEquals( 0.01, agent.getClosureProbability(), 1e-10 );
		assertEquals(42, agent.getClosureDuration() );
	}


	@Test(expected=IllegalArgumentException.class)
	public void tooManyParams() {
		agent.setParameters( "0.01:42:222" );
		fail( "Should throw IllegalArgumentException" );
	}

	@Test(expected=IllegalArgumentException.class)
	public void tooFewParams() {
		agent.setParameters( "0.01" );
		fail( "Should throw IllegalArgumentException" );
	}

	@Test(expected=IllegalArgumentException.class)
	public void invalidFirstParamType() {
		agent.setParameters( "hugo:23" );
		fail( "Should throw IllegalArgumentException" );
	}

	@Test(expected=IllegalArgumentException.class)
	public void invalidSecondParamType() {
		agent.setParameters( "0.111:hugo" );
		fail( "Should throw IllegalArgumentException" );
	}

	@Test
	public void closureTest() throws FileNotFoundException, ConfigurationException {
		TrafficSimulator sim = SimulationXMLReader.buildSimulator( new File( "resources/test/closure-test.xml" ) );
		agent.setSimulation(sim);
		agent.setParameters( "1.0:2" );
		RoadSegment node = sim.getMap().getRoadSegment( 0 );
		assertFalse( "Node is closed", node.isClosed() );
		agent.takeSimulationStep(1);
		assertTrue( "Node is open", node.isClosed() );
		agent.takeSimulationStep(2);
		assertTrue( "Node is open", node.isClosed() );
		agent.takeSimulationStep(3);
		assertTrue( "Node is open", node.isClosed() );
		agent.takeSimulationStep(4);
		assertFalse( "Node is closed", node.isClosed() );		
	}
}
