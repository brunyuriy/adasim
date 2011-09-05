/**
 * Jonathan Ramaswamy
 * Graph is a simple object representing an unweighted, directed graph using nodes
 */
import java.util.ArrayList;
import java.util.List;

public class Graph {
	
	public List<GraphNode> nodes; //The nodes within the graph
	public int numNodes; //The number of nodes in the graph
	
	public Graph() {
		nodes = new ArrayList<GraphNode>();
		numNodes = 0;
	}
	
	/**
	 * Adds a new node to the graph and increases the tracker for the number of nodes by one
	 */
	public void addNode() {
		nodes.add(new GraphNode(numNodes));
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
	 * Returns a list of nodes by number that the given node is connected to
	 */
	public List<Integer> getDestinations(int i) {
		GraphNode n = nodes.get(i);
		return n.getNeighbors();
	}

}
