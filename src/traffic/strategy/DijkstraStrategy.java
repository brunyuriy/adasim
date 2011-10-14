package traffic.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import traffic.graph.Graph;

public class DijkstraStrategy implements CarStrategy{

	public DijkstraStrategy(){}
	
	public List<Integer> getPath(Graph G, int s, int t) {
		int [] dist = new int [G.getNumNodes()];
		int [] prev = new int [G.getNumNodes()];
		boolean [] visited = new boolean [G.getNumNodes()];
		for (int i=0; i<dist.length; i++) {
			dist[i] = Integer.MAX_VALUE;
			prev[i] = Integer.MAX_VALUE;
		}
		dist[s] = 0;
		for (int i=0; i<dist.length; i++) {
			int next = minVertex(dist, visited);
			visited[next] = true;
			if(next == t) {
				List<Integer> path = new ArrayList<Integer>();
				while(next != Integer.MAX_VALUE) {
					path.add(0, next);
					next = prev[next];
				}
				return path;
			}
			List<Integer> n = G.getDestinations(next);
		    for (int j=0; j<n.size(); j++) {
		    	int v = n.get(j);
		        int d = dist[next] + G.getStopAtNode(next);
		        if (dist[v] > d) {
		        	dist[v] = d;
		            prev[v] = next;
		        }
		    }
	   }
	   return null;
	}
	
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

	public int redoPath(Graph g, int current) {
		boolean found = false;
		int n = -1;
		List<Integer> d = g.getDestinations(current);
		while(!found) {
			if(d.size() > 0) {
				Random generator = new Random();
				int rand = generator.nextInt(d.size());
				n = d.get(rand);
				if(g.getCarsAtNode(n) < 2) {
					found = true;
				}
				d.remove(rand);
			} else {
				found = true;
			}
		}
		return n;
	}
	
	

}
