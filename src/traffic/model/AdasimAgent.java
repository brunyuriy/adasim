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
}
