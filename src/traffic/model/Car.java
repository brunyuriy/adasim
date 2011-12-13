package traffic.model;
/**
 * Jonathan Ramaswamy
 * Car
 * The car object represents a single car on the graph, and holds an info object
 * with important information about itself. The car is given a graph traversal strategy
 * that it will use to select its path from start to end
 * 
 */
import java.util.List;

import org.apache.log4j.Logger;

import traffic.graph.Graph;
import traffic.strategy.CarStrategy;

public class Car {
	
	private CarInfo info; //Info for the car
	
	private static Logger logger = Logger.getLogger(Car.class);
	
	public Car(int start, int end, CarStrategy strat, int num) throws InstantiationException, IllegalAccessException {
		info = new CarInfo(start, end, num, strat );
	}

	/**
	 * If the car is not at its destination, tries to move the car to a new random node
	 * Checks to see if the car has waited at the node long enough before moving it
	 * If the car has waited long enough but has no nodes with spaces nearby,
	 * it stays at the current node until a node with space is found
	 */
	public void tryMove(Graph g) {
		if(info.getPath() != null) {
			if(!info.atDestination()) {
				if(info.getDelay() == -1) { //Speed limit hasn't been found yet
					info.setDelay(g.getDelayAtNode(info.getCurrentPosition()));
				}
				if(info.getDelay() == 0) { //Car is free to move if path is clear
					moveCar(g);
				} else { //Car must wait a certain number of turns to move again
					logger.info("Car " + info.getCarNum() + " must wait at node " + info.getCurrentPosition() + " for " + info.getDelay() + " more turns");
					info.setDelay(info.getDelay()-1);
				}
			}
		}
	}
	
	//Moves the car to the next node on its path
	private void moveCar(Graph g) {
		int o = info.getCurrentPosition();
		info.setCurrentPosition(info.getPath().get(0));
		g.changeCarNode(info.getCarNum(), o, info.getCurrentPosition());
		setFinish();
		logger.info("Car " + info.getCarNum() + " moved to node " + info.getCurrentPosition() + " from node " + o);
		info.setDelay(-1);
		List<Integer> temp = info.getPath();
		temp.remove(0);
		if ( temp.size() == 0 ) { 
			temp = null;
		}
		info.setPath(temp);
	}
		
	/**
	 * Returns the car's current position
	 */
	public int getCurrent() {
		return info.getCurrentPosition();
	}
	
	/**
	 * Returns the car's number
	 */
	public int getCarNumber() {
		return info.getCarNum();
	}
	
	/**
	 * Checks to see if the current position equals the final position,
	 * and sets finish to be true if so
	 */
	private void setFinish() {
		info.setFinish();
	}
	
	/**
	 * Returns true if the car is at the end, false if not
	 */
	public boolean checkFinish() {
		return info.atDestination() || info.getPath() == null ;
	}

	/**
	 * @return the info
	 */
	public CarInfo getInfo() {
		return info;
	}

}
