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

package adasim.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import adasim.agent.AbstractAdasimAgent;
import adasim.algorithm.delay.TrafficDelayFunction;
import adasim.model.internal.RoadVehicleQueue;
import adasim.util.ReflectionUtils;


/**
 * A RoadSegment is a single node on the graph. It has a queue of vehicles
 * and uses a given speed strategy and delay to determine their movements
 * <p>
 * The RoadSegment applies an uncertainty filter to only some properties, 
 * others it reports faithfully, because precision there is crucial for 
 * the internal working of the simulator. As a convenience for testing, while
 * no filter is configured (i.e. <code>null</code> is assigned
 * to the filter field), it will faithfully report all values.
 * <p>
 * Properties accessible to other agents:
 * <ul>
 * <li>ID: certain
 * <li>delay: uncertain
 * <li>currentDelay: uncertain
 * <li>capacity: uncertain
 * <li>closed: certain
 * <li>numVehiclesAtNode: uncertain
 * </ul>
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 * @author Jochen Wuttke - wuttkej@gmail.com
 */

public final class RoadSegment extends AbstractAdasimAgent implements Comparable<RoadSegment> {
	
	private static Logger logger = Logger.getLogger(RoadSegment.class);

	
	private Set<RoadSegment> outgoing; //Nodes that this node has an edge directed towards
	private TrafficDelayFunction ss; //The strategy by which the speed changes

	//PROPERTIES
	private int delay; //The basic delay of this node. To be modified by the speed strategy
	private RoadVehicleQueue queue; //Holds the vehicles on this node and deals with the adasim
	private int capacity; //The number of vehicles the road can hold before the speed strategy takes effect
	private boolean closed;
	
