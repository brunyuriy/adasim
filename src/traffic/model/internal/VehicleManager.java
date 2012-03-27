/*******************************************************************************
 * Copyright (C) 2011 - 2012 Jochen Wuttke
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
 * Created: Mar 27, 2012
 */

package traffic.model.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import traffic.graph.Graph;
import traffic.model.TrafficSimulator;
import traffic.model.Vehicle;

/**
 * The vehicle manager is responsible for ensuring that vehicles
 * declared in the configuration file enter the simulation at the specified 
 * time. 
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public final class VehicleManager {

	private Map<Integer, List<Vehicle>> startingQueue;
	
	public VehicleManager() {
		startingQueue = new HashMap<Integer, List<Vehicle>>();
	}
	
	/**
	 * Adds a vehicle to the manager, scheduling it for starting
	 * at time <code>time</code>.
	 * @param v
	 * @param time must be <code>> 0</code>
	 */
	public void addVehicle( Vehicle v, int time ) {
		assert time > 0;
		getSlot(time).add(v);
	}
	
	private List<Vehicle> getSlot( int t ) {
		List<Vehicle> l = startingQueue.get(t);
		if ( l == null ) {
			l = new ArrayList<Vehicle>();
			startingQueue.put(t, l);
		}
		return l;
	}
	
	/**
	 * This method should only be called by the {@link TrafficSimulator}.
	 * It is <em>not</em> for other users.
	 * @param g
	 * @param t
	 */
	public void cycle( Graph g, int t ) {
		List<Vehicle> l = startingQueue.remove(t);
		if ( l != null ) {
			for ( Vehicle v : l ) {
				v.getStartNode().enterNode(v);
			}
		}
	}
	
	/**
	 * "Private" accessor method. For testing only.
	 * @return
	 */
	Map<Integer, List<Vehicle>> getQueue() {
		return startingQueue;
	}
}
