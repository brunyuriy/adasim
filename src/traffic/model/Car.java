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
 * Created: Jul 29, 2011
 */

package traffic.model;

import org.apache.log4j.Logger;

import traffic.graph.GraphNode;
import traffic.strategy.CarStrategy;

/**
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 *
 */

public class Car {
	
	private CarInfo info; //Info for the car
	
	private static Logger logger = Logger.getLogger(Car.class);
	
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
	
	private String carPosition() {
		StringBuffer buf = new StringBuffer( "Car: ");
		buf.append( this.getID() );
		buf.append(" At: " );
		buf.append( info.getCurrentPosition().getID() );
		return buf.toString();
	}
	
	/**
	 * Returns the car's current position
	 */
	public int getCurrent() {
		return info.getCurrentPosition().getID();
	}
	
	/**
	 * Returns the car's number
	 */
	public int getID() {
		return info.getCarNum();
	}
	
	/**
	 * Returns true if the car is at the end, false if not
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
