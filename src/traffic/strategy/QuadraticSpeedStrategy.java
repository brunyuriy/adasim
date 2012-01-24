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
 * Created: Oct 18, 2011
 */

package traffic.strategy;

/**
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 *
 */

public class QuadraticSpeedStrategy implements SpeedStrategy {

	/**
	 * The speed limit is set to be proportionate to the square of the number of cars at the node
	 */
	public int getDelay(int weight, int capacity, int number) {
		if (number > capacity) {
			return number - capacity + (weight * weight);
		} else {
			return weight * weight;
		}
	}

}
