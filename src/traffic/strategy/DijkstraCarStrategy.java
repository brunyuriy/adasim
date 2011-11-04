/**
 * Jonathan Ramaswamy
 * Dijkstra Car Strategy
 * A strategy algorithm for cars that uses Dijkstra's algorithm to find
 * the shortest path from the starting node to the destination node
 */
package traffic.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import traffic.graph.Graph;

public class DijkstraCarStrategy implements CarStrategy {

	public DijkstraCarStrategy(){}
	
	/**
	 * Uses Dijkstra's algorithm to find the shortest path from one node to the end
	 * Returns a list corresponding to the path for the car to take
	 */
	public List<Integer> getPath(Graph g, int c, int d) {
		int [] dist = new int [g.getNumNodes()];
		int [] prev = new int [g.getNumNodes()];
		boolean [] visited = new boolean [g.getNumNodes()];
		for (int i=0; i<dist.length; i++) {
			dist[i] = Integer.MAX_VALUE;
			prev[i] = Integer.MAX_VALUE;
		}
		dist[c] = 0;
		for (int i=0; i<dist.length; i++) {
			int next = minVertex(dist, visited);
			visited[next] = true;
			if(next == d) {
				List<Integer> path = new ArrayList<Integer>();
				while(next != Integer.MAX_VALUE) {
					path.add(0, next);
					next = prev[next];
				}
				return path;
			}
			List<Integer> n = g.getNeighbors(next);
		    for (int j=0; j<n.size(); j++) {
		    	int v = n.get(j);
		        int w = dist[next] + g.getLimitAtNode(next);
		        if (dist[v] > w) {
		        	dist[v] = w;
		            prev[v] = next;
		        }
		    }
	   }
	   return null;
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
