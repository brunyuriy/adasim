/*******************************************************************************
 * Copyright (c) 2011 - Jonathan Ramaswamy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

import traffic.model.Car;
import traffic.strategy.SpeedStrategy;

/**
 * A GraphNode is a single node on the graph. It has a queue of cars
 * and uses a given speed strategy and delay to determine their movements
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 */

public class GraphNode {
	
	private Set<GraphNode> outgoing; //Nodes that this node has an edge directed towards
	private int nodeNum; //The number of this node on the graph
	private SpeedStrategy ss; //The strategy by which the speed changes
	private int delay; //The basic delay of this node. To be modified by the speed strategy
	private NodeVehicleQueue queue; //Holds the cars on this node and deals with the traffic
	private int capacity; //The number of cars the road can hold before the speed strategy takes effect
	
	/**
	 * Creates a new node with a default delay
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

	/* ***************************************************
	 * TRAFFIC MANAGEMENT METHODS
	 *************************************************** */
	/**
	 * Adds the given car number to the list of cars currently at the node
	 * Changes the speed limit at the node
	 */
	public void enterNode(Car c) {
		queue.enqueue(c, getCurrentDelay() );
	}
	
	/**
	 * Signals to the node that the car will stop at this
	 * node and no longer move. This should normally only
	 * happen when the car is at it's destination or when
	 * it cannot find a path to follow.
	 * 
	 * @param c
	 */
	public void park( Car c ) {
		queue.park(c);
	}
	
	/**
	 * Returns the number of turns a car must stay limited at this node
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
	 * Returns the number of cars currently at this node
	 */
	public int numCarsAtNode() {
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
		Set<Car> finishedCars = queue.moveCars();
		if ( finishedCars == null ) return;
		for ( Car c : finishedCars ) {
			c.move();
		}
	}
}
