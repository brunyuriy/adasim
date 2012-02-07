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
 * The car object represents a single car on the graph, and holds an info object
 * with important information about itself. The car is given a graph traversal strategy
 * that it will use to select its path from start to end
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 */

import org.apache.log4j.Logger;

import traffic.graph.GraphNode;
import traffic.strategy.VehicleStrategy;
import traffic.strategy.RandomNoiseStrategy;

public class Vehicle {
	
	private GraphNode start; //Starting position
	private GraphNode end; //Destination position
	private GraphNode currentNode; //Current position
	private int id; //This car's number in the list of cars
	private boolean finish; //True if the car has reached its destination
	protected VehicleInfo info; //Info for the car
	private VehicleStrategy cs; //Strategy the car uses to traverse the graph
	
	private static Logger logger = Logger.getLogger(Vehicle.class);
	
	protected Vehicle( int id ) {
		info = new VehicleInfo();
	}
	
	public Vehicle(GraphNode start, GraphNode end, VehicleStrategy strat, int num) {
		setStartNode(start);
		setEndNode(end);
		id = num;
		finish = false;
		setStrategy(strat);
	}
	
	/**
	 * @return The starting node for the car
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
	 * @return The ending node for the car
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
	 * @return The current node of the car
	 */
	public GraphNode getCurrentPosition() {
		return currentNode;
	}

	/**
	 * Sets the current position to the given variable c
	 */
	public void setCurrentPosition(GraphNode c) {
		currentNode = c;
	}

	/**
	 * @return The number of this car
	 */
	public int getID() {
		return id;
	}

	/**
	 * @return True if the car is at its ending node, false otherwise
	 */
	public boolean checkFinish() {
		return finish;
	}

	/**
	 * Sets the finish variable to true if the car's current position
	 * is the same as its ending position
	 */
	public void setFinish() {
		finish = true;
	}

	public boolean isFinished() {
		return finish;
	}

	/**
	 * @return the cs
	 */
	public VehicleStrategy getStrategy() {
		return cs;
	}
	
	public int getCurrentNode() {
		return getCurrentPosition().getID();
	}

	/**
	 * @param cs the cs to set
	 */
	public void setStrategy(VehicleStrategy cs) {
		this.cs = cs;
		if ( cs != null ) {
			cs.setStartNode(start);
			cs.setEndNode(end);
			cs.setCarId(id);
		}
	}

	/**
	 * @return
	 */
	public GraphNode getNextNode() {
		return cs.getNextNode();
		
	}

	/**
	 * Signals to the car that it should move to the next node or park.
	 */
	public void move() {
		GraphNode nextNode = getNextNode();
		if ( nextNode == null ) {
			//this happens if there is no path, or the car is at its goal
			getCurrentPosition().park(this);
			setFinish();
			logger.info( "STOP: " + carPosition() );
		} else {
			logger.info( "MOVE: " + carPosition() + " To:" + nextNode.getID() );
			setCurrentPosition(nextNode);
			nextNode.enterNode(this);
		}
	}
	
	/**
	 * @return the position of the car as a string
	 */
	private String carPosition() {
		StringBuffer buf = new StringBuffer( "Car: ");
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

}
