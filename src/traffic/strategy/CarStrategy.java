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
 * Created: Oct 11, 2011
 */

package traffic.strategy;

import java.util.List;

import traffic.graph.Graph;
import traffic.graph.GraphNode;

/**
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 *
 */

public interface CarStrategy {
	
	/**
	 * This is intended for testing only, as it circumvents the control
	 * that can be implemented in getNextNode()
	 * @param from
	 * @param to
	 * @return
	 */
	public List<GraphNode> getPath(GraphNode from, GraphNode to); //Creates the path for the car to follow

	/**
	 * @return The next node according to the routing strategy. May be <code>null</code> 
	 * if there is no next node.
	 */
	public GraphNode getNextNode();
	
	/**
	 * Required setter to configure the strategy with the graph to work on
	 * @param g
	 */
	public void setGraph( Graph g );
	
	public void setStartNode( GraphNode start );
	
	public void setEndNode( GraphNode end );
	
	public void setCarId( int id );

}
