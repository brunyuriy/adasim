/**
 * Jonathan Ramaswamy
 * Dijkstra Car Strategy
 * A strategy algorithm for cars that uses Dijkstra's algorithm to find
 * the shortest path from the starting node to the destination node
 */
package traffic.strategy;

import java.util.ArrayList;
import java.util.List;

import traffic.graph.Graph;
import traffic.graph.GraphNode;

public class LookaheadDijkstraCarStrategy implements CarStrategy {
	
	private final int lookahead;

	public LookaheadDijkstraCarStrategy( int lookahead ){
		this.lookahead = lookahead;
	}
	
	/**
	 * Uses Dijkstra's algorithm to find the shortest path from one node to the end
	 * Returns a list corresponding to the path for the car to take
	 */
	public List<Integer> getPath(Graph g, int currentNode, int destNode ) {
		int [] dist = new int [g.getNumNodes()];
		int [] prev = new int [g.getNumNodes()];
		List<Integer>[] paths = new List[g.getNumNodes()];
		boolean [] visited = new boolean [g.getNumNodes()];
		for (int i=0; i<dist.length; i++) {
			dist[i] = Integer.MAX_VALUE;
			paths[i] = null;
			prev[i] = Integer.MAX_VALUE;
		}
		dist[currentNode] = 0;
		paths[currentNode] = new ArrayList<Integer>();
		for (int i=0; i<dist.length; i++) {
			int next = minVertex(dist, visited);
			if ( next == -1 ) continue;	//we have an unreachable target
			visited[next] = true;
			for ( int neighbor : g.getNeighbors(next) ) {
				if ( visited[neighbor] ) continue;
				int nDist = getDelay( g, neighbor );
				int tempDist = dist[next] + nDist;
				if ( tempDist < dist[neighbor] ) {
					dist[neighbor] = tempDist;
					prev[neighbor] = next;
					paths[neighbor] = extendPath(paths[next], neighbor );
				}
			}
	   }
	   return paths[destNode];
	}
	
	/**
	 * @param list
	 * @param neighbor
	 * @return
	 */
	private List<Integer> extendPath(List<Integer> list, int neighbor) {
		List<Integer> copy = new ArrayList<Integer>(list);
		copy.add(neighbor);
		return copy;
	}

	/**
	 * @param g
	 * @param neighbor
	 * @return
	 */
	private int getDelay(Graph g, int neighbor) {
		GraphNode n;
		for ( GraphNode gn : g.getNodes() ) {
			if ( gn.getID() == neighbor ) {
				//return gn.getDelay();
				return 1;
			}
		}
		return Integer.MAX_VALUE;
	}

	//Finds the closest vertex on the graph from the current one
	private int minVertex (int [] dist, boolean [] v) {
		int x = Integer.MAX_VALUE;
		int y = -1;
	    for (int i=0; i<dist.length; i++) {
	    	if (!v[i] && dist[i]<x) {
	    		y=i;
	        	x=dist[i];
	        }
	    }
	    return y;
	}	

}
