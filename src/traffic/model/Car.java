package traffic.model;
/**
 * Jonathan Ramaswamy
 * Car
 * The car object represents a single car on the graph, and holds an info object
 * with important information about itself. The car is given a graph traversal strategy
 * that it will use to select its path from start to end
 * 
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
