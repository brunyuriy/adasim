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
 * Created: Sep 5, 2011
 */

package traffic.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import traffic.model.AdasimAgent;
import traffic.model.Vehicle;
import traffic.strategy.SpeedStrategy;

/**
 * A GraphNode is a single node on the graph. It has a queue of vehicles
 * and uses a given speed strategy and delay to determine their movements
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 */

public class GraphNode implements AdasimAgent {
	
	private Set<GraphNode> outgoing; //Nodes that this node has an edge directed towards
	private int nodeNum; //The number of this node on the graph
	private SpeedStrategy ss; //The strategy by which the speed changes
	private int delay; //The basic delay of this node. To be modified by the speed strategy
	private NodeVehicleQueue queue; //Holds the vehicles on this node and deals with the traffic
	private int capacity; //The number of vehicles the road can hold before the speed strategy takes effect
	
	/**
	 * Creates a node with delay 1.
	 * @param n
	 * @param s
	 * @param capacity
	 */
	public GraphNode(int n, SpeedStrategy s, int capacity) {
		this(n, s, 1, capacity);
	}
	
	/**
	 * Creates a new node with the given node number, speed strategy,
	 * delay and capacity arguments
	 * @param n
	 * @param s
	 * @param delay
	 * @param capacity
	 */
	public GraphNode(int n, SpeedStrategy s, int delay, int capacity ) {
		nodeNum = n;
		outgoing = new HashSet<GraphNode>();
		ss = s;
		this.delay = delay;
		queue = new NodeVehicleQueue();
		this.capacity = capacity;
	}
	
	/* ***************************************************
	 * GRAPH MANAGEMENT METHODS
	 *************************************************** */
		
	/**
	 * Adds an outgoing edge to the given node
	 * @param to
	 */
	public void addEdge( GraphNode to ) {
		if ( to == null ) return;
		outgoing.add( to );
	}
	
	/**
	 * Removes the given edge from this node's list of outgoing edges
	 * @param to
	 */
	public void removeEdge( GraphNode to ) {
		outgoing.remove( to );
	}
	
	/**
	 * Returns all nodes this node has an outgoing edge towards
	 * @return
	 */
	public List<GraphNode> getNeighbors() {
		return new ArrayList<GraphNode>(outgoing);
	}
	/**
	 * Returns this node's ID number
	 * @return
	 */
	public int getID() {
		return nodeNum;
	}

	/**
	 * Returns this node's speed strategy
	 * @return
	 */
	public SpeedStrategy getSpeedStrategy() {
		return ss;
	}

	/**
	 * @param ss the ss to set
	 */
	public void setSpeedStrategy(SpeedStrategy ss) {
		this.ss = ss;
	}

	/* ***************************************************
	 * TRAFFIC MANAGEMENT METHODS
	 *************************************************** */
	/**
	 * Adds the given vehicle number to the list of vehicles currently at the node
	 * Changes the speed limit at the node
	 */
	public void enterNode(Vehicle c) {
		queue.enqueue(c, getCurrentDelay() );
	}
	
	/**
	 * Signals to the node that the vehicle will stop at this
	 * node and no longer move. This should normally only
	 * happen when the vehicle is at it's destination or when
	 * it cannot find a path to follow.
	 * 
	 * @param c
	 */
	public void park( Vehicle c ) {
		queue.park(c);
	}
	
	/**
	 * Returns the number of turns a vehicle must stay limited at this node
	 */
	public int getDelay() {
		return delay;
	}
	
	public int getCurrentDelay() {
		return ss.getDelay(delay, capacity, queue.size());
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	/**
	 * @param capacity the capacity to set
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	/**
	 * Returns the number of vehicles currently at this node
	 */
	public int numVehiclesAtNode() {
		return queue.size();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return nodeNum;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphNode other = (GraphNode) obj;
		if (nodeNum != other.nodeNum)
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "" + nodeNum;
	}

	/* ***************************************************
	 * SIMULATION MANAGEMENT METHODS
	 *************************************************** */

	public void takeSimulationStep() {
		Set<Vehicle> finishedVehicles = queue.moveVehicles();
		if ( finishedVehicles == null ) return;
		for ( Vehicle c : finishedVehicles ) {
			c.takeSimulationStep();
		}
	}

	/* (non-Javadoc)
	 * @see traffic.model.AdasimAgent#setParameters(java.lang.String)
	 */
	@Override
	public void setParameters(String params) {
		// TODO Auto-generated method stub
		
	}
}
