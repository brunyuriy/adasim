/**
 * Jonathan Ramaswamy
 * CarInfo
 * CarInfo holds all the information for a car object, including its starting, ending,
 * and current positions. This info object can be read by other cars and used in conjunction
 * with a strategy algorithm to make decisions
 */
package traffic.model;

import traffic.graph.GraphNode;
import traffic.strategy.CarStrategy;
import traffic.strategy.NoiseStrategy;
import traffic.strategy.RandomNoiseStrategy;

public class CarInfo {
	
	private GraphNode start; //Starting position
	private GraphNode end; //Destination position
	private GraphNode currentNode; //Current position
	private int carNum; //This car's number in the list of cars
	private int delay; //The time the car must wait at its current node
	private boolean finish; //True if the car has reached its destination
	private NoiseStrategy noise; //The noise strategy
	private CarStrategy cs; //Strategy the car uses to traverse the graph

	
	public CarInfo(GraphNode start, GraphNode end, int num, CarStrategy strat) {
		this.start = start;
		this.end = end;
		currentNode = start;
		carNum = num;
		delay = -1;
		finish = false;
		noise = new RandomNoiseStrategy();
		cs = strat;
		cs.setStartNode(start);
		cs.setEndNode(end);
		cs.setCarId(carNum);
	}
	
	/**
	 * @return The starting node for the car
	 */
	public GraphNode getStartNode() {
		return start;
	}
	
	/**
	 * @return The ending node for the car
	 */
	public GraphNode getEndNode() {
		return end;
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
	 * @return The turn delay at this node
	 */
	public int getDelay() {
		return delay;
	}
	
	/**
	 * Sets the turn delay to the given variable d
	 */
	public void setDelay(int d) {
		delay = d;
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
	 * @return
	 */
	public GraphNode getNextNode() {
		return cs.getNextNode();
	}

}
