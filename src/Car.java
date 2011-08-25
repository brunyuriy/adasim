/**
 * Jonathan Ramaswamy
 * Car Version 4
 * The car object represents a single car on the graph, and knows its starting
 * position, current position, and end position. The car will move in a random direction
 * towards its destination until it is reached.
 */
import java.util.Random;

import org.apache.log4j.Logger;

public class Car {
	
	private int startx; //Starting x position
	private int starty; //Starting y position
	private int endx; //Destination x position
	private int endy; //Destination y position
	private int currentx; //Current x position
	private int currenty; //Current y position
	private int carNum; //This car's number in the list of cars
	private int carConflict; //The number of a conflicting car
	private int speed; //The car's speed limit
	private int moves; //The number of moves left until the car can move a spot
	private boolean finish; //True if the car has reached its destination
	
	private static Logger logger = Logger.getLogger(Car.class);
	
	public Car(String positions, int num) {
		startx = Integer.parseInt(positions.substring(0, 2));
		starty = Integer.parseInt(positions.substring(3, 5));
		endx = Integer.parseInt(positions.substring(6, 8));
		endy = Integer.parseInt(positions.substring(9, 11));
		currentx = startx;
		currenty = starty;
		carNum = num;
		carConflict = -1;
		speed = Integer.parseInt(positions.substring(12, 14));
		moves = speed;
		finish = false;
	}
	
	/**
	 * Checks to see how many moves are left before the car can move
	 * If 0, the car takes one step towards the destination
	 */
	public void tryMove() {
		if(!finish) {
			logger.info("Trying to move car " + carNum);
			if(moves == 1) {
				moveOneStep();
				moves = speed;
			} else {
				moves--;
				logger.info("Car " + carNum + " must wait " + moves + " more steps");
			}
		}
	}
	
	//Moves the car one step in a random direction towards the destination
	private void moveOneStep() {
		if(!finish) {
			if(currentx != endx && currenty != endy) {
				Random generator = new Random();
				int rand = generator.nextInt(2);
				if(rand == 0) {
					if(endx > startx) {
						currentx++;
					} else {
						currentx--;
					}
				} else {
					if(endy > starty) {
						currenty++;
					} else {
						currenty--;
					}
				}
			} else if (currentx == endx) {
				if(endy > starty) {
					currenty++;
				} else {
					currenty--;
				}
			} else if (currenty == endy) {
				if(endx > startx) {
					currentx++;
				} else {
					currentx--;
				}
			}
			setFinish();
			logger.info("Car " + carNum + " moves one step to (" + currentx + ", " + currenty + ")");
		}
	}
	
	/**
	 * Returns the car's current x position
	 */
	public int getCurrentX() {
		return currentx;
	}
	
	/**
	 * Returns the car's current y position
	 */
	public int getCurrentY() {
		return currenty;
	}
	
	/**
	 * Returns the car's number
	 */
	public int getCarNumber() {
		return carNum;
	}
	
	/**
	 * Sets a conflict with the given car number
	 * @param carN The car that this car shares a space with
	 */
	public void setConflict(int carN) {
		carConflict = carN;
	}
	
	/**
	 * Returns a car that conflicts with the current car, or -1 if none do
	 */
	public int getConflict() {
		return carConflict;
	}
	
	/**
	 * Checks to see if the current position equals the final position,
	 * and sets finish to be true if so
	 */
	private void setFinish() {
		finish = currentx == endx && currenty == endy;
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
