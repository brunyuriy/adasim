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
 * Created: Nov 3, 2011
 */

package traffic;

/**
 * This class simply stores version information and has no active component.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class Version {
	
	private final static String VERSION = "0.5.0";

	static public String versionString() {
		return "Adasim - Version " + VERSION ;
	}

	
}
