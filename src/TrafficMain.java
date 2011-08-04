/**
 * Main class for running the traffic simulator
 */
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
 
public class TrafficMain {
	
	private static Logger logger = Logger.getLogger(TrafficMain.class);
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		logger.info("Entering Application");
		new TrafficGUI();
	}
}
