/**
 * Jonathan Ramaswamy
 * Car Version 4
 * The car object represents a single car on the graph, and knows its starting
 * position, current position, and end position. The car will move in a random direction
 * towards its destination until it is reached.
 */
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

public class Car {
	
	private int start; //Starting position
	private int end; //Destination position
	private int current; //Current position
	private int carNum; //This car's number in the list of cars
	private int speed; //The car's speed limit
	private int moves; //The number of moves left until the car can move a spot
	private boolean finish; //True if the car has reached its destination
	
	private static Logger logger = Logger.getLogger(Car.class);
	
	public Car(String positions, int num) {
		start = Integer.parseInt(positions.substring(0, 2));
		end = Integer.parseInt(positions.substring(3, 5));
		current = start;
		carNum = num;
		speed = Integer.parseInt(positions.substring(6, 8));
		moves = speed;
		finish = false;
	}
	
	/**
	 * Checks to see how many moves are left before the car can move
	 * If 0, the car takes one step
	 */
	public void tryMove(Graph g) {
		if(!finish) {
			logger.info("Trying to move car " + carNum);
			if(moves == 1) {
				moveOneStep(g);
				moves = speed;
			} else {
				moves--;
				logger.info("Car " + carNum + " must wait " + moves + " more steps");
			}
		}
	}
	
	//Moves the car one step in a random direction on the graph
	private void moveOneStep(Graph g) {
		if(!finish) {
			Random generator = new Random();
			List<Integer> d = g.getDestinations(current);
			int rand = generator.nextInt(d.size());
			int n = d.get(rand);
			current = n;
			setFinish();
			logger.info("Car " + carNum + " moved to node " + current);
		}
	}
	
	/**
	 * Returns the car's current position
	 */
	public int getCurrentX() {
		return current;
	}
	
	/**
	 * Returns the car's number
	 */
	public int getCarNumber() {
		return carNum;
	}
	
	/**
	 * Checks to see if the current position equals the final position,
	 * and sets finish to be true if so
	 */
	private void setFinish() {
		finish = current == end;
	}
	
	/**
	 * Returns true if the car is at the end, false if not
	 */
	public boolean checkFinish() {
		return finish;
	}
	
	/**
	 * Returns the car's set speed limit
	 */
	public int getSpeed() {
		return speed;
	}
	
	/**
	 * Returns the number of moves left until the car can move
	 */
	public int getMoves() {
		return moves;
	}
}
