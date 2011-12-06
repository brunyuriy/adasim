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
 * Created: Dec 6, 2011
 */

package traffic.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import traffic.graph.Graph;
import traffic.graph.GraphNode;
import traffic.strategy.CarStrategy;
import traffic.strategy.SpeedStrategy;


/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
final public class SimulationFactory {

	private static final Logger logger = Logger.getLogger(SimulationFactory.class);


	private Document doc;

	SimulationFactory( String config ) throws JDOMException, IOException {
		SAXBuilder b = new SAXBuilder(false	);
		doc = b.build( new StringReader( config ) );
	}

	private SimulationFactory( File f ) throws JDOMException, IOException {
		SAXBuilder b = new SAXBuilder(false);
		doc = b.build( f );
	}
	
	public static TrafficSimulator buildSimulator( String xmlString ) {
		return null;
	}

	public static TrafficSimulator buildSimulator( File config ) throws FileNotFoundException, ConfigurationException {
		try {
			SimulationFactory factory = new SimulationFactory(config);
			TrafficSimulator sim = new TrafficSimulator( factory.buildGraph(), factory.buildCars() );
			return sim;
		} catch ( JDOMException e ) {
			buildError( e );
		} catch ( FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			buildError( e );
		} catch ( IllegalArgumentException e ) {
			buildError( e );
		} catch ( ConfigurationException e ) {
			buildError(e);
			throw e;
		}
		return null;
	}

	/**
	 * @param e
	 */
	private static void buildError(Exception e) {
		logger.error( "Problem parsing configuration file: " + e.getMessage() );
	}

	/**
	 * @return
	 */
	private Graph buildGraph() {
		try {
			Element root = doc.getRootElement();
			List<Element> graphChild = root.getChildren("graph");
			Element graphList = graphChild.get(0);
			List<Element> children = graphList.getChildren("node");		
			//TODO: check for missing strategy
			Class<?> cls = Class.forName(graphList.getAttributeValue("default_strategy"));
			SpeedStrategy ss = (SpeedStrategy) cls.newInstance();
			return new Graph( buildNodes( children, ss ) );
		} catch (Exception e) {
			logger.error("Bad config file");
			return null;
		}
	}
	
	private List<GraphNode> buildNodes( List<Element> nodeDeclarations, SpeedStrategy defaultStrategy ) {
		List<GraphNode> nodes = new ArrayList<GraphNode>( nodeDeclarations.size() );
		for( Element node : nodeDeclarations ) {
			int id = Integer.parseInt( node.getAttributeValue( "id" ) );
			SpeedStrategy ss = buildStrategy( node, defaultStrategy );
			nodes.add( new GraphNode( id, ss) );
		}
		for ( Element node: nodeDeclarations ) {
			buildNeigbors(nodes, node );
		}
		return nodes;		
	}

	/**
	 * @param nodes
	 * @param node
	 */
	private void buildNeigbors(List<GraphNode> nodes, Element node) {
		String[] neighbors = node.getAttributeValue("neighbors").split(" ");
		GraphNode gn = getNode( nodes, node );
		for ( String n : neighbors ) {
			//TODO: deal with edges to non-existing nodes
			gn.addEdge(Integer.parseInt(n));					
		}
	}

	/**
	 * @param nodes
	 * @param node
	 * @return
	 */
	private GraphNode getNode(List<GraphNode> nodes, Element node) {
		int id = Integer.parseInt( node.getAttributeValue( "id" ) );
		for ( GraphNode n : nodes ) {
			if ( n.getID() == id ) return n;
		}
		return null;
	}

	/**
	 * @param node
	 * @param defaultStrategy
	 * @return
	 */
	private SpeedStrategy buildStrategy(Element node,
			SpeedStrategy defaultStrategy) {
		SpeedStrategy ss = null;
		String ssn = node.getAttributeValue( "strategy" );
		if ( ssn != null ) {
			try {
				@SuppressWarnings("rawtypes")
				Class ssc = Class.forName( ssn );
				ss = (SpeedStrategy) ssc.newInstance();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return ( ss == null? defaultStrategy : ss );
	}

	/**
	 * @return
	 * @throws ConfigurationException 
	 */
	private List<Car> buildCars() throws ConfigurationException {
		try {
			Element root = doc.getRootElement();
			Element carChild = root.getChild("cars");
			if ( carChild == null ) throw new ConfigurationException( "No <cars> declaration found." );
			//Element carList = carChild.get(0);
			@SuppressWarnings("unchecked")
			List<Element> carNodes = carChild.getChildren("car");
			if ( carNodes.size() < 1 ) throw new ConfigurationException( "Simulation must have at least one <car>" );
			Class<?> cls = Class.forName(carChild.getAttributeValue("default_strategy"));
			CarStrategy cs = (CarStrategy) cls.newInstance();
			//TODO: deal with invalid default strategies
			List<Car> cars = new ArrayList<Car>();
			for ( Element car : carNodes ) {
				cars.add( buildCar( car, cs ) );
			}
			return cars;
		} catch (ConfigurationException e ) {
			throw e;
		} catch (Exception e) {
			logger.error("Bad config file");
			return null;
		}	
	}

	/**
	 * @param car
	 * @return
	 */
	private Car buildCar(Element car, CarStrategy defaultStrategy ) {
		try {
			if(car.getAttributeValue("strategy") == null) {
					return new Car(Integer.parseInt(car.getAttributeValue("start")), 
							Integer.parseInt(car.getAttributeValue("end")),
							defaultStrategy, Integer.parseInt(car.getAttributeValue("id")));
			} else {
				return new Car(Integer.parseInt(car.getAttributeValue("start")), 
						Integer.parseInt(car.getAttributeValue("end")),
						(CarStrategy) Class.forName(car.getAttributeValue("strategy")).newInstance() ,
						Integer.parseInt(car.getAttributeValue("id")));
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
