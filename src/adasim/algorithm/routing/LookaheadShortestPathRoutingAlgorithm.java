/*******************************************************************************
 * Copyright (C) 2011 - 2012 Jochen Wuttke, Jonathan Ramaswamy
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Contributors:
 *    Jochen Wuttke (wuttkej@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Dec 3, 2011
 */
package adasim.algorithm.routing;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import adasim.model.RoadSegment;
import adasim.model.Vehicle;


/**
 * This car strategy is the base implementation for all strategies 
 * using Dijkstras shortest path algorithm on node weights and 
 * adasim delays. 
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class LookaheadShortestPathRoutingAlgorithm extends AbstractRoutingAlgorithm {
	
	private final static Logger logger = Logger.getLogger(LookaheadShortestPathRoutingAlgorithm.class);
	
	private final int lookahead;
	private final int recompute;
	private List<RoadSegment> path;
	private int steps;
	private boolean finished = false;
	
	/**
	 * The default constructor builds this strategy with a lookahead of 0.
	 * <p>
	 * A lookahead of 0 means that the strategy will only consider the 
	 * unmodified weight of each node, and for this reason will never recompute 
	 * the path.
	 */
	public LookaheadShortestPathRoutingAlgorithm() {
		this(0);
	}

	/**
	 * Creates a new strategy object. 
	 * The lookahead parameter defines how far ahead the strategy considers adasim in addition to node
	 * delays. This parameter also defines how often the strategy recomputes the path
	 * it follows. 
	 * <p>
	 * For a lookahead of <em>n</em> it will consider adasim for <em>n</em> nodes from the
	 * current node, and will recompute the path every <em>n</em> moves. 
	 * 
	 * @param lookahead
	 */
	public LookaheadShortestPathRoutingAlgorithm( int lookahead ){
		this(lookahead, lookahead );
	}

	/**
	 * Creates a new strategy object. 
	 * The lookahead parameter defines how far ahead the strategy considers adasim in addition to node
	 * delays. This parameter also defines how often the strategy recomputes the path
	 * it follows. 
	 * <p>
	 * For a lookahead of <em>n</em> it will consider adasim for <em>n</em> nodes from the
	 * current node, and will recompute the path every <em>recomp</em> moves. 
	 * 
	 * @param lookahead
	 */
	public LookaheadShortestPathRoutingAlgorithm( int lookahead, int recomp ){
		this.lookahead = lookahead;
		this.recompute = recomp;
		this.steps = 0;
		logger.info( "LookaheadShortestPathRoutingAlgorithm(" + lookahead + "," + recompute +")" );
	}

	
	public List<RoadSegment> getPath(RoadSegment source, RoadSegment target ) {
		return dijkstra(graph.getRoadSegments(), source, target, lookahead );
	}
	
	/**
	 * Computes Dijkstra's shortest path algorithm on the graph represented by
	 * <code>nodes</code>, and returns a list of nodes that represent
	 * the shortest past from <code>source</code> to <code>target</code>.
	 * @param nodes
	 * @param source
	 * @param target
	 * @param l
	 * @return the shortest past from <code>source</code> to <code>target</code>
	 */
	private List<RoadSegment> dijkstra(List<RoadSegment> nodes, RoadSegment source, RoadSegment target, int l) {
		int size = nodes.size();
		int[] dist = new int[size];
		int[] previous = new int[size];
		Set<Integer> q = new HashSet<Integer>();
		
		init( dist, previous, getIndex(nodes, source) , q );
		while( !q.isEmpty() ) {
			int current = getIndexOfMin(q, dist);
			if ( dist[current] == Integer.MAX_VALUE ) break;
			q.remove( current );
			
			for ( RoadSegment node : nodes.get(current).getNeighbors() ) {
				int depth = getCurrentDepth(previous, nodes, source, nodes.get(current) );
				
				//if we ever make vehicle extensible, then we have to query the class of the configure vehicle
				int t = dist[current] + ( depth <= l ? node.getCurrentDelay(Vehicle.class) : node.getDelay(Vehicle.class) );
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
	 * @param current
	 * @return the current depth of the search path
	 */
	private int getCurrentDepth(int[] previous, List<RoadSegment> nodes,
			RoadSegment source, RoadSegment current) {
		List<RoadSegment> path = reconstructPath(previous, nodes, source, current );
		if ( path == null ) return 1;
		else return path.size() + 1;
	}

	/**
	 * @param previous map to previous nodes on a path 
	 * @param nodes list of all nodes
	 * @param source ID of source node
	 * @param target ID of target node
	 * @return the path constructed from the intermediate data structures passed in
	 */
	private List<RoadSegment> reconstructPath(int[] previous, List<RoadSegment> nodes, RoadSegment source, RoadSegment target) {
		int ti = getIndex(nodes, target);
		if ( previous[ ti ] == -1 ) return null; //no path
		LinkedList<RoadSegment> path = new LinkedList<RoadSegment>();
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
	 * @return the index of the node in the list, -1 if the node cannot be found
	 */
	private int getIndex(List<RoadSegment> nodes, RoadSegment node) {
		for ( int i =0 ; i < nodes.size() ; i++ ) {
			if ( nodes.get(i).equals( node ) ) return i;
		}
			
		return -1;
	}

	/**
	 * Computes the array index of the smallest element
	 * @param q
	 * @return the index of the smalles element
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
	 * @see adasim.algorithm.CarStrategy#getNextNode()
	 */
	@Override
	public RoadSegment getNextNode() {
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
			RoadSegment next = path.remove(0);
			path = getPath(next);
			logger.info( "UPDATE: " + pathLogMessage() );
			steps = 0;
			return next;
		} else {
			return path.remove(0);
		}
	}

	/**
	 * Computes a path to the configured target node starting from
	 * the passed <code>start</code> node.
	 * @param start
	 */
	private List<RoadSegment> getPath(RoadSegment start) {
		List<RoadSegment> p = dijkstra(graph.getRoadSegments(), start, target, lookahead );
		if ( p == null ) {
			finished = true;
		}
		return p;
	}

	private String pathLogMessage() {
		StringBuffer buf = new StringBuffer( "PATH: Vehicle: " );
		buf.append( vehicle.getID() );
		buf.append( " From: " );
		buf.append( source.getID() );
		buf.append( " To: " );
		buf.append( target.getID() );
		buf.append( " Path: " );
		buf.append( path == null ? "[]" : path );
		return buf.toString();
	}
}
