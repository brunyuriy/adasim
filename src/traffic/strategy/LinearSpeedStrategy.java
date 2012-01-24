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

public class LinearSpeedStrategy implements SpeedStrategy {

	/**
	 * Sets the number of turns a car must wait at a node to be equal to
	 * the number of cars currently at the node
	 */
	public int getDelay(int weight, int capacity, int number) {
		return Math.max(weight, number - capacity + weight);
	}

}
