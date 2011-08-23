/**
 * Main class for running the traffic simulator
 */

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
 
public class TrafficMain {
	
	private static Logger logger = Logger.getLogger(TrafficMain.class);
	
	private static TrafficSimulator tsim;
	private static Timer timer;
	private static final int move = 100;
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		tsim = TrafficSimulator.getInstance();
		tsim.setFileName(args[0]);
		tsim.readPositions();
		timer = new Timer();
		logger.info("Starting Simulation");
		timer.scheduleAtFixedRate(new SimTask(), 0, move);
	}
	
	public static class SimTask extends TimerTask {

		public void run() {
			boolean done = tsim.runSim();
			if(done) {
				endSimulation();
			}
		}
		
	}
	
	private static void endSimulation() {
		logger.info("Stopping simulation");
		timer.cancel();
		System.exit(0);
	}
}
