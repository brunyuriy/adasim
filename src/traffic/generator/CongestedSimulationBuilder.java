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

import traffic.agent.AdasimAgent;
import traffic.graph.Graph;
import traffic.graph.GraphNode;
import traffic.model.Vehicle;
import traffic.model.ConfigurationException;
import traffic.model.TrafficSimulator;
import traffic.strategy.VehicleStrategy;
import traffic.strategy.LinearSpeedStrategy;
import traffic.strategy.SpeedStrategy;

/**
 * This class is hack we used for some experiments. 
 * It has not been updated for the new configuration file
 * formats and does not support most of the interesting
 * features of adasim.
 * @author Jochen Wuttke - wuttkej@gmail.com
 * @deprecated
 */
public class CongestedSimulationBuilder {
	
	private Random random;
	
	CongestedSimulationBuilder() {
		random = new Random();
	}
	
	CongestedSimulationBuilder( long seed ) {
		random = new Random( seed );
	}
	
	TrafficSimulator build( ConfigurationOptions opts ) throws ConfigurationException {
		Graph g = buildGraph(opts);
		return new TrafficSimulator( g, null, buildVehicles(opts, g) );
	}

	/**
	 * @param opts
	 * @return a list of vehicles, size determined by commandline argument
	 * @throws ConfigurationException 
	 */
	private List<AdasimAgent> buildVehicles(ConfigurationOptions opts, Graph g) throws ConfigurationException {
		List<AdasimAgent> vehicles = new ArrayList<AdasimAgent>();
		List<GraphNode> nodes = g.getNodes();
		GraphNode start = randomNode( nodes );
		GraphNode end;
		do {
			end = randomNode(nodes);
		} while ( start.equals(end) );

		for ( int i = 0; i < opts.getNumVehicles(); i++ ) {
			vehicles.add( buildVehicle( i, opts, g, start, end ) );
		}
		return vehicles;
	}

	/**
	 * @param i
	 * @param opts
	 * @return a fully configured vehicle
	 * @throws ConfigurationException 
	 */
	private Vehicle buildVehicle(int i, ConfigurationOptions opts, Graph g, GraphNode start, GraphNode end ) throws ConfigurationException {
		VehicleStrategy cs = randomVehicleStrategy( opts.getStrategies() );
		cs.setGraph(g);		
		return new Vehicle( start, end, cs, i);
	}

	/**
	 * @param nodes
	 * @return the ID of the randomly chose node
	 */
	private GraphNode randomNode(List<GraphNode> nodes) {
		return nodes.get( random.nextInt( nodes.size() ) );
	}


	/**
	 * @param strategies
	 * @return a random strategy picked from the allowed list
	 * @throws ConfigurationException 
	 */
	private VehicleStrategy randomVehicleStrategy(List<String> strategies) throws ConfigurationException {
		String s = strategies.get( random.nextInt( strategies.size() ) );
		try {
			@SuppressWarnings("rawtypes")
			Class c = Class.forName( s );
			return (VehicleStrategy) c.newInstance();
		} catch (Exception e) {
			throw new ConfigurationException(e);
		} 	}

	/**
	 * @param opts
	 * @return a fully configured graph
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
	 * @return a fully configured node
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
	 * @return some random delay picked from the allowed range
	 */
	private int randomDelay(ConfigurationOptions opts) {
		int[] nodeDelay = opts.getNodeDelay();
		return random.nextInt( nodeDelay[1] - nodeDelay[0] + 1 ) + nodeDelay[0];
	}

	static SpeedStrategy ss = new LinearSpeedStrategy();
	/**
	 * @param opts
	 * @return ss the currently configured speed strategy, if <code>null</code> this will cause problems
	 * @throws ConfigurationException 
	 */
	private SpeedStrategy randomSpeedStrategy(ConfigurationOptions opts) throws ConfigurationException {
		return ss;
	}

	private void randomizeNeighbors(GraphNode node, List<GraphNode> nodes, double degreeProb, double oneWayProb ) {
		for ( int i = 0; i < nodes.size(); i++) {
			if ( random.nextDouble() < degreeProb/2/(1+oneWayProb) && !nodes.get(i).equals(node)) {
				GraphNode target = nodes.get(i);
				node.addEdge(target);
				if ( random.nextDouble() > oneWayProb ) {
					target.addEdge( node );
				}
			}
		}
	}
}
