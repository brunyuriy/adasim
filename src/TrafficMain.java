/**
 * Jonathan Ramaswamy
 * TrafficMain Version 4
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
		tsim = TrafficSimulator.getInstance(args[0], args[1]);
		logger.info("Starting Simulation");
		boolean done = false;
		while(!done) {
			done = tsim.runSim();
		}
		logger.info("Stopping simulation");
		System.exit(0);
	}
}
