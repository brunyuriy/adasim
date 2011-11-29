package traffic.graph;
/**
 * Jonathan Ramaswamy
 * GraphNode
 * GraphNode represents the nodes that make up the graph
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import traffic.strategy.LinearSpeedStrategy;
import traffic.strategy.QuadraticSpeedStrategy;
import traffic.strategy.RandomSpeedStrategy;
import traffic.strategy.SpeedStrategy;


public class GraphNode {
	
	private List<Integer> outgoing; //Nodes that this node has an edge directed towards
	private int nodeNum; //The number of this node on the graph
	private List<Integer> cars; //The cars at this node
	private SpeedStrategy ss; //The strategy by which the speed changes
	private int delay; //The time a car must wait at this node
	
	public GraphNode(int n, SpeedStrategy s) {
		nodeNum = n;
		outgoing = new ArrayList<Integer>();
		cars = new ArrayList<Integer>();
		ss = s;
		delay = ss.getDelay(cars.size());
	}
	
	/**
	 * Adds an edge from this node to the given node
	 * @param i
	 */
	public void addEdge(int i) {
		outgoing.add(i);
	}
	
	/**
	 * Returns all nodes this node has an edge directed towards
	 */
	public List<Integer> getNeighbors() {
		return Collections.unmodifiableList(outgoing);
	}
	
	/**
	 * Adds the given car number to the list of cars currently at the node
	 * Changes the speed limit at the node
	 */
	public void addCar(int i) {
		cars.add(i);
		delay = ss.getDelay(cars.size());
	}
	
	/**
	 * Removes the given car from the list of cars at this node
	 * Changes the speed limit at the node
	 */
	public void removeCar(int i) {
		cars.remove(cars.indexOf(i));
		delay = ss.getDelay(cars.size());
	}
	
	/**
	 * Returns the number of turns a car must stay limited at this node
	 */
	public int getDelay() {
		return delay;
	}
	
	/**
	 * Returns the number of cars currently at this node
	 */
	public int numCarsAtNode() {
		return cars.size();
	}

}
