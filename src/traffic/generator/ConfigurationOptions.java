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

package traffic.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * This class stores all configuration options for {@link Generator}.
 * The values are assigned through the command line parse called with
 * {@link ConfigurationOptions#parse}
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
	private double oneWay = 0.01;
	private boolean bottleneck = false;
	private File graphFile = null;
	private int capacity = 0;
	
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
	 * Process the options found in the commandline arguments
	 * and checks that all required options are present.
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
		if ( opts.has( "node-capacity" ) ) {
			cfg.capacity = Integer.parseInt( opts.valueOf( "node-capacity").toString() ) ;
		} //this argument is optional
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
		if ( opts.has( "one-way-prob" ) ) {
			cfg.oneWay = Double.parseDouble( opts.valueOf( "one-way-prob").toString() );
		} 
		cfg.bottleneck = opts.has( "congested" );
		if ( opts.has( "graph" ) ) {
			cfg.graphFile = new File( opts.valueOf( "graph").toString() );
		}
	}

	/**
	 * Initializes the command line parser with all supported options.
	 */
	private static OptionParser setupParser() {
		OptionParser parser = new OptionParser();
		parser.acceptsAll( Arrays.asList( "N", "num-nodes" ), "Number of nodes (roads) in the simulation" )
			.withRequiredArg()
			.describedAs( "nodes");
		parser.acceptsAll( Arrays.asList( "D", "node-degree" ), "The desired average node degree" )
			.withRequiredArg()
			.describedAs( "deg" );
		parser.acceptsAll( Arrays.asList( "node-capacity" ), "The default node capacity" )
		.withRequiredArg()
		.describedAs( "capacity" );
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
		parser.acceptsAll( Arrays.asList( "one-way-prob"), "Probability for a road to be a one-way street" )
			.withRequiredArg()
			.describedAs( "prob");
		parser.acceptsAll( Arrays.asList( "graph"), "A file containing a user-define graph to be used in the simulation" )
		.withRequiredArg()
		.describedAs( "graph-file");
		parser.accepts( "congested", "Build a simulation that likely contains a bottleneck.");	//TODO: This is a hack for SEAMS 2012
		return parser;
	}

	/**
	 * @return the number of nodes to be generated for the graph
	 */
	int getNumNodes() {
		return numNodes;
	}

	/**
	 * @return the number of cars to placed on the graph (this is approximated only)
	 */
	int getNumCars() {
		return numCars;
	}

	/**
	 * @return the probability with which two nodes should be connected by 
	 * an edge
	 */
	double getDegreeProb() {
		return degreeProb;
	}

	/**
	 * @return the the array of consecutive, valid node delays
	 */
	int[] getNodeDelay() {
		return nodeDelay;
	}

	/**
	 * @return the list of permitted car strategies
	 */
	List<String> getStrategies() {
		return strategies;
	}

	/**
	 * @return the {@link File} object linking to the file for the output XML
	 */
	File getOutputFile() {
		return outputFile;
	}

	/**
	 * @return the probability with which roads are one-way streets
	 */
	double getOneWayProbability() {
		return oneWay;
	}

	/**
	 * @return the bottleneck
	 * @deprecated
	 */
	boolean isBottleneck() {
		return bottleneck;
	}

	/**
	 * If this returns a non-null value, no new graph will be generated and all options 
	 * specifying graph properties are ignored.
	 * 
	 * @return the {@link File} linking to a file containing the XML for a full graph
	 */
	File getGraphFile() {
		return graphFile;
	}

	/**
	 * @return the default traffic capacity for all nodes
	 */
	int getCapacity() {
		return capacity;
	}
	
}
