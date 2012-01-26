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
import traffic.strategy.CarStrategy;

public class Car {
	
	protected CarInfo info; //Info for the car
	
	private static Logger logger = Logger.getLogger(Car.class);
	
	protected Car( int id ) {
		info = new CarInfo(null, null, id, null );
	}
	
	public Car(GraphNode start, GraphNode end, CarStrategy strat, int num) {
		info = new CarInfo(start, end, num, strat );
	}

	/**
	 * Signals to the car that it should move to the next node or park.
	 */
	public void move() {
		GraphNode nextNode = info.getNextNode();
		if ( nextNode == null ) {
			//this happens if there is no path, or the car is at its goal
			info.getCurrentPosition().park(this);
			info.setFinish();
			logger.info( "STOP: " + carPosition() );
		} else {
			logger.info( "MOVE: " + carPosition() + " To:" + nextNode.getID() );
			info.setCurrentPosition(nextNode);
			nextNode.enterNode(this);
		}
	}
	
	/**
	 * @return the position of the car as a string
	 */
	private String carPosition() {
		StringBuffer buf = new StringBuffer( "Car: ");
		buf.append( this.getID() );
		buf.append(" At: " );
		buf.append( info.getCurrentPosition().getID() );
		return buf.toString();
	}
	
	/**
	 * @return the car's current position
	 */
	public int getCurrent() {
		return info.getCurrentPosition().getID();
	}
	
	/**
	 * @return the car's number
	 */
	public int getID() {
		return info.getCarNum();
	}
	
	/**
	 * @return true if the car is at the end, false if not
	 */
	public boolean checkFinish() {
		return info.isFinished();
	}

	/**
	 * @return the info
	 */
	public CarInfo getInfo() {
		return info;
	}

}
