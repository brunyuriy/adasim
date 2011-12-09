/*******************************************************************************
 * Copyright (c) 2011 - Jochen Wuttke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jochen Wuttke (wuttkej@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Dec 3, 2011
 */
package traffic.strategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import traffic.graph.Graph;
import traffic.graph.GraphNode;

/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class LookaheadShortestPathCarStrategy implements CarStrategy {
	
	private final int lookahead;
	
	public LookaheadShortestPathCarStrategy() {
		this(0);
	}

	public LookaheadShortestPathCarStrategy( int lookahead ){
		this.lookahead = lookahead;
	}
	
	/**
	 * Uses Dijkstra's algorithm to find the shortest path from one node to the end
	 * Returns a list corresponding to the path for the car to take
	 */
	/*
	 * BFS doesn't work, because of node/edge weights. Dijkstra is pretty
	 * much BFS with weights. 
	 * To implement Dijkstra correctly and easily we need to map the nodes to 
	 * indeces in an array and then we can relatively easily use the standard
	 * algorithm with path reconstruction.
	 * The map should be easy based on a list or something.
	 */
	public List<Integer> getPath(Graph g, int source, int target ) {

		return dijkstra(g, source, target, lookahead );
	}
	
	/**
	 * @param g
	 * @param source
	 * @param target
	 * @param l
	 */
	private List<Integer> dijkstra(Graph g, int source, int target, int l) {
		int size = g.getNodes().size();
		int[] dist = new int[size];
		int[] previous = new int[size];
		Set<Integer> q = new HashSet<Integer>();
		
		init( dist, previous, source, q );
		while( !q.isEmpty() ) {
			int current = getMin(q, dist);
		}
		
		
		return null;
	}

	/**
	 * Computes the array index of the smallest element
	 * @param q
	 * @return
	 */
	private int getMin(Set<Integer> q, int[] dist) {
		int min = 0;
		for ( int i : q ) {
			if ( min > dist[i] ) min = i;
		}
		return min;
	}

	/**
	 * @param dist
	 * @param previous
	 * @param source
	 */
	private void init(int[] dist, int[] previous, int source, Set<Integer> q) {
		for ( int i = 0; i < dist.length; i++ ) {
			if ( i == source ) {
				dist[i] = 0;
			} else {
				dist[i] = Integer.MAX_VALUE;
			}
			previous[i] = -1;
			q.add(i);
		}
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
