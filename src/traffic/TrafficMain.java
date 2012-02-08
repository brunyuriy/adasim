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
 *    Jonathan Ramaswamy (ramaswamyj12@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Jul 29, 2011
 */

package traffic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.jdom.JDOMException;

import traffic.model.ConfigurationException;
import traffic.model.SimulationXMLReader;
import traffic.model.TrafficSimulator;

/**
 * This is the main class for running the simulator
 * It takes in a commandline argument and attempts to run the simulation with it
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 */

public class TrafficMain {
	
	private static Logger logger = Logger.getLogger(TrafficMain.class); //Logger that outputs simulation information
	
	private static TrafficSimulator tsim; //The instance of the traffic simulator
	
	/**
	 * 
	 * @param args
	 * @throws JDOMException
	 * @throws IOException
	 * @throws ConfigurationException
	 */
	public static void main(String[] args) throws JDOMException, IOException, ConfigurationException {
		BasicConfigurator.configure();
		ConfigurationOptions opts = null;
		logger.info( Version.versionString() );
		try {
			opts = ConfigurationOptions.parse(args);
		} catch (Exception e) { //Catches invalid commandline statement
			logger.error( "Parsing commandline arguments: " + e.getMessage() );
			System.exit(1);
		}
		
		assert opts != null;

		logger.info("Loading Simulation");
		try{
			tsim = SimulationXMLReader.buildSimulator( new File(opts.getInputFile() ) );
		} catch (ConfigurationException e) { //Catches configuration error in XML file
			logger.info("Exiting due to configuration error " + e.getMessage());
			System.exit(0);
		} catch (FileNotFoundException e) {
			logger.info("Exiting because file cannot be found " + e.getMessage());
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
