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
 * This interface implement traffic delay functions.
 * Given the weight of a node, the number of cars and an additional parameter 
 * (the traffic capacity of a node), it returns the time required
 * to traverse that node.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public interface SpeedStrategy {
	
	public int getDelay(int weight, int cutoff, int number); //Returns the speed limit for the node depending on the number of cars

}
