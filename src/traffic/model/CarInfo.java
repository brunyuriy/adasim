/**
 * Jonathan Ramaswamy
 * CarInfo
 * CarInfo holds all the information for a car object, including its starting, ending,
 * and current positions. This info object can be read by other cars and used in conjunction
 * with a strategy algorithm to make decisions
 */
package traffic.model;

import java.util.ArrayList;
import java.util.List;

import traffic.strategy.NoiseStrategy;
import traffic.strategy.RandomNoiseStrategy;

public class CarInfo {
	
	private int start; //Starting position
	private int end; //Destination position
	private int current; //Current position
	private int carNum; //This car's number in the list of cars
	private int delay; //The time the car must wait at its current node
	private boolean finish; //True if the car has reached its destination
	private List<Integer> path; //Path the car travels
	private NoiseStrategy noise; //The noise strategy
	
	public CarInfo(int start, int end, int num) {
		this.start = start;
		this.end = end;
		current = start;
		carNum = num;
		delay = -1;
		finish = false;
		path = null;
		noise = new RandomNoiseStrategy();
	}
	
	/**
	 * @return The starting node for the car
	 */
	public int getStartNode() {
		return start;
	}
	
	/**
	 * @return The ending node for the car
	 */
	public int getEndNode() {
		return end;
	}
	
	/**
	 * @return The current node of the car
	 */
	public int getCurrentPosition() {
		return current;
	}
	
	/**
	 * Sets the current position to the given variable c
	 */
	public void setCurrentPosition(int c) {
		current = c;
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
		finish = current == end;
	}
	
	/**
	 * @return The path the car will take from start to finish
	 */
	public List<Integer> getPath() {
		return path;
	}
	
	/**
	 * Sets the path for the car to the given list p
	 */
	public void setPath(List<Integer> p) {
		path = p;
	}

}
