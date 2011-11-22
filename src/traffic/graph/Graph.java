package traffic.graph;
/**
 * Jonathan Ramaswamy
 * Graph
 * Graph is a simple object representing an weighted, directed graph using nodes
 */

import java.util.ArrayList;
import java.util.List;

public class Graph {
	
	private List<GraphNode> nodes; //The nodes within the graph
	private int numNodes; //The number of nodes in the graph
	
	public Graph(List<Integer> node, List<String> strategies, 
			     List<Integer> startNodes, List<Integer> endNodes) {
		nodes = new ArrayList<GraphNode>();
		numNodes = 0;
		for(int i = 0; i < node.size(); i++) {
			addNode(node.get(i), strategies.get(i)); //Creates the nodes in the graph
		}
		for(int j = 0; j < startNodes.size(); j++) {
			addEdge(startNodes.get(j), endNodes.get(j)); //Adds edges to the given nodes
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
	 * Returns a list of nodes that the given node has outgoing edges towards
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
	public int getDelayAtNode(int n) {
		return nodes.get(n).getDelay();
	}
	
	/**
	 * Returns the number of nodes on the graph
	 */
	public int getNumNodes() {
		return numNodes;
	}
	
	public List<GraphNode> getNodes() {
		return nodes;
	}

}
