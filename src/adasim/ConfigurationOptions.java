/*******************************************************************************
 * Copyright (C) 2011 - 2012 Jochen Wuttke, Jonathan Ramaswamy
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Contributors:
 *    Jochen Wuttke (wuttkej@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Dec 3, 2011
 */

package adasim;

import java.util.Arrays;

import adasim.model.TrafficSimulator;


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
	 * parsed arguments and default values for arguments not specified.
	 * 
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
	 * @param opts	the options found on the commandline
	 * @param cfg	the data object storing option values
	 * @throws Exception
	 */
	private static void processOptions(OptionSet opts, ConfigurationOptions cfg) throws Exception {
		if ( opts.has( "I" ) ) {
			cfg.inputFile = opts.valueOf( "I").toString();
		} else throw new Exception( "Argument --input-file is required" );
	}

	/**
	 * Defines all the options the parser should recognize. 
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
