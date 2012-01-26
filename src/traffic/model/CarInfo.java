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
 * Created: Oct 25, 2011
 */
package traffic.model;

import traffic.graph.GraphNode;
import traffic.strategy.CarStrategy;
import traffic.strategy.NoiseStrategy;
import traffic.strategy.RandomNoiseStrategy;

/**
 * CarInfo holds all the information for a car object, including its starting, ending,
 * and current positions. This info object can be read by other cars and used in conjunction
 * with a strategy algorithm to make decisions
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 */

public class CarInfo {

	private GraphNode start; //Starting position
	private GraphNode end; //Destination position
	private GraphNode currentNode; //Current position
	private int carNum; //This car's number in the list of cars
	private boolean finish; //True if the car has reached its destination
	private NoiseStrategy noise; //The noise strategy
	private CarStrategy cs; //Strategy the car uses to traverse the graph

	/**
	 * Creates a new CarInfo object with the given parameters
	 * @param start
	 * @param end
	 * @param num
	 * @param strat
	 */
	public CarInfo(GraphNode start, GraphNode end, int num, CarStrategy strat) {
		setStartNode(start);
		setEndNode(end);
		carNum = num;
		finish = false;
		noise = new RandomNoiseStrategy();
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
	public int getCarNum() {
		return carNum;
	}

	/**
	 * @return True if the car is at its ending node, false otherwise
	 */
	public boolean atDestination() {
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
	public CarStrategy getStrategy() {
		return cs;
	}

	/**
	 * @param cs the cs to set
	 */
	public void setStrategy(CarStrategy cs) {
		this.cs = cs;
		if ( cs != null ) {
			cs.setStartNode(start);
			cs.setEndNode(end);
			cs.setCarId(carNum);
		}
	}

	/**
	 * @return
	 */
	public GraphNode getNextNode() {
		return cs.getNextNode();
		
	}

}
