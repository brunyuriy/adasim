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
 * Created: Sep 15, 2011
 */

package adasim.model;


import org.apache.log4j.Logger;

import adasim.agent.AbstractAdasimAgent;
import adasim.algorithm.routing.RoutingAlgorithm;


/**
 * 
 * The vehicle object represents a single vehicle on the graph.
 * <p>
 * Vehicles are part of the core classes of Adasim and receive
 * special treatment up to a point. While they also implement the 
 * AdasimAgent interface, the takeSimulationStep() method is empty.
 * <p>
 * Special treatment arises because both TrafficSimulator and 
 * RoadSegment know about Vehicles and implement protocols for communicating 
 * with them. The specifics of these protocols are documented in the 
 * corresponding classes.
 * <p>
 * User implemented agents and strategies should never <strong>set</strong>
 * any properties of vehicles (with the possible exception of updating strategies).
 * Users also must never call protocol methods such as takeSimulationStep() 
 * and move(). Their internals and use are restricted to the core simulator. 
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 * @author Jochen Wuttke - wuttkej@gmail.com
 */
public final class Vehicle extends AbstractAdasimAgent implements Comparable<Vehicle> {

	private RoadSegment start; //Starting position
	private RoadSegment end; //Destination position
	private RoadSegment currentNode; //Current position
	private RoutingAlgorithm cs; //Strategy the vehicle uses to traverse the graph
	private int carType;

	private static Logger logger = Logger.getLogger(Vehicle.class);

	protected Vehicle( int id ) {
	}

	public Vehicle(RoadSegment start, RoadSegment end, RoutingAlgorithm strat, int num) {
		setStartNode(start);
		setEndNode(end);
		id = num;
		setStrategy(strat);
	}
	
	

	public void setCarType(int carType) {
		 this.carType =  carType;
	}
	
	public int getCarType( ) {
		return this.carType  ;
	}
	
	/**
	 * 
	 * @param caller
	 * @return The (possibly) filtered start node of the car
	 */
	public RoadSegment getStartNode( Class<?> caller ) {
		return filterValue(start, caller);
	}
	
	/**
	 * @return The unfiltered starting node for the vehicle
	 */
	@SuppressWarnings("unused")
	private RoadSegment getStartNode() {
		return start;
	}

	/**
	 * Setting the start node also resets the current node of the vehicle.
	 * This is intended only for initializing the car and should
	 * not be called more than once.
	 * 
	 * @param start the start to set
	 */
	public void setStartNode(RoadSegment start) {
		this.start = start;
		this.currentNode = start;
		//reset the strategy
		setStrategy(cs);
	}
	
	/**
	 * @param caller
	 * @return the (possibly) filtered target not of the vehicle
	 */
	public RoadSegment getEndNode( Class<?> caller ) {
		return filterValue(end, caller);
	}


	/**
	 * @return The ending node for the vehicle
	 */
	@SuppressWarnings("unused")
	private RoadSegment getEndNode() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEndNode(RoadSegment end) {
		this.end = end;
		//reset the strategy
		setStrategy(cs);
	}
	
	/**
	 * @param caller
	 * @return the (possibly) filtered current node of the vehicle
	 */
	public RoadSegment getCurrentPosition( Class<?> caller ) {
		return filterValue(end, caller);
	}

	/**
	 * @return The current node of the vehicle
	 */
	private RoadSegment getCurrentPosition() {
		return currentNode;
	}

	/**
	 * Sets the current position to the given variable c
	 */
	public void setCurrentPosition(RoadSegment c) {
		currentNode = c;
		if ( isFinished() ) {
			logger.info( "STOP: " + vehiclePosition() );
		}
	}

	/**
	 * @return the cs
	 */
	public RoutingAlgorithm getStrategy() {
		return cs;
	}

	/**
	 * @param cs the cs to set
	 */
	public void setStrategy(RoutingAlgorithm cs) {
		this.cs = cs;
		if ( cs != null ) {
			cs.setStartRoad(start);
			cs.setEndRoad(end);
			cs.setVehicle(this);
		}
	}

	/**
	 * @return the position of the car as a string
	 */
	public String vehiclePosition() {
		StringBuffer buf = new StringBuffer( "Vehicle: ");
		buf.append( getID() );
		buf.append(" At: " );
		buf.append( getCurrentPosition().getID() );
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see adasim.model.AdasimAgent#takeSimulationStep()
	 */
	@Override
	public void takeSimulationStep( long cycle ) {
	}
	
	/**
	 * Called by GraphNodes during the vehicle movement protocol.
	 * The Vehicle responds by calling moveTo() on its
	 * currentNode.
	 */
	public void move() {
		if (isFinished()) return;	//quick end if we are done
		
		RoadSegment nextNode = cs.getNextNode();
		if ( nextNode == null ) {
			getCurrentPosition().park(this);
		} else {
			logger.info( "MOVE: " + vehiclePosition() + " To: " + nextNode.getID() );
			currentNode.moveTo(nextNode, this);
		}		
	}

	/**
	 * A vehicle is finished when it either has reached its
	 * target node, or when the strategy can no longer compute
	 * a path to the target.
	 */
	public boolean isFinished() {
		return currentNode != null && currentNode.equals(end);
	}

	@Override
	public int compareTo(Vehicle o) {
		// TODO Auto-generated method stub
		return  o.carType - this.carType  ; // compare two cares if police move to first
	}

}
