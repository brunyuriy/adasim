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
		graph = new Graph();
		setGraph(graphP);
		readPositions(positions);
	}
	
	//Reads in the file containing the graph edges and nodes and builds a graph
	private void setGraph(String g) {
		File graphP = new File(g);
		try {
			Scanner input = new Scanner(graphP);
			int nodes = Integer.parseInt(input.nextLine());
			for(int i = 0; i < nodes; i++) {
				String stop = input.nextLine();
				int s = Integer.parseInt(stop.substring(2,3));
				graph.addNode(i, s);
			}
			while(input.hasNextLine()) {
				String edge = input.nextLine();
				int i = Integer.parseInt(edge.substring(0,1));
				int o = Integer.parseInt(edge.substring(2,3));
				graph.addEdge(i, o);
			}
			logger.info("Graph set");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the car with the given number
	 */
	public Car getCar(int index) {
		return cars.get(index);
	}
	
	/**
	 * Returns the number of cars in the simulation
	 */
	public int getNumberOfCars() {
		return carNum;
	}
	
	//Reads in the positions of the cars on the graph
	public void readPositions(String carFile) {
		File positions = new File(carFile);
		try {
			Scanner input = new Scanner(positions);
			while(input.hasNextLine()) {
				String position = input.nextLine();
				Car c = new Car(position, carNum);
				cars.add(c);
				graph.addCarAtNode(carNum, c.getCurrent());
				carNum++;
			}
			logger.info("Positions on graph set");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
	
	/**
	 * Runs the simulation by trying to move each car one at a time
	 * @return True if the simulation is over
	 */
	public boolean runSim() {
		for(Car c: cars) {
			c.tryMove(graph);
		}
		return checkAllFinish();
	}
}
