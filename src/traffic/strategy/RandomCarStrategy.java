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

public class RandomCarStrategy implements CarStrategy {

	public RandomCarStrategy(){}
	
	/**
	 * Picks random neighbors to move towards until the destination is reached
	 * Returns the path represented as a list of integers
	 */
	public List<Integer> getPath(Graph g, int c, int d) {
		int next = c;
		List<Integer> path = new ArrayList<Integer>();
		while(next != d) {
			List<Integer> dest = g.getNeighbors(next);
			Random generator = new Random();
			int rand = generator.nextInt(dest.size());
			next = dest.get(rand);
			path.add(next);
		}
		return path;
	}

	/**
	 * Picks a random neighbor to move to next, as long as it is not over capacity
	 */
	public int redoPath(Graph g, int c, int d) {
		boolean found = false;
		int n = -1;
		List<Integer> dest = g.getNeighbors(c);
		while(!found) {
			if(dest.size() > 0) {
				Random generator = new Random();
				int rand = generator.nextInt(dest.size());
				n = dest.get(rand);
				if(g.getCarsAtNode(n) < 2) {
					found = true;
				}
				dest.remove(rand);
			} else {
				found = true;
			}
		}
		return n;
	}

}
