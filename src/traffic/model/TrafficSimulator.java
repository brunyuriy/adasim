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
import traffic.graph.GraphNode;

/**
 * TrafficSimulator is the main program for running the simulator. It keeps track
 * of the graph and where all the vehicles are located on it, and outputs information to
 * the logger object with every step taken.
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
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

	//Uses the previously specified algorithm to create paths for each vehicle on the graph
	private void addVehiclesToGraph() {
		for(AdasimAgent c: getAgents( Vehicle.class ) ) {
			if ( c instanceof Vehicle ) {
				Vehicle v = (Vehicle)c;
				graph.addVehicleAtNode(v, v.getStartNode().getID());
			}
		}
	}

	/**
	 * Runs the simulation by trying to move each vehicle one at a time
	 * @return True if the simulation is over
	 */
	public boolean takeSimulationStep() {
		logger.info( "SIMULATION: Cycle: " + cycle++ );
		for ( GraphNode node: graph.getNodes() ) {
			node.takeSimulationStep();
		}		
		for ( AdasimAgent agent : agents ) {
			agent.takeSimulationStep();
		}
		return checkAllFinish();
	}

	//Checks to see if all vehicles have finished moving, returns true if so
	private boolean checkAllFinish() {
		for(AdasimAgent c: agents) {
			if(c instanceof Vehicle && !((Vehicle)c).checkFinish()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return the vehicles
	 */
	public List<AdasimAgent> getAgents() {
		return agents;
	}

	/**
	 * Returns the vehicle with the given ID
	 * @param id
	 * @return
	 * @deprecated
	 */
	public Vehicle getVehicle( int id ) {
//		//for ( Vehicle c : agents ) {
//			if ( c.getID() == id ) 
//				return c;
//		}
		return null;
	}

	/**
	 * @return the graph
	 */
	public Graph getGraph() {
		return graph;
	}
	
	/**
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
