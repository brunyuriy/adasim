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

import java.util.Random;

/**
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 *
 */

public class RandomSpeedStrategy implements SpeedStrategy{

	/**
	 * Picks a random number between 0 and 9 to be the speed limit
	 */
	public int getDelay(int weight, int capacity, int number) {
		Random generator = new Random();
		return generator.nextInt(10);
	}

}
