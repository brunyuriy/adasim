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
 * Created: Dec 3, 2011
 */

package traffic.generator;

import java.util.List;

import org.jdom.Content;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Document;
import org.jdom.Element;

import traffic.strategy.CarStrategy;

/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
class SimulationConfigBuilder {
	
	private static final String DEFAULT_SPEED_STRATEGY = "traffic.strategy.LinearSpeedStrategy";
	private static final String DEFAULT_CAR_STRATEGY = "traffic.strategy.DijkstraCarStrategy";
	
	private DefaultJDOMFactory factory = new DefaultJDOMFactory();
	
	Document build( ConfigurationOptions opts ) {
		Element sim = factory.element( "simulation" );
		factory.addContent(sim, buildGraph( opts.getNumNodes(), opts.getNodeDelay(), opts.getDegreeProb() ) );
		factory.addContent(sim, buildCars( opts.getNumCars(), opts.getStrategies() ) );
		return factory.document( sim );
	}

	/**
	 * @param numCars
	 * @param strategies
	 * @return
	 */
	Content buildCars(int numCars, List<Class<CarStrategy>> strategies) {
		Element cars = factory.element( "cars" );
		cars.setAttribute( "default_strategy", DEFAULT_CAR_STRATEGY);
		for( int i = 0; i < numCars; i++ ) {
			cars.addContent( buildCar(strategies, i) );
		}
		return cars;
	}

	/**
	 * @param strategies
	 * @param i
	 * @return
	 */
	private Element buildCar(List<Class<CarStrategy>> strategies, int i) {
		Element car = factory.element( "car" );
		car.setAttribute( "id", "" + i );
		int s = randomNode();
		car.setAttribute( "start", "" + s );
		car.setAttribute( "end", "" + randomNode( s ) );
		car.setAttribute( "strategy", randomStrategy( strategies ) );
		return car;
	}

	/**
	 * @param strategies
	 * @return
	 */
	private String randomStrategy(List<Class<CarStrategy>> strategies) {
		return "";
	}

	/**
	 * @param s
	 * @return
	 */
	private int randomNode(int s) {
		//TODO: this can cause an infinite loop if there is only one node in the system!!!!!
		int n;
		do {
			n = randomNode();
		} while ( n == s );
		return n;
	}

	/**
	 * @return
	 */
	private int randomNode() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @param numNodes
	 * @param nodeDelay
	 * @param degreeProb
	 * @return
	 */
	Content buildGraph(int numNodes, int[] nodeDelay, double degreeProb) {
		Element graph = factory.element( "graph" );
		graph.setAttribute( "default_strategy", DEFAULT_SPEED_STRATEGY);
		for( int i = 0; i < numNodes; i++ ) {
			graph.addContent( buildNode(i) );
		}
		return graph;
	}

	/**
	 * @param graph
	 * @param i
	 */
	private Element buildNode( int i) {
		Element node = factory.element( "node" );
		node.setAttribute( "id", "" + i );
		node.setAttribute( "neighbors", randomizeNeighbors() );
		return node;
	}

	/**
	 * @return
	 */
	private String randomizeNeighbors() {
		return "";
	}

}
