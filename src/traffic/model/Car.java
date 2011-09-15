package traffic.model;
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

import traffic.graph.Graph;

public class Car {
	
	private int start; //Starting position
	private int end; //Destination position
	private int current; //Current position
	private int carNum; //This car's number in the list of cars
	private int stop; //The time the car must wait at its current node
	private boolean finish; //True if the car has reached its destination
	
	private static Logger logger = Logger.getLogger(Car.class);
	
	public Car(String positions, int num) {
		start = Integer.parseInt(positions.substring(0, 2));
		end = Integer.parseInt(positions.substring(3, 5));
		current = start;
		carNum = num;
		stop = -1;
		finish = false;
	}
	
	/**
	 * If the car is not at its destination, tries to move the car to a new random node
	 * Checks to see if the car has waited at the node long enough before moving it
	 * If the car has waited long enough but has no nodes with spaces nearby,
	 * it stays at the current node until a node with space is found
	 */
	public void tryMove(Graph g) {
		if(!finish) {
			if(stop == -1) {
				stop = g.getStopAtNode(current);
			}
			if(stop == 0) {
				int n = getNewNode(g);
				if(n == -1) {
					logger.info("All neighboring nodes are full, car " + carNum + "must wait another turn to move");
				} else {
					int o = current;
					current = n;
					g.changeCarNode(carNum, o, n);
					setFinish();
					logger.info("Car " + carNum + " moved to node " + current + " from node " + o);
					stop = -1;
				}
			} else {
				logger.info("Car " + carNum + " must wait at node " + current + " for " + stop + "more turns");
				stop--;
			}
		}
	}
	
	//Gets a node for the car to move to. If all neighboring nodes are full, returns -1
	private int getNewNode(Graph g) {
		boolean found = false;
		int n = -1;
		List<Integer> d = g.getDestinations(current);
		while(!found) {
			Random generator = new Random();
			int rand = generator.nextInt(d.size());
			n = d.get(rand);
			if(g.getCarsAtNode(n) < 2) {
				found = true;
			}
			d.remove(rand);
		}
		return n;
	}
	
	/**
	 * Returns the car's current position
	 */
	public int getCurrent() {
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
}