	/**
	 * Creates a node with delay 1.
	 * @param n
	 * @param s
	 * @param capacity
	 */
	public RoadSegment(int n, TrafficDelayFunction s, int capacity) {
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
	public RoadSegment(int n, TrafficDelayFunction s, int delay, int capacity ) {
		id = n;
		outgoing = new HashSet<RoadSegment>();
		ss = s;
		this.delay = delay;
		queue = new RoadVehicleQueue();
		this.capacity = capacity;
	}
	
	/* ***************************************************
	 * GRAPH MANAGEMENT METHODS
	 *************************************************** */
		
	/**
	 * Adds an outgoing edge to the given node
	 * @param to
	 */
	public static String linksOfNodes ="";
	public void addEdge( RoadSegment to ) {
		if ( to == null ) return;
		if(linksOfNodes=="") {
			linksOfNodes = "{source: \""+this.getID() +"\", target: \""+to.getID() +"\", type: \"view\"}";
				
		}else {
            linksOfNodes = linksOfNodes+",\n{source: \""+this.getID() +"\", target: \""+to.getID() +"\", type: \"view\"}";
		}
		outgoing.add( to );
	}
	
	/**
	 * Removes the given edge from this node's list of outgoing edges
	 * @param to
	 */
	public void removeEdge( RoadSegment to ) {
		outgoing.remove( to );
	}
	
	/**
	 * @return all nodes this node has an outgoing edge towards
	 */
	public List<RoadSegment> getNeighbors() {
		return new ArrayList<RoadSegment>(outgoing);
	}
	
	private boolean isNeighbor(RoadSegment n) {
		return outgoing.contains(n);
	}

	/* ***************************************************
	 * GRAPH NODE INTERNALS
	 *************************************************** */	

	/**
	 * @return this node's speed strategy
	 */
	public TrafficDelayFunction getSpeedStrategy() {
		return ss;
	}

	/**
	 * @param ss the ss to set
	 */
	public void setSpeedStrategy(TrafficDelayFunction ss) {
		this.ss = ss;
	}

	
	/* ***************************************************
	 * AGENT PROPERTIES
	 *************************************************** */	
	
	/**
	 * Returns the number of turns a vehicle must stay limited at this node.
	 * May be uncertain and privacy filtered.
	 */
	public int getDelay( Class<?> caller) {
		return filterValue(delay, caller);
	}
	
	/**
	 * @return the unfiltered delay. For internal use only.
	 */
	@SuppressWarnings("unused")
	private int getDelay() {
		return delay;
	}
	
	/**
	 * @return the adasim dependent delay at this node.
	 */
	public int getCurrentDelay( Class<?> caller ) {		
		int cd = closed ? Integer.MAX_VALUE : ss.getDelay(delay, capacity, queue.size());
		return filterValue(cd, caller);
	}
	
	/**
	 * @return the unfiltered current delay. For internal use only.
	 */
	private int getCurrentDelay() {
		return closed ? Integer.MAX_VALUE : ss.getDelay(delay, capacity, queue.size());
	}
	
	public int getCapacity(Class<?> caller) {
		return filterValue(capacity, caller);
	}
	
	/**
	 * @return the unfiltered capacity. For internal use only.
	 */
	@SuppressWarnings("unused")
	private int getCapacity() {
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
		return uncertaintyFilter.filter(queue.size() );
	}
	
	/**
	 * Sets the <code>closed</code> flag of this node to <code>c</code>.
	 * 
	 * @param c
	 */
	public void setClosed( boolean c ) {
		closed = c;
	}
	
	/**
	 * When a RoadSegment (road) is closed, this means two things:
	 * <ul>
	 * <li>Vehicles can no longer enter the road. Trying to enter is considered invalid and vehicles will be removed.
	 * <li>Vehicles that are already on the road can continue driving and will eventually leave the node.
	 * </ul>
	 * 
	 * @return <code>true</code> if the node is currently closed to adasim
	 */
	public boolean isClosed() {
		return closed;
	}
	
	/* ***************************************************
	 * TRAFFIC MANAGEMENT METHODS
	 *************************************************** */
	/**
	 * Adds the given vehicle number to the list of vehicles currently at the node
	 * if the node is open (i.e. <code>isClosed() == false</code>.
	 * If the node is closed, an attempt to enter is invalid and leads to the 
	 * vehicle being stopped.
	 */
	public void enterNode(Vehicle v) {
		if (closed) {
			logger.info( "INVALID: Node " + this.getID() + " is closed." );
			park(v);
		} else {
			logger.info( "ENTER: " + v.vehiclePosition() );
			queue.enqueue(v, getCurrentDelay() );
			v.setCurrentPosition(this);
		}
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
		logger.info( "STOP: " + c.vehiclePosition() );
		//this is to ensure termination
		try {
			c.setCurrentPosition( (RoadSegment) ReflectionUtils.getProperty(c, "getEndNode"));
		} catch (Exception e) {
			// this should never happen
			e.printStackTrace();
		} 
	}
	
	/* ***************************************************
	 * SIMULATION MANAGEMENT METHODS
	 *************************************************** */

	/**
	 * Handles the vehicle movement protocol.
	 * <p>
	 * In each cycle all cars currently on the node move a distance matching
	 * the currently allowed maximum speed of this node.
	 * If a vehicle reaches the end of the street segment, {@link Vehicle#move()}
	 * is called and the vehicle can propose a new node it wishes to move to
	 * by calling {@link RoadSegment#moveTo(RoadSegment, Vehicle)} on this node. 
	 * This node verifies whether this is legal (the target node must be a 
	 * neighbor in the graph), and if it is legal, hands off the vehicle
	 * by calling {@link RoadSegment#enterNode(Vehicle)} on the target node.
	 * If the move is not legal, the vehicle is stopped and removed 
	 * from the simulation. Corresponding events will be logged.
	 */
	public void takeSimulationStep( long cycle ) {
		Set<Vehicle> finishedVehicles = queue.moveVehicles();

		if ( finishedVehicles == null ) return;
		

		LinkedList<Vehicle> finishedVehiclesSorted = new LinkedList<Vehicle>();
		finishedVehiclesSorted.addAll(finishedVehicles);
		Collections.sort(finishedVehiclesSorted);
		
		
		//For debug only
		for (Vehicle vehicle : finishedVehiclesSorted) {
			// vehicle reach destination
			if(vehicle.isFinished()) {
				continue;
			}
			logger.info( "================> selected " + vehicle.vehiclePosition() +
					", carType:"+ vehicle.getCarType() );
		}
		
		for ( Vehicle c : finishedVehiclesSorted ) {
			c.move();
		}
	}
	
	/**
	 * Called by vehicles during the movement protocol. 
	 * <p>
	 * This method is used to announce a vehicle's intention to enter
	 * node <code>targetNode</code>. This RoadSegment checks the move for 
	 * validity and rejects illegal moves.
	 * 
	 * @param targetNode
	 * @param v
	 */
	public void moveTo( RoadSegment targetNode, Vehicle v ) {
		if ( isNeighbor(targetNode) ) {
			targetNode.enterNode(v);
		} else {
			logger.info( "INVALID: Move: " + v.vehiclePosition() + " To: " + targetNode.getID() );
			park(v);
		}
	}

	/* ***************************************************
	 * OBJECT METHODS
	 *************************************************** */

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return id;
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
		RoadSegment other = (RoadSegment) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "" + id;
	}
	
	/* ***************************************************
	 * STATIC UTILITY METHODS
	 *************************************************** */
	
	/**
	 * @param roads a list of road segments
	 * @param id the ID to search for
	 * @return the RoadSegment with the ID, or <code>null</code> if 
	 * none can be found.
	 */
	public static RoadSegment getRoadSegment(Collection<RoadSegment> roads, int id) {
		for ( RoadSegment n : roads ) {
			if ( n.getID() == id ) return n;
		}
		return null;
	}

	@Override
	public int compareTo(RoadSegment o) {
		// TODO Auto-generated method stub
		return this.getID() - o.getID();
	}

}
