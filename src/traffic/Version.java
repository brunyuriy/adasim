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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

/**
 * This class simply stores version information and has no active component.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
class Version {
	private static String VERSION = "";
	
	static{
		Properties props = new Properties();
		try {
			URL res = Version.class.getClassLoader().getResource( "resources/VERSION" );
			props.load( new InputStreamReader( res.openStream() ) );
			VERSION = props.getProperty("version.major") + "." + props.getProperty("version.minor") 
					+ "." + props.getProperty( "version.build" );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static public String versionString() {
		return "Adasim - Version " + VERSION ;
	}

	
}
