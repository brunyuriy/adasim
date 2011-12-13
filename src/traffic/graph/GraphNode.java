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

import traffic.strategy.SpeedStrategy;


public class GraphNode {
	
	private Set<GraphNode> outgoing; //Nodes that this node has an edge directed towards
	private int nodeNum; //The number of this node on the graph
	private List<Integer> cars; //The cars at this node
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
		cars = new ArrayList<Integer>();
		ss = s;
		this.delay = delay;
	}
		
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
	
	/**
	 * Adds the given car number to the list of cars currently at the node
	 * Changes the speed limit at the node
	 */
	public void addCar(int i) {
		cars.add(i);
	}
	
	/**
	 * Removes the given car from the list of cars at this node
	 * Changes the speed limit at the node
	 */
	public void removeCar(int i) {
		cars.remove(cars.indexOf(i));
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

	public int getID() {
		return nodeNum;
	}

	/**
	 * @return the ss
	 */
	public SpeedStrategy getSpeedStrategy() {
		return ss;
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

	/**
	 * @return the cars
	 */
	public List<Integer> getCars() {
		return cars;
	}
}
