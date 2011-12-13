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
 * Created: Dec 12, 2011
 */

package traffic.strategy;

import traffic.graph.Graph;

/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public abstract class AbstractCarStrategy implements CarStrategy {
	
	protected Graph graph;

	/* (non-Javadoc)
	 * @see traffic.strategy.CarStrategy#setGraph(traffic.graph.Graph)
	 */
	@Override
	public void setGraph(Graph g) {
		this.graph = g;
	}

}