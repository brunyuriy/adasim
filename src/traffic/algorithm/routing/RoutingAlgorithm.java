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

package traffic.algorithm.routing;

import java.util.List;

import traffic.model.AdasimMap;
import traffic.model.RoadSegment;

/**
 * This interface defines routing strategies. Given a graph, start and end node,
 * a routing strategy should compute a path from start to end and upon request
 * (via <code>getNextNode()</code>) should return the next node the vehicle
 * has to move to to follow that path.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public interface RoutingAlgorithm {
	
	/**
	 * This is intended for testing only, as it circumvents the control
	 * that can be implemented in getNextNode()
	 * @param from
	 * @param to
	 * @return the path computed by this strategy
	 */
	public List<RoadSegment> getPath(RoadSegment from, RoadSegment to); 

	/**
	 * @return The next node according to the routing strategy. May be <code>null</code> 
	 * if there is no next node.
	 */
	public RoadSegment getNextNode();
	
	/**
	 * Required setter to configure the strategy with the graph to work on
	 * @param g
	 */
	public void setGraph( AdasimMap g );
	
	public void setStartNode( RoadSegment start );
	
	public void setEndNode( RoadSegment end );
	
	public void setVehicleId( int id );

}
