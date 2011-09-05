/**
 * Jonathan Ramaswamy
 * GraphNode represents the nodes that make up the graph
 */
import java.util.ArrayList;
import java.util.List;


public class GraphNode {
	
	public List<Integer> outgoing; //Nodes that this node has an edge directed towards
	public int nodeNum; //The number of this node on the graph
	
	public GraphNode(int n) {
		nodeNum = n;
		outgoing = new ArrayList<Integer>();
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
		return outgoing;
	}

}
