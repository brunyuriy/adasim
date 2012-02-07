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
 * Created: Jan 18, 2012
 */

package traffic.model;

/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
class InternalVehicle extends Vehicle {
	
	private int start, end;
	
	InternalVehicle( int id, int start, int end ) {
		super( id );
		this.start = start;
		this.end = end;
	}

	/**
	 * @return the start
	 */
	int getStart() {
		return start;
	}

	/**
	 * @return the end
	 */
	int getEnd() {
		return end;
	}
	
	

}
