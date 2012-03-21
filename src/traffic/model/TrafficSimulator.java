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

package traffic.model;

import java.util.*;

import org.apache.log4j.Logger;

import traffic.graph.Graph;

/**
 * TrafficSimulator is the main program for running the simulator. It keeps track
 * of the graph and where all the vehicles are located on it, and outputs information to
 * the logger object with every step taken.
 * <p>
 * To use, create a {@link TrafficSimulator} instance and call <code>run()</code>.
 * No other interaction with the simulation is encourage or needed.
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 * @author Jochen Wuttke - wuttkej@gmail.com
 */

public class TrafficSimulator{

	private static Logger logger = Logger.getLogger(TrafficSimulator.class);


	private List<AdasimAgent> agents; //List of vehicles in the simulation
	private Graph graph; //The graph the vehicles run on
	private long cycle = 1;

	public TrafficSimulator( Graph g, List<AdasimAgent> c ) {
		if(g == null || c == null) {
			throw new IllegalArgumentException();
		}
		this.graph = g;
		this.agents = c;
		addVehiclesToGraph();
	}

	/**
	 * Runs the simulator until all agents concur that they are done.
	 * Currently the only way to interrupt this is by forcing the program
	 * to terminate (SIG_INT or suchlike).
	 */
	public void run() {
		while(! checkAllFinish()) {
			takeSimulationStep();
		}
	}

	private void addVehiclesToGraph() {
		for(AdasimAgent c: getAgents( Vehicle.class ) ) {
			if ( c instanceof Vehicle ) {
				Vehicle v = (Vehicle)c;
				graph.addVehicleAtNode(v, v.getStartNode().getID());
			}
		}
	}

	/**
	 * All agents in the simulator take one step.
	 */
	private void takeSimulationStep() {
		logger.info( "SIMULATION: Cycle: " + cycle++ );
		graph.setClosed();
		for ( AdasimAgent agent : agents ) {
			agent.takeSimulationStep();
		}
	}

	/**
	 * @return true if all vehicles return true on their <code>checkFinish()</code> call
	 */
	private boolean checkAllFinish() {
		for(AdasimAgent c: agents) {
			if(c instanceof Vehicle && !((Vehicle)c).checkFinish()) {
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
	 * @return the graph (map) used by this {@link TrafficSimulator}.
	 * <p>
	 * While this instance is mutable, this is strongly discouraged, as 
	 * effects are unspecified!
	 */
	public Graph getGraph() {
		return graph;
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
