/**
 * Jonathan Ramaswamy
 * Random Car Strategy
 * Picks a random path for the car to follow towards its destination
 * Makes no optimizations
 */
package traffic.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import traffic.graph.Graph;
import traffic.graph.GraphNode;

public class RandomCarStrategy implements CarStrategy {

	public RandomCarStrategy(){}
	
	/**
	 * Picks random neighbors to move towards until the destination is reached
	 * Returns the path represented as a list of integers
	 */
	public List<Integer> getPath(Graph g, int c, int d) {
		int next = c;
		List<Integer> path = new ArrayList<Integer>();
		while(next != d && path.size() < g.getNumNodes() ) {
			List<GraphNode> dest = g.getNeighbors(next);
			Random generator = new Random();
			int rand = generator.nextInt(dest.size());
			next = dest.get(rand).getID();
			path.add(next);
		}
		return path.get( path.size() - 1 ) == d ? path : null ;	//The random strategy must terminate even it if can't find a path
	}

	/* (non-Javadoc)
	 * @see traffic.strategy.CarStrategy#getNextNode()
	 */
	@Override
	public GraphNode getNextNode() {
		// TODO Auto-generated method stub
		return null;
	}

}
