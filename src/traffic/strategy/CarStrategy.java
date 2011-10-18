/**
 * Jonathan Ramaswamy
 * Car Strategy
 * The strategy interface for specific car strategies to follow
 */
package traffic.strategy;

import java.util.List;

import traffic.graph.Graph;

public interface CarStrategy {
	
	public List<Integer> getPath(Graph g, int c, int d); //Creates the path for the car to follow
	public int redoPath(Graph g, int c, int d); //Re does the path if the current path does not work anymore

}
