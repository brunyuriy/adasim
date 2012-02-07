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

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jdom.JDOMException;

import traffic.model.ConfigurationException;

/**
 * A simple generator for random simulation setups.
 * <p>
 * It supports full randomization of graphs of defined 
 * sizes with a defined number of cars, and it supports
 * loading graph and randomizing only cars for that graph.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class Generator {

	/**
	 * @param args
	 * @throws JDOMException 
	 */
	public static void main(String[] args) {
		ConfigurationOptions opts = null;
		try {
			opts = ConfigurationOptions.parse(args);
		} catch (Exception e) {
			System.err.println( "Error parsing command line options: " + e.getMessage() );
			System.exit( 1 );
		}
		
		try {
			if ( opts.isBottleneck() ) {
				SimulationXMLWriter.write( new CongestedSimulationBuilder().build(opts), opts.getOutputFile() );
			} else {
				SimulationXMLWriter.write( new SimulationBuilder().build(opts), opts.getOutputFile() );
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
