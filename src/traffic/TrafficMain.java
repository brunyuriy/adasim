/*******************************************************************************
 * Copyright (c) 2011 - Jonathan Ramaswamy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jonathan Ramaswamy (ramaswamyj12@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Jul 29, 2011
 */

package traffic;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.jdom.JDOMException;

import traffic.model.ConfigurationException;
import traffic.model.SimulationXMLReader;
import traffic.model.TrafficSimulator;

/**
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 *
 */

public class TrafficMain {
	
	private static Logger logger = Logger.getLogger(TrafficMain.class);
	
	private static TrafficSimulator tsim; //The instance of the traffic simulator
	
	public static void main(String[] args) throws JDOMException, IOException, ConfigurationException {
		BasicConfigurator.configure();
		ConfigurationOptions opts = null;
		logger.info( Version.versionString() );
		try {
			opts = ConfigurationOptions.parse(args);
		} catch (Exception e) {
			logger.error( "Parsing commandline arguments: " + e.getMessage() );
			System.exit(1);
		}
		
		assert opts != null;

		logger.info("Loading Simulation");
		try{
			tsim = SimulationXMLReader.buildSimulator( new File(opts.getInputFile() ) );
		} catch (ConfigurationException e) {
			logger.info("Exiting due to configuration error");
			System.exit(0);
		}
		logger.info("Starting Simulation");
		boolean done = false;
		while(!done) {
			done = tsim.takeSimulationStep();
		}
		logger.info("Stopping simulation");
		System.exit(0);
	}
}
