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
 * Created: Jan 24, 2012
 */

package traffic.model;

/**
 * An {@link AdasimAgent} is an active component in Adasim.
 * Each agent registers itself with the simulation framework,
 * and at each cycle of the simulation, it can perform
 * an action.
 * <p>
 * Typical agents would be Vehicles, and their actions might be moving 
 * and making routing decisions.
 * An agent can be anything, even a virtual agent not tied to a 
 * "real" object in the simulation.
 * <p>
 * Agents <strong>must have</strong> a constructor that takes a String argument.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public interface AdasimAgent {
	/**
	 * This is the main method through which the {@link TrafficSimulator} 
	 * communicates with agents. The {@link TrafficSimulator} call this
	 * method once per simulation cylce on each agent. 
	 */
	public void takeSimulationStep();
	
	/**
	 * Method called to configure this agent when loading a simulation.
	 * 
	 * @param params
	 */
	public void setParameters( String params );
}
