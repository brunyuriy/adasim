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

package adasim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.jdom.JDOMException;

import adasim.model.ConfigurationException;
import adasim.model.RoadSegment;
import adasim.model.TrafficSimulator;
import adasim.model.internal.SimulationXMLReader;


/**
 * This is the main class for running the simulator.
 * <p>
 * It takes on commandline argument <code>-I file</code> with the name
 * of a configuration file defining a simulation. It will then load,
 * validate and run the simulation. All simulation output is 
 * written to stdout.
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 */

public class TrafficMain {
	
	private static Logger logger = Logger.getLogger(TrafficMain.class); //Logger that outputs simulation information
	
	/**
	 * 
	 * @param args
	 * @throws JDOMException
	 * @throws IOException
	 * @throws ConfigurationException
	 */
	public static void main(String[] args) {
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
			TrafficSimulator tsim = SimulationXMLReader.buildSimulator( new File(opts.getInputFile() ) );
			logger.info("Starting Simulation");
			tsim.run();
			logger.info("Stopping simulation");
		} catch (ConfigurationException e) { //Catches configuration error in XML file
			logger.info("Exiting due to configuration error " + e.getMessage());
		} catch (FileNotFoundException e) {
			logger.info("Exiting because file cannot be found " + e.getMessage());
		}
		
		String content = "var links = [\n"+ RoadSegment.linksOfNodes +"\n];";
		//logger.info(content);
		//Write to file
		try {
			File file = new File("visual/js/mappingData.js");
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close(); // Be sure to close BufferedWriter
			logger.info("To view visual map  open visual/index.html");
		} catch (Exception e) {
			// TODO: handle exception
		}
	
		
		//NO CODE BEYOND THIS LINE
	}
}
