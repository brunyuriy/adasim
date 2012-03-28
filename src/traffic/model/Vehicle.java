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

package traffic.model;
/**
 * 
 * The vehicle object represents a single vehicle on the graph, and holds an info object
 * with important information about itself. The vehicle is given a graph traversal strategy
 * that it will use to select its path from start to end
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 * @author Jochen Wuttke - wuttkej@gmail.com
 */

import org.apache.log4j.Logger;

import traffic.graph.GraphNode;
import traffic.strategy.VehicleStrategy;

public class Vehicle extends AbstractAdasimAgent {

	private GraphNode start; //Starting position
	private GraphNode end; //Destination position
	private GraphNode currentNode; //Current position
	private int id; //This vehicle's number in the list of vehicles
	protected VehicleInfo info; //Info for the vehicle
	private VehicleStrategy cs; //Strategy the vehicle uses to traverse the graph

	private static Logger logger = Logger.getLogger(Vehicle.class);

	protected Vehicle( int id ) {
		info = new VehicleInfo();
	}

	public Vehicle(GraphNode start, GraphNode end, VehicleStrategy strat, int num) {
		setStartNode(start);
		setEndNode(end);
		id = num;
		setStrategy(strat);
	}

	/**
	 * @return The starting node for the vehicle
	 */
	public GraphNode getStartNode() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStartNode(GraphNode start) {
		this.start = start;
		this.currentNode = start;
		//reset the strategy
		setStrategy(cs);
	}

	/**
	 * @return The ending node for the vehicle
	 */
	public GraphNode getEndNode() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEndNode(GraphNode end) {
		this.end = end;
		//reset the strategy
		setStrategy(cs);
	}

	/**
	 * @return The current node of the vehicle
	 */
	public GraphNode getCurrentPosition() {
		return currentNode;
	}

	/**
	 * Sets the current position to the given variable c
	 */
	void setCurrentPosition(GraphNode c) {
		currentNode = c;
	}

	/**
	 * @return The number of this vehicle
	 */
	public int getID() {
		return id;
	}

	/**
	 * @return the cs
	 */
	public VehicleStrategy getStrategy() {
		return cs;
	}

	/**
	 * @param cs the cs to set
	 */
	public void setStrategy(VehicleStrategy cs) {
		this.cs = cs;
		if ( cs != null ) {
			cs.setStartNode(start);
			cs.setEndNode(end);
			cs.setVehicleId(id);
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


	/**
	 * @return the info
	 */
	public VehicleInfo getInfo() {
		return info;
	}

	/* (non-Javadoc)
	 * @see traffic.model.AdasimAgent#takeSimulationStep()
	 */
	@Override
	public void takeSimulationStep( long cycle ) {
		if (isFinished()) return;	//quick end if we are done

		GraphNode nextNode = cs.getNextNode();
		if ( nextNode == null ) {
			fakeFinish();
			logger.info( "STOP: " + vehiclePosition() );
		} else {
			logger.info( "MOVE: " + vehiclePosition() + " To:" + nextNode.getID() );
			//TODO: technically, the nodes should set the vehicles position
			setCurrentPosition(nextNode);
			nextNode.enterNode(this);
		}
	}

	/**
	 * 
	 */
	private void fakeFinish() {
		//TODO: replace with correct code in GraphNode
		getCurrentPosition().park(this);
		setCurrentPosition(end);
	}

	/* (non-Javadoc)
	 * @see traffic.model.AbstractAdasimAgent#isFinished()
	 */
	@Override
	public boolean isFinished() {
		return currentNode != null && currentNode.equals(end);
	}

}
