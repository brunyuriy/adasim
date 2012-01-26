/*******************************************************************************
 * Copyright (c) 2011 - Jonathan Ramaswamy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jonathan Ramaswamy (ramaswamyj12@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Jul 29, 2011
 */

package traffic.model;

import java.util.*;

import org.apache.log4j.Logger;

import traffic.TrafficMain;
import traffic.graph.Graph;
import traffic.graph.GraphNode;

/**
 * TrafficSimulator is the main program for running the simulator. It keeps track
 * of the graph and where all the cars are located on it, and outputs information to
 * the logger object with every step taken.
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 */

public class TrafficSimulator{
	
	private static Logger logger = Logger.getLogger(TrafficSimulator.class);

		
	private List<Car> cars; //List of cars in the simulation
	private Graph graph; //The graph the cars run on
	private long cycle = 1;
	
	public TrafficSimulator( Graph g, List<Car> c ) {
		if ( g == null ) throw new IllegalArgumentException( "Graph must not be null" );
		if ( c == null ) throw new IllegalArgumentException( "Cars must not be null" );
		this.graph = g;
		this.cars = c;
		addCarsToGraph();
	}
	
	//Uses the previously specified algorithm to create paths for each car on the graph
	private void addCarsToGraph() {
		for(Car c: cars) {
			graph.addCarAtNode(c, c.getCurrent());
		}
	}
	
	/**
	 * Runs the simulation by trying to move each car one at a time
	 * @return True if the simulation is over
	 */
	public boolean takeSimulationStep() {
		logger.info( "SIMULATION: Cycle: " + cycle++ );
		for ( GraphNode node: graph.getNodes() ) {
			node.takeSimulationStep();
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
	 * Returns the car with the given ID
	 * @param id
	 * @return
	 */
	public Car getCar( int id ) {
		for ( Car c : cars ) {
			if ( c.getID() == id ) 
				return c;
		}
		return null;
	}

	/**
	 * @return the graph
	 */
	public Graph getGraph() {
		return graph;
	}

}
