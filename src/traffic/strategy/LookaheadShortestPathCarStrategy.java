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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
	
	public List<Integer> getPath(Graph g, int source, int target ) {
		return dijkstra(g, g.getNode(source), g.getNode(target), lookahead );
	}
	
	/**
	 * @param g
	 * @param source ID of the source node
	 * @param target ID of the target node
	 * @param l
	 */
	private List<Integer> dijkstra(Graph g, GraphNode source, GraphNode target, int l) {
		int size = g.getNodes().size();
		int[] dist = new int[size];
		int[] previous = new int[size];
		Set<Integer> q = new HashSet<Integer>();
		
		init( dist, previous, getIndex(g.getNodes(), source) , q );
		while( !q.isEmpty() ) {
			int current = getIndexOfMin(q, dist);
			if ( dist[current] == Integer.MAX_VALUE ) break;
			q.remove( current );
			
			for ( GraphNode node : g.getNodes().get(current).getNeighbors() ) {
				int depth = lookahead; //reconstructPath(previous, source, g.getNodes().get(current).getID() ).size();
				int t = dist[current] + ( depth < lookahead ? node.getCurrentDelay() : node.getDelay() );
				int thisIndex = getIndex( g.getNodes() , node );
				if ( t < dist[ thisIndex ] ) {
					dist[thisIndex] = t;
					previous[thisIndex] = current;
				}
			}
		}
		return reconstructPath( previous, g.getNodes(), source, target );
	}

	/**
	 * @param previous
	 * @param ID of source node
	 * @param ID of target node
	 * @return
	 */
	private List<Integer> reconstructPath(int[] previous, List<GraphNode> nodes, GraphNode source, GraphNode target) {
		int ti = getIndex(nodes, target);
		if ( previous[ ti ] == -1 ) return null; //no path
		LinkedList<Integer> path = new LinkedList<Integer>();
		int current = ti;
		do {
			path.push( nodes.get(current).getID() );
			current = previous[current];
		} while ( current != getIndex(nodes, source) && previous[current] != -1 );
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
