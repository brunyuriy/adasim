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

import org.apache.log4j.Logger;

import traffic.graph.GraphNode;

/**
 * This vehicle strategy is the base implementation for all strategies 
 * using Dijkstras shortest path algorithm on node weights and 
 * traffic delays. 
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class LookaheadShortestPathVehicleStrategy extends AbstractVehicleStrategy {
	
	private final static Logger logger = Logger.getLogger(LookaheadShortestPathVehicleStrategy.class);
	
	private final int lookahead;
	private final int recompute;
	private List<GraphNode> path;
	private int steps;
	private boolean finished = false;
	
	/**
	 * The default constructor builds this strategy with a lookahead of 0.
	 * <p>
	 * A lookahead of 0 means that the strategy will only consider the 
	 * unmodified weight of each node, and for this reason will never recompute 
	 * the path.
	 */
	public LookaheadShortestPathVehicleStrategy() {
		this(0);
	}

	/**
	 * Creates a new strategy object. 
	 * The lookahead parameter defines how far ahead the strategy considers traffic in addition to node
	 * delays. This parameter also defines how often the strategy recomputes the path
	 * it follows. 
	 * <p>
	 * For a lookahead of <em>n</em> it will consider traffic for <em>n</em> nodes from the
	 * current node, and will recompute the path every <em>n</em> moves. 
	 * 
	 * @param lookahead
	 */
	public LookaheadShortestPathVehicleStrategy( int lookahead ){
		this(lookahead, lookahead );
	}

	/**
	 * Creates a new strategy object. 
	 * The lookahead parameter defines how far ahead the strategy considers traffic in addition to node
	 * delays. This parameter also defines how often the strategy recomputes the path
	 * it follows. 
	 * <p>
	 * For a lookahead of <em>n</em> it will consider traffic for <em>n</em> nodes from the
	 * current node, and will recompute the path every <em>recomp</em> moves. 
	 * 
	 * @param lookahead
	 */
	public LookaheadShortestPathVehicleStrategy( int lookahead, int recomp ){
		this.lookahead = lookahead;
		this.recompute = recomp;
		this.steps = 0;
	}

	
	public List<GraphNode> getPath(GraphNode source, GraphNode target ) {
		return dijkstra(graph.getNodes(), source, target, lookahead );
	}
	
	/**
	 * @param g
	 * @param source ID of the source node
	 * @param target ID of the target node
	 * @param l
	 */
	private List<GraphNode> dijkstra(List<GraphNode> nodes, GraphNode source, GraphNode target, int l) {
		int size = nodes.size();
		int[] dist = new int[size];
		int[] previous = new int[size];
		Set<Integer> q = new HashSet<Integer>();
		
		init( dist, previous, getIndex(nodes, source) , q );
		while( !q.isEmpty() ) {
			int current = getIndexOfMin(q, dist);
			if ( dist[current] == Integer.MAX_VALUE ) break;
			q.remove( current );
			
			for ( GraphNode node : nodes.get(current).getNeighbors() ) {
				int depth = getCurrentDepth(previous, nodes, source, nodes.get(current) ); 
				int t = dist[current] + ( depth <= lookahead ? node.getCurrentDelay() : node.getDelay() );
				int thisIndex = getIndex( nodes, node );
				if ( t < dist[ thisIndex ] ) {
					dist[thisIndex] = t;
					previous[thisIndex] = current;
				}
			}
		}
		return reconstructPath( previous, nodes, source, target );
	}

	/**
	 * @param previous
	 * @param nodes
	 * @param source
	 * @param graphNode
	 * @return
	 */
	private int getCurrentDepth(int[] previous, List<GraphNode> nodes,
			GraphNode source, GraphNode current) {
		List<GraphNode> path = reconstructPath(previous, nodes, source, current );
		if ( path == null ) return 1;
		else return path.size() + 1;
	}

	/**
	 * @param previous
	 * @param ID of source node
	 * @param ID of target node
	 * @return
	 */
	private List<GraphNode> reconstructPath(int[] previous, List<GraphNode> nodes, GraphNode source, GraphNode target) {
		int ti = getIndex(nodes, target);
		if ( previous[ ti ] == -1 ) return null; //no path
		LinkedList<GraphNode> path = new LinkedList<GraphNode>();
		int current = ti;
		do {
			path.push( nodes.get(current) );
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

	/* (non-Javadoc)
	 * @see traffic.strategy.VehicleStrategy#getNextNode()
	 */
	@Override
	public GraphNode getNextNode() {
		if ( finished ) return null;
		if ( path == null ) {
			path = getPath(source);
			logger.info( pathLogMessage() );
		}
		assert path != null || finished;
		if ( path == null || path.size() == 0 ) {
			finished = true;
			return null;
		}
		if ( ++steps == recompute ) {
			GraphNode next = path.remove(0);
			path = getPath(next);
			logger.info( "UPDATE: " + pathLogMessage() );
			steps = 0;
			return next;
		} else {
			return path.remove(0);
		}
	}

	/**
	 * @param next
	 */
	private List<GraphNode> getPath(GraphNode start) {
		List<GraphNode> p = dijkstra(graph.getNodes(), start, target, lookahead );
		if ( p == null ) {
			finished = true;
		}
		return p;
	}

	private String pathLogMessage() {
		StringBuffer buf = new StringBuffer( "PATH: Vehicle: " );
		buf.append( vehicleID );
		buf.append( " From: " );
		buf.append( source.getID() );
		buf.append( " To: " );
		buf.append( target.getID() );
		buf.append( " Path: " );
		buf.append( path == null ? "[]" : path );
		return buf.toString();
	}
}
