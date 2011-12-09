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
			int current = getIndexOfMin(q, dist);
			if ( dist[current] == Integer.MAX_VALUE ) break;
			q.remove( current );
			
			for ( GraphNode node : g.getNodes().get(current).getNeighbors() ) {
				int t = dist[current] + node.getDelay();	//TODO: modify this for lookahead values
				int thisIndex = getIndex( g.getNodes() , node );
				if ( t < dist[ thisIndex ] ) {
					dist[thisIndex] = t;
					previous[thisIndex] = current;
				}
			}
		}
		return reconstructPath( previous, source, target );
	}

	/**
	 * @param previous
	 * @param source
	 * @param target
	 * @return
	 */
	private List<Integer> reconstructPath(int[] previous, int source, int target) {
		if ( previous[target] == -1 ) return null; //no path
		LinkedList<Integer> path = new LinkedList<Integer>();
		int current = target;
		do {
			path.push( current );
			current = previous[current];
		} while ( current != source && previous[current] != -1 );
		return path;
	}

	/**
	 * @param nodes
	 * @param node
	 * @return
	 */
	private int getIndex(List<GraphNode> nodes, GraphNode node) {
		for ( int i =0 ; i < nodes.size() ; i++ ) {
			if ( nodes.get(i).equals( node ) ) return i;
		}
			
		return -1;
	}

	/**
	 * Computes the array index of the smallest element
	 * @param q
	 * @return
	 */
	private int getIndexOfMin(Set<Integer> q, int[] dist) {
		int min = q.iterator().next();
		for ( int i : q ) {
			if ( dist[min] > dist[i] ) min = i;
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

}
