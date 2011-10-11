package traffic.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import traffic.graph.Graph;

public class DijkstraStrategy implements CarStrategy{

	public DijkstraStrategy(){}
	
	public List<Integer> getPath(Graph g, int current, int destination) {
		int[] d = dijkstra(g, current, destination);
		List<Integer> x = new ArrayList<Integer>();
		for(int i: d) {
			x.add(i);
		}
		return x;
		/*int[] dist = new int[g.getNumNodes()];
		int[] prev = new int[g.getNumNodes()];
		List<Integer> graph = new ArrayList<Integer>();
		
		for(int i = 0; i<dist.length; i++) {
			dist[i] = Integer.MAX_VALUE;
			graph.add(i);
		}
		
		dist[current] = 0;
		
		while(!graph.isEmpty()) {
			int u = minVertex(dist, graph);
			if(u == -1) {
				break;
			}
			int r = graph.remove(u);
			if(r == destination) {
				List<Integer> p = new ArrayList<Integer>();
				while(prev[r] > -1) {
					p.add(r);
					r = prev[r];
				}
				return p;
			}
			List<Integer> nb = g.getDestinations(r);
			for(int n: nb) {
				int alt = dist[u] + g.getStopAtNode(n);
				if(alt < dist[n]) {
					dist[n] = alt;
					prev[n] = r;
				}
			}
		}
	    List<Integer> intList = new ArrayList<Integer>();
	    for (int index = 0; index < dist.length; index++)
	    {
	        intList.add(dist[index]);
	    }

		return intList;*/
	}
	
	/*private int minVertex(int[] dist, List<Integer> graph) {
		int x = Integer.MAX_VALUE;
		int y = -1;
		for(int i = 0; i < dist.length; i++) {
			if(graph.contains(i) && dist[i] < x) {
				x = dist[i];
				y = graph.indexOf(i);
			}
		}
		return y;
	}*/
	
	// Dijkstra's algorithm to find shortest path from s to all other nodes
	public static int [] dijkstra (Graph G, int s, int t) {
	   final int [] dist = new int [G.getNumNodes()];  // shortest known distance from "s"
	   final int [] pred = new int [G.getNumNodes()];  // preceeding node in path
	   final boolean [] visited = new boolean [G.getNumNodes()]; // all false initially
	   for (int i=0; i<dist.length; i++) {
	     dist[i] = Integer.MAX_VALUE;
	   }
	   dist[s] = 0;
	   for (int i=0; i<dist.length; i++) {
	     final int next = minVertex (dist, visited);
	     visited[next] = true;
	     final int[] n = G.getNeighbors(next);
	     for (int j=0; j<n.length; j++) {
	         final int v = n[j];
	         final int d = dist[next] + G.getStopAtNode(next);
	         if (dist[v] > d) {
	             dist[v] = d;
	              pred[v] = next;
	          }
	      }
	   }
	   return pred;  // (ignore pred[s]==0!)
	   }
	   private static int minVertex (int [] dist, boolean [] v) {
	       int x = Integer.MAX_VALUE;
	      int y = -1;   // graph not connected, or no unvisited vertices
	       for (int i=0; i<dist.length; i++) {
	          if (!v[i] && dist[i]<x) {y=i; x=dist[i];}
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
