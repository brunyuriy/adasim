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
 * Created: Dec 3, 2011
 */

package traffic.generator;

/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class Generator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ConfigurationOptions opts = null;
		try {
			opts = ConfigurationOptions.parse(args);
		} catch (Exception e) {
			System.err.println( "Erros parsing commandline options: " + e.getMessage() );
			System.exit( 1 );
		}
	}

}
