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
 *    Jonathan Ramaswamy (ramaswamyj12@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Jul 29, 2011
 */

package adasim.model;

import java.util.*;

import org.apache.log4j.Logger;

import adasim.TrafficMain;
import adasim.agent.AdasimAgent;
import adasim.model.internal.VehicleManager;
import adasim.util.ReflectionException;
import adasim.util.ReflectionUtils;


/**
 * TrafficSimulator is the core the simulator. It keeps track
 * of the map and where all the vehicles are located on it, and outputs information to
 * the logger object with every step taken.
 * <p>
 * Usually there is no need to explicitly create an object of this type. Normal use of
 * the simulator is handled through the commandline interface of {@link TrafficMain}.
 * <p>
 * There are several types of agents and other active elements in a
 * simulation. Currently the simulator enforces the following 
 * protocol in each cycle <em>t</em>:
 * <ol>
 * <li>Vehicles scheduled to start in cycle <em>t</em> are added to the simulation
 * <li>All agents (including vehicles, but not GraphNodes) can perform an action
 * <li>All GraphNodes execute the vehicle movement protocol (documented in {@link RoadSegment}).
 * </ol>
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 * @author Jochen Wuttke - wuttkej@gmail.com
 */

public final class TrafficSimulator{

	private static Logger logger = Logger.getLogger(TrafficSimulator.class);


	private List<AdasimAgent> agents; //List of vehicles in the simulation
	private AdasimMap map; //The map the vehicles run on
	private VehicleManager manager;
	private long cycle = 0;

	public TrafficSimulator( AdasimMap g, VehicleManager m, List<AdasimAgent> c ) {
		if(g == null || c == null || m == null ) {
			throw new IllegalArgumentException();
		}
		this.map = g;
		this.agents = c;
		this.manager = m;
		this.manager.setSimulation(this);
		//inject this into agents
		for ( AdasimAgent a : agents ) {
			a.setSimulation( this );
		}
	}

	/**
	 * Runs the simulator until all agents concur that they are done.
	 * Currently the only way to interrupt this is by forcing the program
	 * to terminate (SIG_INT or suchlike).
	 */
	public void run() {
		while(! isFinished()) {
			takeSimulationStep();
		}
	}

	/**
	 * All agents in the simulator take one step. 
	 * This is for testing only. NEVER call this explicitly!
	 */
	public void takeSimulationStep() {
		logger.info( "SIMULATION: Cycle: " + ++cycle );
		manager.takeSimulationStep(cycle);
		for ( AdasimAgent agent : agents ) {
			agent.takeSimulationStep( cycle );
		}
		for ( RoadSegment g: map.getRoadSegments() ) {
			g.takeSimulationStep(cycle);
		}
	}

	/**
	 * @return true if all vehicles return true on their <code>checkFinish()</code> call
	 */
	boolean isFinished() {
		if ( ! manager.isFinished() ) return false;
		for(AdasimAgent c: agents) {
			if(!c.isFinished()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return an unmodifiable list of all agents known to this instance of {@link TrafficSimulator}. 
	 */
	public List<AdasimAgent> getAgents() {
		return Collections.unmodifiableList(agents);
	}
	
	public AdasimAgent getAgent( int id ) {
		for ( AdasimAgent agt : agents ) {
			if ( agt.getID() == id ) {
				return agt;
			}
		}
		return null;
	}

	/**
	 * Convenience method that returns the vehicle with the given ID
	 * @param id
	 * @return the vehicle with the given ID
	 */
	public Vehicle getVehicle( int id ) {
		for ( Vehicle c : getAgents(Vehicle.class) ) {
			if ( c.getID() == id ) 
				return c;
		}
		return null;
	}

	/**
	 * Add a vehicle to the simulation. It will be added
	 * to its starting node and start moving in the next simulation cycle.
	 * @param v
	 */
	public void addVehicle( Vehicle v ) {
		agents.add(v);
		try {
			((RoadSegment) ReflectionUtils.getProperty( v, "getStartNode")).enterNode(v);
		} catch (Exception e) {
			//this should never happen
			e.printStackTrace();
		} 
	}

	/**
	 * @return the map (map) used by this {@link TrafficSimulator}.
	 * <p>
	 * While this instance is mutable, this is strongly discouraged, as 
	 * effects are unspecified!
	 */
	public AdasimMap getMap() {
		return map;
	}

	/**
	 * Returns a list of all agents that have the specified type.
	 * While this list is mutable, it does not write through to the internal
	 * store of agents, thus modifications to this list have no effect on the
	 * simulator.
	 * 
	 * @param sample
	 * @return all agents that have the specified type
	 */
	@SuppressWarnings("unchecked")
	public <T extends AdasimAgent> List<T> getAgents( Class<T> sample ) {
		List<T> l = new ArrayList<T>();
		for ( AdasimAgent agt : agents ) {
			if (agt.getClass().equals( sample ) ) {
				l.add((T)agt);
			}
		}
		return l;
	}
}
