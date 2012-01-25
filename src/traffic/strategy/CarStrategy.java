/**
 * Jonathan Ramaswamy
 * Car Strategy
 * The strategy interface for specific car strategies to follow
 */
package traffic.strategy;

import java.util.List;

import traffic.graph.Graph;
import traffic.graph.GraphNode;

/**
 * This interface defines routing strategies. Given a graph, start and end node,
 * a routing strategy should compute a path from start to end and upon request
 * (via <code>getNextNode()</code>) should return the next node the car
 * has to move to to follow that path.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
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
