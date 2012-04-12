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
 *    Jonathan Ramaswamy (ramaswamyj12@gmail.com) - initial API and implementation
 *    Jochen Wuttke (wuttkej@gmail.com) - extended API 
 ********************************************************************************
 *
 * Created: Sep 5, 2011
 */

package traffic.model;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import traffic.algorithm.delay.TrafficDelayFunction;

/**
 * The graph class represents a collection of GraphNodes
 * Edges are contained as neighbors within the nodes
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 */

public final class AdasimMap {
	
	private Set<RoadSegment> roads; //The nodes within the graph
	
	/**
	 * Creates a graph comprised of the given set of nodes
	 * @param nodes
	 */
	public AdasimMap( Set<RoadSegment> nodes ) {
		this.roads = nodes;
	}
	
	/**
	 * Creates a graph comprised of the given set of nodes
	 * @param nodes
	 */
	public AdasimMap( List<RoadSegment> nodes ) {
		this.roads = new HashSet<RoadSegment>( nodes );
	}
	
	/**
	 * Creates a new node given a number, speed strategy, and capacity
	 * Inserts the new node into the graph
	 * @param num
	 * @param speed
	 * @param capacity
	 */
	public void addRoadSegment(int num, TrafficDelayFunction speed, int capacity) {
		roads.add(new RoadSegment(num, speed, capacity));
	}
	
	/**
	 * Inserts the given node into the graph
	 * @param node
	 */
	public void addRoadSegment( RoadSegment node ) {
		roads.add(node);
	}
	
	/**
	 * Adds an edge between two nodes
	 * Nodes themselves hold their edges
	 * @param i
	 * @param o
	 */
	public void addEdge(int i, int o) {
		RoadSegment n = getRoadSegment( i);
		if ( n != null ) {
			RoadSegment n2 = getRoadSegment( o );
			if ( n2 != null ) n.addEdge( n2 );
		}
	}
	
	/**
	 * Adds the vehicle to the given node
	 * @param c
	 * @param n
	 */
	public void addVehicleAtSegment(Vehicle c, int n) {
		RoadSegment gn = getRoadSegment(n);
		gn.enterNode(c);
	}
	
	/**
	 * @return the list of nodes in the graph
	 */
	public List<RoadSegment> getRoadSegments() {
		return new ArrayList<RoadSegment>(roads);
	}
	
	/**
	 * Returns the graph node with the given ID
	 * @param id
	 * @return the node with ID <code>id</code> or <code>null</code> if
	 * no such node exists 
	 */
	public RoadSegment getRoadSegment( int id ) {
		for ( RoadSegment node : roads ) {
			if ( node.getID() == id ) return node;
		}
		return null;
	}
	
}
