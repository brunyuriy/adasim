package traffic.graph;
/**
 * Jonathan Ramaswamy
 * Graph is a simple object representing an weighted, directed graph using nodes
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Graph {
	
	private List<GraphNode> nodes; //The nodes within the graph
	private int numNodes; //The number of nodes in the graph
	
	public Graph(List<Integer> node, List<String> strats, 
			     List<Integer> startNodes, List<Integer> endNodes) {
		nodes = new ArrayList<GraphNode>();
		numNodes = 0;
		for(int i = 0; i < node.size(); i++) {
			addNode(node.get(i), strats.get(i));
		}
		for(int j = 0; j < startNodes.size(); j++) {
			addEdge(startNodes.get(j), endNodes.get(j));
		}
	}
	
	/**
	 * Adds a new node to the graph and increases the tracker for the number of nodes by one
	 */
	public void addNode(int num, String speed) {
		nodes.add(new GraphNode(num, speed));
		numNodes++;
	}
	
	/**
	 * Quick method to make an entire node at once with cars and edges
	 * Used mainly for testing purposes
	 * @param n The node number to add the cars and edges at
	 * @param c The list of cars
	 * @param o The list of edges
	 */
	public void makeNode(int n, int[] c, int[] o) {
		nodes.get(n).makeNode(c, o);
	}
	
	/**
	 * Adds an edge between two nodes
	 * Each node keeps track of the other nodes it has an edge to
	 * @param i The incoming node to the edge
	 * @param o The outgoing node from the edge
	 */
	public void addEdge(int i, int o) {
		GraphNode n = nodes.get(i);
		n.addEdge(o);
	}
	
	/**
	 * Returns a list of nodes by number that the given node is connected to
	 */
	public List<Integer> getNeighbors(int i) {
		GraphNode n = nodes.get(i);
		return n.getNeighbors();
	}
	
	/**
	 * Adds the given car number to the given node
	 * @param c The number of the car to be added
	 * @param n The node to add the car at
	 */
	public void addCarAtNode(int c, int n) {
		GraphNode gn = nodes.get(n);
		gn.addCar(c);
	}
	
	/**
	 * Shifts the given car from the old node to the new one
	 * @param c The car that's changing nodes
	 * @param o The old node the car was on
	 * @param n The new node the car is moving to
	 */
	public void changeCarNode(int c, int o, int n) {
		GraphNode gn = nodes.get(n);
		GraphNode go = nodes.get(o);
		go.removeCar(c);
		gn.addCar(c);
	}
	
	/**
	 * Gets the speed limit at the given node
	 * @param n The node to get the speed limit from
	 * @return The number of turns a car must stay stopped at the given node
	 */
	public int getLimitAtNode(int n) {
		return nodes.get(n).getLimit();
	}
	
	/**
	 * Returns the number of cars at a given node
	 */
	public int getCarsAtNode(int n) {
		return nodes.get(n).numCarsAtNode();
	}
	
	/**
	 * Returns the number of nodes on the graph
	 */
	public int getNumNodes() {
		return numNodes;
	}

}
