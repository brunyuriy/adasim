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
			Graph g = factory.buildGraph();
			TrafficSimulator sim = new TrafficSimulator( g, factory.buildCars(g) );
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
	 * @throws ConfigurationException 
	 */
	private Graph buildGraph() throws ConfigurationException {
		try {
			Element root = doc.getRootElement();
			Element graph = root.getChild("graph");
			if ( graph == null ) throw new ConfigurationException( "No <graph> found.");
			@SuppressWarnings("unchecked")
			List<Element> children = graph.getChildren("node");		
			if ( children.size() < 1 ) throw new ConfigurationException( "Simulation must have at least one <node>" );
			//TODO: check for missing strategy
			Class<?> cls = Class.forName(graph.getAttributeValue("default_strategy"));
			SpeedStrategy ss = (SpeedStrategy) cls.newInstance();
			return new Graph( buildNodes( children, ss ) );
		} catch ( ConfigurationException e ) {
			throw e;
		} catch (Exception e) {
			logger.error("Bad config file");
			return null;
		}
	}
	
	private List<GraphNode> buildNodes( List<Element> nodeDeclarations, SpeedStrategy defaultStrategy ) {
		List<GraphNode> nodes = new ArrayList<GraphNode>( nodeDeclarations.size() );
		for( Element node : nodeDeclarations ) {
			int id = Integer.parseInt( node.getAttributeValue( "id" ) );
			if ( hasValidNeighbors(node) ) {
				SpeedStrategy ss = buildStrategy( node, defaultStrategy );
				nodes.add( new GraphNode( id, ss, getDelay(node )) );
			}
		}
		for ( Element node: nodeDeclarations ) {
			if ( hasValidNeighbors(node) ) {
				buildNeigbors(nodes, node );
			}
		}
		nodes = validate(nodes);
		return nodes;		
	}
	
	/**
	 * @param node
	 * @return
	 * @throws ConfigurationException 
	 */
	private int getDelay(Element node) {
		String d = node.getAttributeValue("delay");
		if ( d == null ) return 1;
		else {
			try {
				int i = Integer.parseInt(d);
				if ( i < 1 ) {
					logger.warn( "Delay on node " + node.getAttribute("id") + " must be > 0 " );
					return 1;
				}
				return i;
			} catch (NumberFormatException e ) {
				logger.warn( "Delay on node " + node.getAttribute("id") + " is not an integer. Defaulting to 1." );
				return 1;
			}
		}
	}

	/**
	 * @param nodes
	 * @return
	 */
	private List<GraphNode> validate(List<GraphNode> nodes) {
		List<GraphNode> l = new ArrayList<GraphNode>();
		for ( GraphNode node : nodes ) {
			List<GraphNode> remaining = new ArrayList<GraphNode>();
			for ( GraphNode i : node.getNeighbors() ) {
				if ( nodes.contains( i ) ) {
					remaining.add( i );
				} else {
					node.removeEdge(i);
				}
			}
			if ( remaining.size() > 0 ) {
				l.add( node );
			}
		}
		return l;
	}

	private boolean hasValidNeighbors( Element node ) {
		String n = node.getAttributeValue("neighbors").trim();
		return !( n.equals("") );
	}

	/**
	 * @param nodes
	 * @param node
	 */
	private void buildNeigbors(List<GraphNode> nodes, Element node) {
		String[] neighbors = node.getAttributeValue("neighbors").split(" ");
		GraphNode gn = getNode( nodes, node );
		for ( String n : neighbors ) {
			int nn = Integer.parseInt(n);
			gn.addEdge( getNode( nodes, nn ));					
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

	private GraphNode getNode(List<GraphNode> nodes, int node) {
		for ( GraphNode n : nodes ) {
			if ( n.getID() == node ) return n;
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
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
			
		}
		return ( ss == null? defaultStrategy : ss );
	}

	/**
	 * @return
	 * @throws ConfigurationException 
	 */
	private List<Car> buildCars( Graph g ) throws ConfigurationException {
		try {
			Element root = doc.getRootElement();
			Element carChild = root.getChild("cars");
			if ( carChild == null ) throw new ConfigurationException( "No <cars> declaration found." );
			@SuppressWarnings("unchecked")
			List<Element> carNodes = carChild.getChildren("car");
			if ( carNodes.size() < 1 ) throw new ConfigurationException( "Simulation must have at least one <car>" );
			CarStrategy cs = null;
			try {
				Class<?> cls = Class.forName(carChild.getAttributeValue("default_strategy"));
				cs = (CarStrategy) cls.newInstance();
				cs.setGraph(g);
			} catch (Exception e ) {
				throw new ConfigurationException( e.getMessage() );
			}
			List<Car> cars = new ArrayList<Car>();
			for ( Element car : carNodes ) {
				Car c = buildCar( car, cs, g );
				if ( c != null ) cars.add( c );
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
	 * @throws ConfigurationException 
	 */
	private Car buildCar(Element car, CarStrategy defaultStrategy, Graph g ) throws ConfigurationException {
		try {
			int start = Integer.parseInt(car.getAttributeValue("start"));
			int end = Integer.parseInt(car.getAttributeValue("end"));
			int id = Integer.parseInt(car.getAttributeValue("id"));
			List<GraphNode> nodes = g.getNodes();
			
			try {
				checkEndPoint(nodes, start, id, "Start" );
				checkEndPoint(nodes, end, id, "End" );
			} catch ( ConfigurationException e ) {
				return null;
			}
			
			CarStrategy cs = defaultStrategy;
			if(car.getAttributeValue("strategy") != null) {
				String s = null;
				try {
					s = car.getAttributeValue("strategy");
					cs = (CarStrategy) Class.forName(s).newInstance();
					cs.setGraph(g);
				} catch (Exception e) {
					logger.warn( "CarStrategy " + s + " not found. Using default." );
					cs = defaultStrategy;
				}
			}
			return new Car(start, end, cs, id );
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}

	/**
	 * @param nodes
	 * @param end
	 * @param id
	 * @throws ConfigurationException 
	 */
	private void checkEndPoint(List<GraphNode> nodes, int end, int id, String s) throws ConfigurationException {
		if ( !isValidNode( end, nodes ) ) { 
			logger.warn( s + " node " + end + " for car " + id + " does not exist");
			throw new ConfigurationException("");
		}
	}

	/**
	 * @param end
	 */
	private boolean isValidNode(int id, List<GraphNode> nodes ) {
		for ( GraphNode node : nodes ) {
			if ( node.getID() == id ) return true;
		}
		return false;
	}
}
