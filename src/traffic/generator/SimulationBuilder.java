/*******************************************************************************
 * Copyright (c) 2011 - Jochen Wuttke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jochen Wuttke (wuttkej@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Dec 12, 2011
 */

package traffic.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import traffic.graph.Graph;
import traffic.graph.GraphNode;
import traffic.model.Car;
import traffic.model.ConfigurationException;
import traffic.model.TrafficSimulator;
import traffic.strategy.CarStrategy;
import traffic.strategy.LinearSpeedStrategy;
import traffic.strategy.SpeedStrategy;

/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class SimulationBuilder {
	
	private Random random;
	
	SimulationBuilder() {
		random = new Random();
	}
	
	SimulationBuilder( long seed ) {
		random = new Random( seed );
	}
	
	TrafficSimulator build( ConfigurationOptions opts ) throws ConfigurationException {
		Graph g = buildGraph(opts);
		return new TrafficSimulator( g, buildCars(opts, g) );
	}

	/**
	 * @param opts
	 * @return
	 * @throws ConfigurationException 
	 */
	private List<Car> buildCars(ConfigurationOptions opts, Graph g) throws ConfigurationException {
		List<Car> cars = new ArrayList<Car>();
		for ( int i = 0; i < opts.getNumCars(); i++ ) {
			cars.add( buildCar( i, opts, g ) );
		}
		return cars;
	}

	/**
	 * @param i
	 * @param opts
	 * @return
	 * @throws ConfigurationException 
	 */
	private Car buildCar(int i, ConfigurationOptions opts, Graph g) throws ConfigurationException {
		CarStrategy cs = randomCarStrategy( opts.getStrategies() );
		cs.setGraph(g);
		List<GraphNode> nodes = g.getNodes();
		int start = randomNode( nodes );
		int end;
		do {
			end = randomNode(nodes);
		} while ( start == end );
		
		return new Car( start, end, cs, i);
	}

	/**
	 * @param nodes
	 * @return the ID of the randomly chose node
	 */
	private int randomNode(List<GraphNode> nodes) {
		return nodes.get( random.nextInt( nodes.size() ) ).getID();
	}

	/**
	 * @param strategies
	 * @return
	 * @throws ConfigurationException 
	 */
	private CarStrategy randomCarStrategy(List<String> strategies) throws ConfigurationException {
		String s = strategies.get( random.nextInt( strategies.size() ) );
		try {
			@SuppressWarnings("rawtypes")
			Class c = Class.forName( s );
			return (CarStrategy) c.newInstance();
		} catch (Exception e) {
			throw new ConfigurationException(e);
		} 	}

	/**
	 * @param opts
	 * @return
	 * @throws ConfigurationException 
	 */
	private Graph buildGraph(ConfigurationOptions opts) throws ConfigurationException {
		Graph g = new Graph( new HashSet<GraphNode>() );
		for ( int i = 0; i < opts.getNumNodes(); i++ ) {
			g.addNode( buildNode( opts, i ) );
		}
		for ( GraphNode node : g.getNodes() ) {
			randomizeNeighbors(node, g.getNodes(), opts.getDegreeProb(), opts.getOneWayProbability() );
		}
		return g;
	}

	/**
	 * @param opts
	 * @return
	 * @throws ConfigurationException 
	 */
	private GraphNode buildNode(ConfigurationOptions opts, int id ) throws ConfigurationException {
		SpeedStrategy ss = randomSpeedStrategy( opts );
		int delay = randomDelay( opts );
		GraphNode node = new GraphNode(id, ss, delay );
		return node;
	}

	/**
	 * @param opts
	 * @return
	 */
	private int randomDelay(ConfigurationOptions opts) {
		int[] nodeDelay = opts.getNodeDelay();
		return random.nextInt( nodeDelay[1] - nodeDelay[0] + 1 ) + nodeDelay[0];
	}

	/**
	 * @param opts
	 * @return
	 * @throws ConfigurationException 
	 */
	static SpeedStrategy ss = new LinearSpeedStrategy();
	private SpeedStrategy randomSpeedStrategy(ConfigurationOptions opts) throws ConfigurationException {
		return ss;
	}

	/**
	 * @return
	 */
	private void randomizeNeighbors(GraphNode node, List<GraphNode> nodes, double degreeProb, double oneWayProb ) {
		for ( int i = 0; i < nodes.size(); i++) {
			if ( random.nextDouble() < degreeProb/2+oneWayProb && !nodes.get(i).equals(node)) {
				GraphNode target = nodes.get(i);
				node.addEdge(target);
				if ( random.nextDouble() > oneWayProb ) {
					target.addEdge( node );
				}
			}
		}
	}
}
