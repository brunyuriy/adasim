package traffic.graph;
/**
 * Jonathan Ramaswamy
 * GraphNode
 * GraphNode represents the nodes that make up the graph
 */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import traffic.model.Car;
import traffic.strategy.SpeedStrategy;


public class GraphNode {
	
	private Set<GraphNode> outgoing; //Nodes that this node has an edge directed towards
	private int nodeNum; //The number of this node on the graph
	private List<Car> cars; //The cars at this node
	private SpeedStrategy ss; //The strategy by which the speed changes
	private int delay; //The basic delay of this node. To be modified by the speed strategy
	
	/**
	 * Creates a node with delay 1.
	 * @param n
	 * @param s
	 */
	public GraphNode(int n, SpeedStrategy s) {
		this(n, s, 1);
	}
	
	public GraphNode(int n, SpeedStrategy s, int delay ) {
		nodeNum = n;
		outgoing = new HashSet<GraphNode>();
		cars = new ArrayList<Car>();
		ss = s;
		this.delay = delay;
	}
	
	/* ***************************************************
	 * GRAPH MANAGEMENT METHODS
	 *************************************************** */
		
	public void addEdge( GraphNode to ) {
		if ( to == null ) return;
		outgoing.add( to );
	}
	
	public void removeEdge( GraphNode to ) {
		outgoing.remove( to );
	}
	
	/**
	 * Returns all nodes this node has an edge directed towards
	 */
	public List<GraphNode> getNeighbors() {
		return new ArrayList<GraphNode>(outgoing);
	}
	
	public int getID() {
		return nodeNum;
	}

	/**
	 * @return the ss
	 */
	public SpeedStrategy getSpeedStrategy() {
		return ss;
	}

	/**
	 * @return the cars
	 */
	public List<Car> getCars() {
		return cars;
	}

	/* ***************************************************
	 * TRAFFIC MANAGEMENT METHODS
	 *************************************************** */
	/**
	 * Adds the given car number to the list of cars currently at the node
	 * Changes the speed limit at the node
	 */
	public void enterNode(Car c) {
		cars.add(c);
	}
	
	/**
	 * Removes the given car from the list of cars at this node
	 * Changes the speed limit at the node
	 */
	public void exitNode(Car c) {
		cars.remove(cars.indexOf(c));
	}
	
	/**
	 * Signals to the node that the car will stop at this
	 * node and no longer move. This should normally only
	 * happen when the car is at it's destination or when
	 * it cannot find a path to follow.
	 * 
	 * @param c
	 */
	public void park( Car c ) {
		//TODO:
	}
	
	/**
	 * Returns the number of turns a car must stay limited at this node
	 */
	public int getDelay() {
		return delay;
	}
	
	public int getCurrentDelay() {
		return delay + ss.getDelay( cars.size() );
	}
	
	/**
	 * Returns the number of cars currently at this node
	 */
	public int numCarsAtNode() {
		return cars.size();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return nodeNum;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphNode other = (GraphNode) obj;
		if (nodeNum != other.nodeNum)
			return false;
		return true;
	}
}
