/*******************************************************************************
 * Copyright (c) 2011 - Jonathan Ramaswamy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jonathan Ramaswamy (ramaswamyj12@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Sep 5, 2011
 */

package traffic.graph;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import traffic.model.Car;
import traffic.strategy.SpeedStrategy;

/**
 * The graph class represents a collection of GraphNodes
 * Edges are contained as neighbors within the nodes
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 */

public class Graph {
	
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
	 * Adds the car to the given node
	 * @param c
	 * @param n
	 */
	public void addCarAtNode(Car c, int n) {
		GraphNode gn = get(nodes, n);
		gn.enterNode(c);
	}
	
	/**
	 * Shifts the given car from the old node to the new one
	 * @param c
	 * @param o
	 * @param n
	 */
	public void changeCarNode(Car c, int oldNode, int targetNode) {
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

}
