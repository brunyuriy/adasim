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

package traffic.strategy;

import java.util.Random;

/**
 * Returns a random noise value
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 *
 */
public class RandomNoiseStrategy implements NoiseStrategy{
	
	public double getNoise() {
		Random generator = new Random();
		double rand = generator.nextDouble();
		return rand;
	}

}
