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
 * A road agent can close roads, simulating accidents or construction
 * Once a road is closed, a car cannot enter it, and cars on the road cannot move
 * @author Jonathan Ramaswamy
 *
 */
public class RoadAgent extends AbstractAdasimAgent {

	public RoadAgent() {
		
	}
	
	/**
	 * Has a 1 in 20 chance of returning true, indicating the road is closed
	 * @return
	 */
	public boolean isClosed() {
		Random generator = new Random();
		int rand = generator.nextInt(3);
		return rand == 1;
	}

	@Override
	public void setParameters(String params) {
		
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	/* (non-Javadoc)
	 * @see traffic.model.AdasimAgent#takeSimulationStep(long)
	 */
	@Override
	public void takeSimulationStep(long cycle) {
		// TODO Auto-generated method stub
		
	}
}
