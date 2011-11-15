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
		logger.info("Starting Simulation");
		tsim = TrafficSimulator.getInstance(args[0], args[1]); //args[0] = cars, args[1] = graph
		boolean done = false;
		while(!done) {
			done = tsim.takeSimulationStep();
		}
		logger.info("Stopping simulation");
		System.exit(0);
	}
}
