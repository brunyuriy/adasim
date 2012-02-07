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
 * Created: Oct 25, 2011
 */
package traffic.model;

import traffic.strategy.NoiseStrategy;
import traffic.strategy.RandomNoiseStrategy;

/**
 * VehicleInfo holds all the information for a vehicle object, including its starting, ending,
 * and current positions. This info object can be read by other vehicles and used in conjunction
 * with a strategy algorithm to make decisions
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 */

public class VehicleInfo {

	private NoiseStrategy noise; //The noise strategy

	/**
	 * Creates a new VehicleInfo object with the given parameters
	 */
	public VehicleInfo() {
		noise = new RandomNoiseStrategy();
	}



}
