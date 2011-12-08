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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
final class ConfigurationOptions {
	
	private int numNodes = 0;
	private int numCars = 0;
	private double degreeProb = 0.0;
	private int[] nodeDelay = { 0, 0 };
	private List<String> strategies = new ArrayList<String>();
	private File outputFile = new File( "generated-config.xml" );
	
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
		if ( opts.has( "N" ) ) {
			cfg.numNodes = Integer.parseInt( opts.valueOf( "N").toString() );
		} else throw new Exception( "Argument --num-nodes is required" );
		if ( opts.has( "C" ) ) {
			cfg.numCars = Integer.parseInt( opts.valueOf( "C").toString() );
		} else throw new Exception( "Argument --num-cars is required" );
		if ( opts.has( "D" ) ) {
			cfg.degreeProb = Integer.parseInt( opts.valueOf( "D").toString() ) / (double)cfg.numNodes;
		} else throw new Exception( "Argument --node-degree is required" );
		if ( opts.has( "o" ) ) {
			cfg.outputFile = new File( opts.valueOf( "o").toString() );
		} else throw new Exception( "Argument --output-file is required" );
		if ( opts.has( "d" ) ) {
			String s = opts.valueOf( "d").toString();
			String[] ss = s.split( ":");
			if ( ss.length != 2 ) throw new Exception( "Argument to node-delay must have the form min:max" );
			cfg.nodeDelay[0] = Integer.parseInt(ss[0]);
			cfg.nodeDelay[1] = Integer.parseInt(ss[1]);
		} else throw new Exception( "Argument --node-delay is required" );
		if ( opts.has( "S") ) {
			String[] ss = opts.valueOf("S").toString().split( ",");
			for ( String s : ss ) {
				cfg.strategies.add( s );
			}
		} else throw new Exception( "Argument --car-strategies is required" );
	}

	/**
	 * 
	 */
	private static OptionParser setupParser() {
		OptionParser parser = new OptionParser();
		parser.acceptsAll( Arrays.asList( "N", "num-nodes" ), "Number of nodes (roads) in the simulation" )
			.withRequiredArg()
			.describedAs( "nodes");
		parser.acceptsAll( Arrays.asList( "D", "node-degree" ), "The desired average node degree" )
			.withRequiredArg()
			.describedAs( "prob" );
		parser.acceptsAll(Arrays.asList( "d", "node-delay"), "Range of possible node delays" )
			.withRequiredArg()
			.describedAs( "min:max" );
		parser.acceptsAll( Arrays.asList( "C", "num-cars" ), "Number of cars in the simulation" )
			.withRequiredArg()
			.describedAs( "cars" );
		parser.acceptsAll( Arrays.asList( "S", "car-strategies" ), "Comma separated list of possible routing strategies" )
			.withRequiredArg()
			.describedAs( "strategies" );
		parser.acceptsAll( Arrays.asList( "o", "output-file" ) )
			.withRequiredArg()
			.describedAs( "file" );
		return parser;
	}

	/**
	 * @return the numNodes
	 */
	int getNumNodes() {
		return numNodes;
	}

	/**
	 * @return the numCars
	 */
	int getNumCars() {
		return numCars;
	}

	/**
	 * @return the degreeProb
	 */
	double getDegreeProb() {
		return degreeProb;
	}

	/**
	 * @return the nodeDelay
	 */
	int[] getNodeDelay() {
		return nodeDelay;
	}

	/**
	 * @return the strategies
	 */
	List<String> getStrategies() {
		return strategies;
	}

	/**
	 * @return the outputFile
	 */
	File getOutputFile() {
		return outputFile;
	}
	
}
