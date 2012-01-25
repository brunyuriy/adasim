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

package traffic;

import java.util.Arrays;

import traffic.model.TrafficSimulator;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * This class stores all configuration options for {@link TrafficSimulator}.
 * The values are assigned through the command line parse called with
 * {@link ConfigurationOptions#parse}
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
final class ConfigurationOptions {
	
	private String inputFile;
	
	/**
	 * This is the main interface to ConfigurationOptions. Passing in
	 * the arguments array will return an object containing all
	 * parsed arguments and default values for arguments not specified
	 * @param args the arguments array from <code>main(String[] args)</code>.
	 * @return A set of configuration options.
	 * @throws Exception
	 */
	static ConfigurationOptions parse( String[] args ) throws Exception {
		ConfigurationOptions cfg = new ConfigurationOptions();
		
		OptionParser parser = setupParser();
		processOptions( parser.parse( args), cfg );
		
		return cfg;
	}

	/**
	 * @param parse
	 * @param cfg
	 * @throws Exception 
	 */
	private static void processOptions(OptionSet opts, ConfigurationOptions cfg) throws Exception {
		if ( opts.has( "I" ) ) {
			cfg.inputFile = opts.valueOf( "I").toString();
		} else throw new Exception( "Argument --input-file is required" );
	}

	/**
	 * 
	 */
	private static OptionParser setupParser() {
		OptionParser parser = new OptionParser();
		parser.acceptsAll( Arrays.asList( "I", "input-file" ) )
			.withRequiredArg()
			.describedAs( "file" );
		return parser;
	}


	/**
	 * @return the name of the file containing the configuration for
	 * this {@link TrafficSimulator}
	 */
	String getInputFile() {
		return inputFile;
	}
	
}
