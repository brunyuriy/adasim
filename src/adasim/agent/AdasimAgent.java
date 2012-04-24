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

package adasim.agent;

import adasim.filter.AdasimFilter;
import adasim.model.TrafficSimulator;

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
	 * method once per simulation cycle on each agent. 
	 *
	 * @param cycle the current simulation cycle. 
	 */
	public void takeSimulationStep(long cycle);
	
	/**
	 * Method called to configure this agent when loading a simulation.
	 * 
	 * @param params
	 */
	public void setParameters( String params );
	
	/**
	 * Injects the {@link TrafficSimulator} instance that contains this agent.
	 * Access to this information is necessary when the agent wants structural
	 * info about the simulation (graph, other agents, etc).
	 * <p>
	 * As a consequence, all agents should access the relevant state only
	 * through this reference. (this is more of an internal development note).
	 * 
	 * @param sim
	 */
	public void setSimulation( TrafficSimulator sim );
	
	/**
	 * This method is used by the simulator to determine whether the entire 
	 * simulation can stop or not. Each agent should respond with
	 * <code>false</code> as long as it still might want to perform actions,
	 * and with <code>true</code> once it no longer desires to perform actions.
	 * <p>
	 * Once an agent has responded with <code>true</code>, the simulator is
	 * no longer obliged to send cylce notifications to this agent.
	 * <p>
	 * Once an agent has responded with <code>true</code>, it should never
	 * respond with <code>false</code> afterwards.
	 * 
	 * @return true when the agent does no longer wish to perform actions
	 */
	public boolean isFinished();
	
	/**
	 * Sets the uncertainty filter that this agent applies to
	 * all its output.
	 * 
	 * @param filter
	 */
	public void setUncertaintyFilter( AdasimFilter filter );
	
	/**
	 * Sets the privacy filter that this agent applies to all its output.
	 * @param filter
	 */
	public void setPrivacyFilter( AdasimFilter filter, Class<?> criterion );
	
	/**
	 * @return the agents unique ID
	 */
	public int getID();
	
	/**
	 * @param id - the unique ID of this agent
	 */
	public void setID( int id );
}
