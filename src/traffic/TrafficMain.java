package traffic;
/**
 * Jonathan Ramaswamy
 * TrafficMain
 * Main class for running the traffic simulator
 */

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.jdom.JDOMException;

import traffic.model.ConfigurationException;
import traffic.model.SimulationFactory;
import traffic.model.TrafficSimulator;
 
public class TrafficMain {
	
	private static Logger logger = Logger.getLogger(TrafficMain.class);
	
	private static TrafficSimulator tsim; //The instance of the traffic simulator
	
	public static void main(String[] args) throws JDOMException, IOException, ConfigurationException {
		BasicConfigurator.configure();
		ConfigurationOptions opts = null;
		try {
			opts = ConfigurationOptions.parse(args);
		} catch (Exception e) {
			logger.error( "Parsing commandline arguments: " + e.getMessage() );
			System.exit(1);
		}
		
		assert opts != null;
		
		logger.info("Starting Simulation");
		tsim = SimulationFactory.buildSimulator( new File(opts.getInputFile() ) );
		//tsim = SimulationFactory.buildSimulator(new File(args[0]));
		boolean done = false;
		while(!done) {
			done = tsim.takeSimulationStep();
		}
		logger.info("Stopping simulation");
		System.exit(0);
	}
}
