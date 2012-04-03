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
 *    Jochen Wuttke (wuttkej@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Mar 14, 2012
 */
package traffic.model;

import java.util.Random;

/**
 * The RoadClosureAgent randomly selects roads to be closed
 * to traffic, and randomly opens them again for traffic
 * at a later time. This is a naive example that mostly illustrates 
 * how road-closures, for example due to accidents or 
 * traffic control measures can be simulated.
 * <p>
 * The agent takes two parameters: the probability with which
 * a road is closed in a cycle, and the time it stays closed.
 * <p>
 *
 * @author Jochen Wuttke - wuttkej@gmail.com
 */

public final class RoadClosureAgent extends AbstractAdasimAgent {

	private Random randomizer;
	
	public RoadClosureAgent() {
		randomizer = new Random();
	}
	
	/**
	 * This constructor is intended for testing only. It should guarantee
	 * the same random results on each run given a fixed seed.
	 * 
	 * @param seed
	 */
	RoadClosureAgent( long seed ) {
		randomizer = new Random(seed);		
	}
	
	@Override
	public void setParameters(String params) {
		
	}

	/* (non-Javadoc)
	 * @see traffic.model.AdasimAgent#takeSimulationStep(long)
	 */
	@Override
	public void takeSimulationStep(long cycle) {
		// TODO Auto-generated method stub
		
	}
}
