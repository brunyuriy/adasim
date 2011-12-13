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

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import traffic.graph.Graph;
import traffic.graph.GraphNode;
import traffic.model.Car;
import traffic.model.ConfigurationException;
import traffic.model.TrafficSimulator;
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
	 */
	private List<Car> buildCars(ConfigurationOptions opts, Graph g) {
		// TODO Auto-generated method stub
		return null;
	}

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
	private SpeedStrategy randomSpeedStrategy(ConfigurationOptions opts) throws ConfigurationException {
		String s = opts.getStrategies().get( random.nextInt( opts.getStrategies().size() ) );
		try {
			@SuppressWarnings("rawtypes")
			Class c = Class.forName( s );
			return (SpeedStrategy) c.newInstance();
		} catch (Exception e) {
			throw new ConfigurationException(e);
		} 
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
