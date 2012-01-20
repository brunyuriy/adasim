/*******************************************************************************
 * Copyright (c) 2011 - Jochen Wuttke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jochen Wuttke (wuttkej@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Dec 13, 2011
 */

package traffic.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import traffic.model.Car;

/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
final class NodeVehicleQueue {
	
	private final static int PARKED = -1;
	private Map<Integer, Set<Car>> queue;
	
	/**
	 * 
	 */
	public NodeVehicleQueue() {
		queue = new HashMap<Integer, Set<Car>>();
		queue.put( PARKED, new HashSet<Car>() );
		queue.put( 0, new HashSet<Car>() );
	}
	
	void enqueue( Car c, int delay ) {
		assert delay >= 0;
		ensureBucketExists( delay );
		queue.get( delay ).add(c);
	}

	/**
	 * @param delay
	 */
	private void ensureBucketExists(int delay) {
		if ( queue.get(delay) == null ) {
			queue.put(delay, new HashSet<Car>() );
		}
	}

	Set<Car> moveCars() {
		Set<Car> fs = queue.remove(0);
		SortedSet<Integer> keys = new TreeSet<Integer>( queue.keySet() );
		for ( Integer key : keys ) {
			if ( key == PARKED ) continue;	//default cases handled separately
			Set<Car> c = queue.remove(key);
			queue.put(key-1, c );
		}
		return fs;
	}
	
	void park( Car c ) {
		removeFromQueue( c );
		queue.get( PARKED ).add( c );
	}

	/**
	 * @param c
	 */
	private void removeFromQueue(Car c) {
		for ( Integer key : queue.keySet() ) {
			if ( key == PARKED ) continue;
			if ( queue.get(key).remove(c) ) return;	//short circuit
		}
	}
	
	/**
	 * @return <code>true</code> if there are no cars in the queue that are waiting to move
	 */
	boolean isEmpty() {
		for ( Integer key : queue.keySet() ) {
			if ( key == PARKED ) continue;
			if ( !queue.get(key).isEmpty() ) return false; //short circuit
		}
		return true;
	}

	/**
	 * @return
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
