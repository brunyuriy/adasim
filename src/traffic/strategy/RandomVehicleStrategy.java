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
 ********************************************************************************
 *
 * Created: Oct 11, 2011
 */

package traffic.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import traffic.model.GraphNode;

/**
 * This class returns a random path for the car to follow
 * No optimizations for shortest path are made
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 */

public class RandomVehicleStrategy extends AbstractVehicleStrategy {

	public RandomVehicleStrategy(){}	
	
	/**
	 * Picks random neighbors to move towards until the destination is reached
	 * Returns the path represented as a list of integers
	 */
	public List<GraphNode> getPath(GraphNode c, GraphNode d) {
		GraphNode next = c;
		List<GraphNode> path = new ArrayList<GraphNode>();
		while(!next.equals(d) && path.size() < graph.getNodes().size() ) {
			List<GraphNode> dest = next.getNeighbors();
			Random generator = new Random();
			int rand = generator.nextInt(dest.size());
			next = dest.get(rand);
			path.add(next);
		}
		return path.get( path.size() - 1 ) == d ? path : null ;	//The random strategy must terminate even it if can't find a path
	}

	/* (non-Javadoc)
	 * @see traffic.strategy.CarStrategy#getNextNode()
	 */
	@Override
	public GraphNode getNextNode() {
		// TODO Auto-generated method stub
		return null;
	}

}
