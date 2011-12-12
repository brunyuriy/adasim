/**
 * Jonathan Ramaswamy
 * Car Strategy
 * The strategy interface for specific car strategies to follow
 */
package traffic.strategy;

import java.util.List;

import traffic.graph.Graph;
import traffic.graph.GraphNode;

public interface CarStrategy {
	
	public List<Integer> getPath(Graph g, int c, int d); //Creates the path for the car to follow
	
	/**
	 * @return The next node according to the routing strategy. May be <code>null</code> 
	 * if there is no next node.
	 */
	public GraphNode getNextNode();

}
