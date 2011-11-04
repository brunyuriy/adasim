package traffic.model;
/**
 * Jonathan Ramaswamy
 * TrafficSimulator Version 4
 * TrafficSimulator is the main program for running the simulator. It keeps track
 * of the grid and where all the cars are located on it, and outputs position information
 * to the GUI
 */


import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

import traffic.factory.CarFactory;
import traffic.factory.GraphFactory;
import traffic.graph.Graph;

public class TrafficSimulator{
	private static TrafficSimulator instance = null; //The single instance of the simulator
	
	private static Logger logger = Logger.getLogger(TrafficSimulator.class);
	
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
	private int carNum; //Number of cars in the simulation
	private Graph graph; //The graph the cars run on
	
	public TrafficSimulator(String positions, String graphP) {
		cars = new ArrayList<Car>();
		carNum = 0;
		setGraph(graphP);
		readPositions(positions);
		setPaths();
	}
	
	//Reads in the file containing the graph edges and nodes and builds a graph
	private void setGraph(String g) {
		graph = GraphFactory.loadGraph(g);
	}
	
	//Reads in the positions of the cars on the graph
	private void readPositions(String carFile) {
		File positions = new File(carFile);
		try {
			Scanner input = new Scanner(positions);
			String num = input.nextLine();
			int numCars = Integer.parseInt(num);
			for(int i = 0; i < numCars; i++) {
				Car c = CarFactory.loadCar(input.nextLine(), carNum);
				cars.add(c);
				graph.addCarAtNode(carNum, c.getCurrent());
				carNum++;
			}
			logger.info("Positions on graph set");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//Uses the previously specified algorithm to create paths for each car on the graph
	private void setPaths() {
		for(Car c: cars) {
			c.makePath(graph, c.getCurrent());
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
