package traffic.model;
/**
 * Jonathan Ramaswamy
 * Car Version 4
 * The car object represents a single car on the graph, and knows its starting
 * position, current position, and end position. The car will move in a random direction
 * towards its destination until it is reached.
 */
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import traffic.graph.Graph;
import traffic.strategy.CarStrategy;
import traffic.strategy.DijkstraCarStrategy;
import traffic.strategy.RandomCarStrategy;

public class Car implements Path {
	
	private int start; //Starting position
	private int end; //Destination position
	private int current; //Current position
	private int carNum; //This car's number in the list of cars
	private int limit; //The time the car must wait at its current node
	private boolean finish; //True if the car has reached its destination
	private List<Integer> path; //Path the car travels
	private CarStrategy cs; //Strategy the car uses to traverse the graph
	
	private static Logger logger = Logger.getLogger(Car.class);
	
	public Car(String positions, int num) {
		start = Integer.parseInt(positions.substring(0, 2));
		end = Integer.parseInt(positions.substring(3, 5));
		current = start;
		carNum = num;
		limit = -1;
		finish = false;
		path = new ArrayList<Integer>();
		if(positions.substring(6, 7).equals("D")) {
			cs = new DijkstraCarStrategy();
		} else {
			cs = new RandomCarStrategy();
		}
	}
	
	/**
	 * If the car is not at its destination, tries to move the car to a new random node
	 * Checks to see if the car has waited at the node long enough before moving it
	 * If the car has waited long enough but has no nodes with spaces nearby,
	 * it stays at the current node until a node with space is found
	 */
	public void tryMove(Graph g) {
		if(!finish) {
			if(limit == -1) { //Speed limit hasn't been found yet
				limit = g.getLimitAtNode(current);
			}
			if(limit == 0) { //Car is free to move if path is clear
				if(g.getCarsAtNode(path.get(0)) > 1) {
					if(!redoPath(g)) {
						logger.info("All neighboring nodes are full, car " + carNum + "must wait another turn to move");
					} else {
						logger.info("The path for car " + carNum + " has been changed");
						moveCar(g);
					}
				} else {
					moveCar(g);
				}
			} else { //Car must wait a certain number of turns to move again
				logger.info("Car " + carNum + " must wait at node " + current + " for " + limit + " more turns");
				limit--;
			}
		}
	}
	
	//Moves the car to the next node on its path
	private void moveCar(Graph g) {
		int o = current;
		current = path.get(0);
		g.changeCarNode(carNum, o, current);
		setFinish();
		logger.info("Car " + carNum + " moved to node " + current + " from node " + o);
		limit = -1;
		path.remove(0);
	}
	
	/**
	 * Uses the given strategy for the node to create a path
	 * The car will attempt to follow this path to its destination
	 * until an obstacle prevents it from doing so
	 */
	public void makePath(Graph g, int c) {
		path.addAll(cs.getPath(g, c, end));
		logger.info("The path for car " + carNum + " is " + path.toString());
	}
	
	/**
	 * Re-does the current path due to an obstacle in the car's way
	 * Returns true if a suitable new path could be found, false otherwise
	 */
	public boolean redoPath(Graph g) {
		path.clear();
		int n = cs.redoPath(g, current, end);
		if(n != -1) {
			path.add(n);
			makePath(g, n);
			return true;
		} else {
			return false;
		}
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
