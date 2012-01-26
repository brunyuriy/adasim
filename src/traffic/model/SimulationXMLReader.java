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
 * Reads in an XML configuration file, validates it agains the Adasim schema
 * and constructs a simulation.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
final public class SimulationXMLReader {

	private static final Logger logger = Logger.getLogger(SimulationXMLReader.class);

	private Document doc;
	private static SimulationXMLBuilder builder;

	private SimulationXMLReader( File f ) throws JDOMException, IOException, ConfigurationException {
		builder = new SimulationXMLBuilder();
		SAXBuilder sbuilder = new SAXBuilder(true);
		sbuilder.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
		sbuilder.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", "/Users/wuttke/Documents/uni/research/UW-SNF/workspace/TrafficSim/resources/xml/adasim.xsd");
		sbuilder.setErrorHandler(new SimpleErrorHandler());

		try {
			doc = sbuilder.build(f);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}

	/**
	 * The main interface to {@link SimulationXMLReader}. It will load all necessary files
	 * and either return a complete and valid {@link TrafficSimulator}, or it will throw
	 * and exception detailing the cause of failure.
	 * 
	 * @param config
	 * @return
	 * @throws FileNotFoundException
	 * @throws ConfigurationException
	 */
	public static TrafficSimulator buildSimulator( File config ) throws FileNotFoundException, ConfigurationException {
		try {
			SimulationXMLReader factory = new SimulationXMLReader(config);
			Graph g = builder.buildGraph( factory.doc.getRootElement().getChild("graph" ) );
			TrafficSimulator sim = new TrafficSimulator( g, factory.buildCars( factory.doc.getRootElement().getChild("cars" ), g ) );
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
	 *Hacked this method. <code>public</code> is not proper, but allows
	 *the Generator access to this.
	 * @return
	 * @throws ConfigurationException 
	 */
	//	public Graph buildGraph( Element graphNode ) throws ConfigurationException {
	//		try {
	//			@SuppressWarnings("unchecked")
	//			List<Element> children = graphNode.getChildren("node");		
	//			Class<?> cls = Class.forName(graphNode.getAttributeValue("default_strategy"));
	//			SpeedStrategy ss = (SpeedStrategy) cls.newInstance();
	//			int capacity = Integer.parseInt(graphNode.getAttributeValue("default_capacity"));
	//			return new Graph( buildNodes( children, ss, capacity) );
	//		} catch (Exception e) {
	//			throw new ConfigurationException("Invalid default graph strategy");
	//		}
	//	}

	//	private List<GraphNode> buildNodes( List<Element> nodeDeclarations, SpeedStrategy defaultStrategy, int capacity) {
	//		List<GraphNode> nodes = new ArrayList<GraphNode>( nodeDeclarations.size() );
	//		for( Element node : nodeDeclarations ) {
	//			int id = Integer.parseInt( node.getAttributeValue( "id" ) );
	//			if ( hasValidNeighbors(node) ) {
	//				SpeedStrategy ss = buildStrategy( node, defaultStrategy );
	//				nodes.add( new GraphNode( id, ss, getDelay(node ), getCapacity(node, capacity)) );
	//			}
	//		}
	//		for ( Element node: nodeDeclarations ) {
	//			if ( hasValidNeighbors(node) ) {
	//				buildNeigbors(nodes, node );
	//			}
	//		}
	//		nodes = validate(nodes);
	//		return nodes;		
	//	}

	//	/**
	//	 * @param node
	//	 * @return
	//	 * @throws ConfigurationException 
	//	 */
	//	private int getDelay(Element node) {
	//		String d = node.getAttributeValue("delay");
	//		if ( d == null ) return 1;
	//		else {
	//			return Integer.parseInt(d); //Delay must be a valid integer due to schema
	//		}
	//	}

	//	private int getCapacity(Element node, int d_capacity) {
	//		String cap = node.getAttributeValue("capacity");
	//		if (cap == null) {
	//			return d_capacity;
	//		} else {
	//			return Integer.parseInt(cap);
	//		}
	//	}

	//	/**
	//	 * @param nodes
	//	 * @return
	//	 */
	//	private List<GraphNode> validate(List<GraphNode> nodes) {
	//		List<GraphNode> l = new ArrayList<GraphNode>( nodes );
	//		List<GraphNode> last;
	//		do {
	//			last = l;
	//			l = reduce( last );
	//		} while ( ! last.equals(l) );
	//		return l;
	//	}

	//	/**
	//	 * Removes nodes that have no neighbors, and removes links that point to invalid neighbors.
	//	 * @param nodes
	//	 * @return
	//	 */
	//	private List<GraphNode> reduce(List<GraphNode> nodes) {
	//		 List<GraphNode> l = new ArrayList<GraphNode>();
	//		for ( GraphNode node : nodes ) {
	//			boolean hasNeighbor = false;
	//			for ( GraphNode i : node.getNeighbors() ) {
	//				if ( nodes.contains( i ) ) {
	//					hasNeighbor = true;
	//				} else {
	//					node.removeEdge(i);
	//				}
	//			}
	//			if ( hasNeighbor ) {
	//				l.add( node );
	//			}
	//		}
	//		return l;
	//	}

	//	private boolean hasValidNeighbors( Element node ) {
	//		String n = node.getAttributeValue("neighbors").trim();
	//		return !( n.equals("") );
	//	}

	//	/**
	//	 * @param nodes
	//	 * @param node
	//	 */
	//	private void buildNeigbors(List<GraphNode> nodes, Element node) {
	//		String[] neighbors = node.getAttributeValue("neighbors").split(" ");
	//		GraphNode gn = getNode( nodes, node );
	//		for ( String n : neighbors ) {
	//			int nn = Integer.parseInt(n);
	//			gn.addEdge( getNode( nodes, nn ));					
	//		}
	//	}

	//	/**
	//	 * @param nodes
	//	 * @param node
	//	 * @return
	//	 */
	//	private GraphNode getNode(List<GraphNode> nodes, Element node) {
	//		int id = Integer.parseInt( node.getAttributeValue( "id" ) );
	//		for ( GraphNode n : nodes ) {
	//			if ( n.getID() == id ) return n;
	//		}
	//		return null;
	//	}

	//	private GraphNode getNode(List<GraphNode> nodes, int node) {
	//		for ( GraphNode n : nodes ) {
	//			if ( n.getID() == node ) return n;
	//		}
	//		return null;
	//	}

	//	/**
	//	 * @param node
	//	 * @param defaultStrategy
	//	 * @return
	//	 */
	//	private SpeedStrategy buildStrategy(Element node,
	//			SpeedStrategy defaultStrategy) {
	//		SpeedStrategy ss = null;
	//		String ssn = node.getAttributeValue( "strategy" );
	//		if ( ssn != null ) {
	//			try {
	//				@SuppressWarnings("rawtypes")
	//				Class ssc = Class.forName( ssn );
	//				ss = (SpeedStrategy) ssc.newInstance();
	//			} catch (Exception e) {
	//			}
	//		}
	//		return ( ss == null? defaultStrategy : ss );
	//	}

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
	 * @return
	 * @throws ConfigurationException 
	 */
	private List<Car> buildCars( Element carsNode, Graph g ) throws ConfigurationException {
		List<Car> cars = builder.buildCars(carsNode);
		List<Car> l = new ArrayList<Car>();
		//TODO: validate cars against graph and translate back to regular cars
		@SuppressWarnings("unchecked")
		List<Element> carNodes = carsNode.getChildren( "car" );
		for ( Element car : carNodes ) {
			Car c = validateCar(car, cars, g );
			if ( c != null ) {
				l.add(c);
			}
		}
		return l;	
	}

	//	/**
	//	 * @param car
	//	 * @return
	//	 * @throws ConfigurationException 
	//	 */
	//	private Car buildCar(Element car, CarStrategy defaultStrategy, Graph g ) throws ConfigurationException {
	//        int start = Integer.parseInt(car.getAttributeValue("start"));
	//		int end = Integer.parseInt(car.getAttributeValue("end"));
	//		int id = Integer.parseInt(car.getAttributeValue("id"));
	//		List<GraphNode> nodes = g.getNodes();
	//		try {
	//			checkEndPoint(nodes, start, id, "Start" );
	//			checkEndPoint(nodes, end, id, "End" );
	//		} catch ( ConfigurationException e ) {
	//			return null;
	//		}
	//		CarStrategy cs = defaultStrategy;
	//		if(car.getAttributeValue("strategy") != null) {
	//			String s = null;
	//			try {
	//				s = car.getAttributeValue("strategy");
	//				cs = (CarStrategy) Class.forName(s).newInstance();
	//				cs.setGraph(g);
	//			} catch (Exception e) {
	//				logger.warn( "CarStrategy " + s + " not found. Using default." );
	//				cs = defaultStrategy;
	//			}
	//		}
	//		return new Car( g.getNode(start), g.getNode(end), cs, id );
	//	}

	/**
	 * @param car
	 * @param g
	 */
	private Car validateCar(Element car, List<Car> cars, Graph g) {
		int start = Integer.parseInt(car.getAttributeValue("start"));
		int end = Integer.parseInt(car.getAttributeValue("end"));
		int id = Integer.parseInt(car.getAttributeValue("id"));

		List<GraphNode> nodes = g.getNodes();
		try {
			Car c = getCar( id, cars );
			GraphNode node = checkEndPoint(nodes, start, id, "Start" );
			c.getInfo().setStartNode(node);
			node = checkEndPoint(nodes, end, id, "End" );
			c.getInfo().setEndNode(node);
			return c;
		} catch ( ConfigurationException e ) {
			return null;
		}
	}

	/**
	 * @param id
	 * @param cars
	 * @return
	 */
	private Car getCar(int id, List<Car> cars) {
		for ( Car c : cars ) {
			if ( c.getID() == id ) return c;
		}
		return null;
	}

	/**
	 * @param nodes
	 * @param end
	 * @param id
	 * @throws ConfigurationException 
	 */
	private GraphNode checkEndPoint(List<GraphNode> nodes, int end, int id, String s) throws ConfigurationException {
		GraphNode n = isValidNode( end, nodes );
		if ( n == null ) { 
			logger.warn( s + " node " + end + " for car " + id + " does not exist");
			throw new ConfigurationException("");
		}
		return n;
	}

	/**
	 * @param end
	 */
	private GraphNode isValidNode(int id, List<GraphNode> nodes ) {
		for ( GraphNode node : nodes ) {
			if ( node.getID() == id ) return node;
		}
		return null;
	}

}
