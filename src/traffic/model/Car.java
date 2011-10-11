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
import java.util.Random;

import org.apache.log4j.Logger;

import traffic.graph.Graph;
import traffic.strategy.CarStrategy;
import traffic.strategy.DijkstraStrategy;
import traffic.strategy.RandomStrategy;

public class Car implements Path {
	
	private int start; //Starting position
	private int end; //Destination position
	private int current; //Current position
	private int carNum; //This car's number in the list of cars
	private int stop; //The time the car must wait at its current node
	private boolean finish; //True if the car has reached its destination
	private List<Integer> path; //Path the car travels
	private CarStrategy cs;
	
	private static Logger logger = Logger.getLogger(Car.class);
	
	public Car(String positions, int num) {
		start = Integer.parseInt(positions.substring(0, 2));
		end = Integer.parseInt(positions.substring(3, 5));
		current = start;
		carNum = num;
		stop = -1;
		finish = false;
		path = new ArrayList<Integer>();
		cs = new DijkstraStrategy();
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
			if(stop == 0){
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
				//moveCar(g);
			} else {
				logger.info("Car " + carNum + " must wait at node " + current + " for " + stop + " more turns");
				stop--;
			}
		}
	}
	
	private void moveCar(Graph g) {
		int o = current;
		current = path.get(0);
		g.changeCarNode(carNum, o, current);
		setFinish();
		logger.info("Car " + carNum + " moved to node " + current + " from node " + o);
		stop = -1;
		path.remove(0);
	}
	
	public void makePath(Graph g, int c) {
		path.addAll(cs.getPath(g, c, end));
		logger.info("The path for car " + carNum + " is " + path.toString());
	}
	
	public boolean redoPath(Graph g) {
		path.clear();
		int n = cs.redoPath(g, current);
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
