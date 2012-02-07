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
 * Created: Jan 24, 2012
 */

package traffic.strategy;

/**
 * This is the TrafficLookahead strategy we use in the paper.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class TrafficLookaheadVehicleStrategy extends LookaheadShortestPathVehicleStrategy {

	public TrafficLookaheadVehicleStrategy() {
		super(5,1);
	}
}
