package traffic.graph;
/**
 * Jonathan Ramaswamy
 * Graph
 * Graph is a simple object representing an weighted, directed graph using nodes
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import traffic.model.Car;
import traffic.strategy.SpeedStrategy;

public class Graph {
	
	private Set<GraphNode> nodes; //The nodes within the graph
	
	public Graph( Set<GraphNode> nodes ) {
		this.nodes = nodes;
	}
	
	public Graph( List<GraphNode> nodes ) {
		this.nodes = new HashSet<GraphNode>( nodes );
	}
	
	/**
	 * Adds a new node to the graph and increases the tracker for the number of nodes by one
	 */
	public void addNode(int num, SpeedStrategy speed, int capacity) {
		nodes.add(new GraphNode(num, speed, capacity));
	}
	
	public void addNode( GraphNode node ) {
		nodes.add(node);
	}
	
	/**
	 * Adds an edge between two nodes
	 * Each node keeps track of the other nodes it has an edge to
	 * @param i The incoming node to the edge
	 * @param o The outgoing node from the edge
	 */
	public void addEdge(int i, int o) {
		GraphNode n = get(nodes, i);
		if ( n != null ) {
			GraphNode n2 = get(nodes, o );
			if ( n2 != null ) n.addEdge( n2 );
		}
	}
	
	/**
	 * @param nodes2
	 * @param i
	 * @return
	 */
	private GraphNode get(Set<GraphNode> nodes, int i) {
		for ( GraphNode node : nodes ) {
			if ( node.getID() == i ) return node;
		}
		return null;
	}

	/**
	 * Returns a list of nodes that the given node has outgoing edges towards
	 */
	public List<GraphNode> getNeighbors(int i) {
		GraphNode n = get(nodes, i);
		return ( n == null? null:n.getNeighbors() );
	}
	
	/**
	 * Adds the given car number to the given node
	 * @param c The number of the car to be added
	 * @param n The node to add the car at
	 */
	public void addCarAtNode(Car c, int n) {
		GraphNode gn = get(nodes, n);
		gn.enterNode(c);
	}
	
	/**
	 * Shifts the given car from the old node to the new one
	 * @param c The car that's changing nodes
	 * @param o The old node the car was on
	 * @param n The new node the car is moving to
	 */
	public void changeCarNode(Car c, int oldNode, int targetNode) {
		GraphNode gn = get(nodes,targetNode);
		//GraphNode go = get(nodes,o);
		//go.exitNode(c);
		gn.enterNode(c);
	}
	
	/**
	 * Gets the speed limit at the given node
	 * @param n The node to get the speed limit from
	 * @return The number of turns a car must stay stopped at the given node
	 */
	public int getDelayAtNode(int n) {
		return get(nodes,n).getDelay();
	}
	
	/**
	 * Returns the number of nodes on the graph
	 */
	public int getNumNodes() {
		return nodes.size();
	}
	
	public List<GraphNode> getNodes() {
		return new ArrayList<GraphNode>(nodes);
	}
	
	/**
	 * @param id the ID value of this node
	 * @return
	 */
	public GraphNode getNode( int id ) {
		for ( GraphNode node : nodes ) {
			if ( node.getID() == id ) return node;
		}
		return null;
	}

}
