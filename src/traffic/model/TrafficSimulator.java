package traffic.model;
/**
 * Jonathan Ramaswamy
 * TrafficSimulator
 * TrafficSimulator is the main program for running the simulator. It keeps track
 * of the graph and where all the cars are located on it, and outputs information to
 * the logger object with every step taken.
 */

import java.util.*;

import traffic.factory.CarFactory;
import traffic.factory.CarFactoryXML;
import traffic.factory.GraphFactory;
import traffic.factory.GraphFactoryXML;
import traffic.graph.Graph;

public class TrafficSimulator{
	private static TrafficSimulator instance = null; //The single instance of the simulator
		
	/**
	 * Returns the single instance of the traffic simulator
	 */
	public static TrafficSimulator getInstance(String positions, String graph) {
		if (instance == null) {
			instance = new TrafficSimulator(positions, graph);
		}
		return instance;
	}
	
	private List<Car> cars; //List of cars in the simulation
	private Graph graph; //The graph the cars run on
	
	public TrafficSimulator(String positions, String graphP) {
		cars = new ArrayList<Car>();
		setGraph(graphP);
		readPositions(positions);
		setPaths();
	}
	
	//Reads in the file containing the graph edges and nodes and builds a graph
	private void setGraph(String g) {
		graph = GraphFactoryXML.loadGraph(g);
	}
	
	//Reads in the positions of the cars on the graph
	private void readPositions(String carFile) {
		cars = CarFactoryXML.loadCar(carFile);
		for(Car c: cars) {
			graph.addCarAtNode(c.getCarNumber(), c.getCurrent());
		}
	}
	
	//Uses the previously specified algorithm to create paths for each car on the graph
	private void setPaths() {
		for(Car c: cars) {
			c.makePath(graph);
		}
	}
	
	/**
	 * Runs the simulation by trying to move each car one at a time
	 * @return True if the simulation is over
	 */
	public boolean takeSimulationStep() {
		for(Car c: cars) {
			c.tryMove(graph);
		}
		return checkAllFinish();
	}
	
	//Checks to see if all cars have finished moving, returns true if so
	private boolean checkAllFinish() {
		for(Car c: cars) {
			if(!c.checkFinish()) {
				return false;
			}
		}
		return true;
	}

}
