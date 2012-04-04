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
 * Created: Mar 21, 2012
 */

package traffic.agent;

import traffic.filter.AdasimFilter;
import traffic.model.TrafficSimulator;

/**
 * This is an abstract adapter class for {@link AdasimAgent}.
 * The methods do nothing. Override those that you need.
 * 
 * It explicitly <em>does not</em> implement <code>takeSimulationStep()</code>
 * as this method must always be provided by implementers.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public abstract class AbstractAdasimAgent implements AdasimAgent {
	
	protected TrafficSimulator simulator;
	protected AdasimFilter privacyFilter;
	protected AdasimFilter uncertaintyFilter;

	/**
	 * Does nothing (ignores the parameter!).
	 */
	@Override
	public void setParameters(String params) {
	}

	/**
	 * Stores the {@link TrafficSimulator} parameter in the protected 
	 * field <code>simulator</code>.
	 */
	@Override
	public void setSimulation(TrafficSimulator sim) {
		this.simulator = sim;
	}

	/**
	 * This stub implementation will always return <code>true</code>.
	 */
	@Override
	public boolean isFinished() {
		return true;
	}


	/**
	 * Sets the filter for sensor uncertainty.
	 * Assigning <code>null</code> is legal here. The agent
	 * implementation is responsible for ensuring safe behavior.
	 * 
	 * @param the uncertainty filter
	 */
	@Override
	public void setUncertaintyFilter(AdasimFilter filter) {
		uncertaintyFilter = filter;
	}

	/**
	 * Sets the filter for data privacy.
	 * Assigning <code>null</code> is legal here. The agent
	 * implementation is responsible for ensuring safe behavior.
	 * 
	 * @param the uncertainty filter
	 */
	@Override
	public void setPrivacyFilter(AdasimFilter filter) {
		privacyFilter = filter;
	}

}
