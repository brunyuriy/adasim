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

import traffic.graph.GraphNode;

public class RandomCarStrategy extends AbstractCarStrategy {

	public RandomCarStrategy(){}	
	
	/**
	 * Picks random neighbors to move towards until the destination is reached
	 * Returns the path represented as a list of integers
	 */
	public List<GraphNode> getPath(GraphNode c, GraphNode d) {
		GraphNode next = c;
		List<GraphNode> path = new ArrayList<GraphNode>();
		while(!next.equals(d) && path.size() < graph.getNumNodes() ) {
			List<GraphNode> dest = next.getNeighbors();
			Random generator = new Random();
			int rand = generator.nextInt(dest.size());
			next = dest.get(rand);
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
