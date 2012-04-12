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
 * Created: Dec 13, 2011
 */

package adasim.model.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import adasim.model.Vehicle;



/**
 * The {@link RoadVehicleQueue} handles adasim on a single node.
 * Entering vehicles are assigned to a slot matching the current delay
 * of the node, and in each simulation cycle, they are moved
 * forward one slot.
 * When they reach the end, they are asked to leave or they are stopped.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public final class RoadVehicleQueue {
	
	/**
	 * ID for the special queue tracking parked/stopped vehicles.
	 */
	private final static int PARKED = -1;
	private Map<Integer, Set<Vehicle>> queue;
	
	/**
	 * Initializes an empty queue.
	 */
	public RoadVehicleQueue() {
		queue = new HashMap<Integer, Set<Vehicle>>();
		queue.put( PARKED, new HashSet<Vehicle>() );
		queue.put( 0, new HashSet<Vehicle>() );
	}
	
	/**
	 * Adds <code>c</code> to the queue in the slot matching the given <code>delay</code>.
	 * @param c
	 * @param delay
	 */
	public void enqueue( Vehicle c, int delay ) {
		assert delay >= 0;
		ensureBucketExists( delay );
		queue.get( delay ).add(c);
	}

	/**
	 * @param delay
	 */
	private void ensureBucketExists(int delay) {
		if ( queue.get(delay) == null ) {
			queue.put(delay, new HashSet<Vehicle>() );
		}
	}

	/**
	 * Moves all vehicles one step ahead
	 * @return the list of vehicles that have reached the tip of the queue
	 */
	public Set<Vehicle> moveVehicles() {
		Set<Vehicle> fs = queue.remove(0);
		SortedSet<Integer> keys = new TreeSet<Integer>( queue.keySet() );
		for ( Integer key : keys ) {
			if ( key == PARKED ) continue;	//default cases handled separately
			Set<Vehicle> c = queue.remove(key);
			queue.put(key-1, c );
		}
		return fs;
	}
	
	/**
	 * Removes <code>c</code> from the active list of vehicles.
	 * @param c
	 */
	public void park( Vehicle c ) {
		removeFromQueue( c );
		queue.get( PARKED ).add( c );
	}

	/**
	 * @param c
	 */
	private void removeFromQueue(Vehicle c) {
		for ( Integer key : queue.keySet() ) {
			if ( key == PARKED ) continue;
			if ( queue.get(key).remove(c) ) return;	//short circuit
		}
	}
	
	/**
	 * @return <code>true</code> if there are no vehicles in the queue that are waiting to move
	 */
	boolean isEmpty() {
		for ( Integer key : queue.keySet() ) {
			if ( key == PARKED ) continue;
			if ( !queue.get(key).isEmpty() ) return false; //short circuit
		}
		return true;
	}

	/**
	 * @return the current number of active vehicles in this queue (parked vehicles are ignored)
	 */
	public int size() {
		int s = 0;
		for ( int key : queue.keySet() ) {
			if ( key == PARKED ) continue;
			s+= queue.get(key).size();
		}	
		return s;
	}
}
