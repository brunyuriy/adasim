/**
 * Jonathan Ramaswamy
 * TrafficMain Version 4
 * Main class for running the traffic simulator
 */

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import traffic.model.TrafficSimulator;
 
public class TrafficMain {
	
	private static Logger logger = Logger.getLogger(TrafficMain.class);
	
	private static TrafficSimulator tsim; //The instance of the traffic simulator
	private static Timer timer; //The timer to keep the simulation running at a constant pace
	private static final int move = 1000; //The interval between each move in miliseconds
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		tsim = TrafficSimulator.getInstance(args[0], args[1]);
		timer = new Timer();
		runSimulation();
	}
	
	//Runs the timer for the simulation
	private static void runSimulation() {
		logger.info("Starting Simulation");
		timer.scheduleAtFixedRate(new SimTask(), 0, move);
	}
	
	//A timer task that runs whenever called to perform a move in the simulation
	private static class SimTask extends TimerTask {

		/**
		 * Completes one step in the simulation for each call, stops running when
		 * the simulation is over
		 */
		public void run() {
			boolean done = tsim.runSim();
			if(done) {
				endSimulation();
			}
		}
		
	}
	
	//Ends the simulation, stops the timer, and exits the program
	private static void endSimulation() {
		logger.info("Stopping simulation");
		timer.cancel();
		System.exit(0);
	}
}
