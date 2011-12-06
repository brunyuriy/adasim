package traffic.model;
/**
 * Jonathan Ramaswamy
 * TrafficSimulator
 * TrafficSimulator is the main program for running the simulator. It keeps track
 * of the graph and where all the cars are located on it, and outputs information to
 * the logger object with every step taken.
 */

import java.util.*;

import traffic.graph.Graph;

public class TrafficSimulator{
		
	private List<Car> cars; //List of cars in the simulation
	private Graph graph; //The graph the cars run on
	
	TrafficSimulator( Graph g, List<Car> c ) {
		if ( g == null ) throw new IllegalArgumentException( "Graph must not be null" );
		if ( c == null ) throw new IllegalArgumentException( "Cars must not be null" );
		this.graph = g;
		this.cars = c;
		setPaths();
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

	/**
	 * @return the cars
	 */
	public List<Car> getCars() {
		return cars;
	}

	/**
	 * @return the graph
	 */
	public Graph getGraph() {
		return graph;
	}

}
