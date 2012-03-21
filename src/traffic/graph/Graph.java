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

package traffic.graph;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import traffic.model.Vehicle;
import traffic.strategy.SpeedStrategy;

/**
 * The graph class represents a collection of GraphNodes
 * Edges are contained as neighbors within the nodes
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 */

public final class Graph {
	
	private Set<GraphNode> nodes; //The nodes within the graph
	
	/**
	 * Creates a graph comprised of the given set of nodes
	 * @param nodes
	 */
	public Graph( Set<GraphNode> nodes ) {
		this.nodes = nodes;
	}
	
	/**
	 * Creates a graph comprised of the given set of nodes
	 * @param nodes
	 */
	public Graph( List<GraphNode> nodes ) {
		this.nodes = new HashSet<GraphNode>( nodes );
	}
	
	/**
	 * Creates a new node given a number, speed strategy, and capacity
	 * Inserts the new node into the graph
	 * @param num
	 * @param speed
	 * @param capacity
	 */
	public void addNode(int num, SpeedStrategy speed, int capacity) {
		nodes.add(new GraphNode(num, speed, capacity));
	}
	
	/**
	 * Inserts the given node into the graph
	 * @param node
	 */
	public void addNode( GraphNode node ) {
		nodes.add(node);
	}
	
	/**
	 * Adds an edge between two nodes
	 * Nodes themselves hold their edges
	 * @param i
	 * @param o
	 */
	public void addEdge(int i, int o) {
		GraphNode n = get(nodes, i);
		if ( n != null ) {
			GraphNode n2 = get(nodes, o );
			if ( n2 != null ) n.addEdge( n2 );
		}
	}
	
	/**
	 * Gets node i from the given list of nodes
	 * Returns null if the node is not in the set
	 * @param nodes
	 * @param i
	 * @return
	 */
	private GraphNode get(Set<GraphNode> nodes, int i) {
		for ( GraphNode node : nodes ) {
			if ( node.getID() == i ) return node;
		}
		return null;
	}

	/**
	 * Returns the nodes that can be accessed directly from the given node
	 * @param i
	 * @return
	 */
	public List<GraphNode> getNeighbors(int i) {
		GraphNode n = get(nodes, i);
		return ( n == null? null:n.getNeighbors() );
	}
	
	/**
	 * Adds the vehicle to the given node
	 * @param c
	 * @param n
	 */
	public void addVehicleAtNode(Vehicle c, int n) {
		GraphNode gn = get(nodes, n);
		gn.enterNode(c);
	}
	
	/**
	 * Shifts the given vehicle from the old node to the new one
	 * @param c
	 * @param o
	 * @param n
	 */
	public void changeVehicleNode(Vehicle c, int oldNode, int targetNode) {
		GraphNode gn = get(nodes,targetNode);
		//GraphNode go = get(nodes,o);
		//go.exitNode(c);
		gn.enterNode(c);
	}
	
	/**
	 * Returns the delay at the given node
	 * @param n
	 * @return
	 */
	public int getDelayAtNode(int n) {
		return get(nodes,n).getDelay();
	}
	
	/**
	 * Returns the number of nodes in the graph
	 * @return
	 */
	public int getNumNodes() {
		return nodes.size();
	}
	
	/**
	 * Returns the list of nodes in the graph
	 * @return
	 */
	public List<GraphNode> getNodes() {
		return new ArrayList<GraphNode>(nodes);
	}
	
	/**
	 * Returns the graph node with the given ID
	 * @param id
	 * @return
	 */
	public GraphNode getNode( int id ) {
		for ( GraphNode node : nodes ) {
			if ( node.getID() == id ) return node;
		}
		return null;
	}
	
	public void setClosed() {
		for (GraphNode node: nodes) {
			node.setClosed();
		}
	}

}
