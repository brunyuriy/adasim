package traffic;
/**
 * Jonathan Ramaswamy
 * TrafficMain
 * Main class for running the traffic simulator
 */

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import traffic.model.TrafficSimulator;
 
public class TrafficMain {
	
	private static Logger logger = Logger.getLogger(TrafficMain.class);
	
	private static TrafficSimulator tsim; //The instance of the traffic simulator
	
	public static void main(String[] args) {
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
		tsim = TrafficSimulator.getInstance(opts.getInputFile()); //args[0] = config
		boolean done = false;
		while(!done) {
			done = tsim.takeSimulationStep();
		}
		logger.info("Stopping simulation");
		System.exit(0);
	}
}
