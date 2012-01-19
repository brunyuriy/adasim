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
 * Created: Jan 18, 2012
 */

package traffic.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;

import traffic.graph.Graph;
import traffic.graph.GraphNode;
import traffic.strategy.CarStrategy;
import traffic.strategy.SpeedStrategy;

/**
 * A builder that constructs elements in a traffic simulation 
 * from their corresponding XML nodes.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class SimulationBuilder {
	
	private static final Logger logger = Logger.getLogger(SimulationBuilder.class);


	public Graph buildGraph( Element graphNode ) throws ConfigurationException {
		try {
			@SuppressWarnings("unchecked")
			List<Element> children = graphNode.getChildren("node");		
			Class<?> cls = Class.forName(graphNode.getAttributeValue("default_strategy"));
			SpeedStrategy ss = (SpeedStrategy) cls.newInstance();
			int capacity = Integer.parseInt(graphNode.getAttributeValue("default_capacity"));
			return new Graph( buildNodes( children, ss, capacity) );
		} catch (Exception e) {
			throw new ConfigurationException("Invalid default graph strategy");
		}
	}

	public GraphNode buildNode( Element nodeElement ) {
		int id = Integer.parseInt( nodeElement.getAttributeValue( "id" ) );
		if ( hasValidNeighbors(nodeElement) ) {
			SpeedStrategy ss = buildStrategy( nodeElement );
			return new GraphNode( id, ss, getDelay(nodeElement ), getCapacity(nodeElement)) ;
		}
		return null;
	}
	
	public List<Car> buildCars( Element carsNode ) throws ConfigurationException {
		List<Element> carNodes = carsNode.getChildren("car");
		CarStrategy cs = null;
		try {
			Class<?> cls = Class.forName(carsNode.getAttributeValue("default_strategy"));
			cs = (CarStrategy) cls.newInstance();
			//cs.setGraph(g);
		} catch (Exception e ) {
			throw new ConfigurationException("Invalid default car strategy");
		}
		List<Car> cars = new ArrayList<Car>();
		for ( Element car : carNodes ) {
			//Car c = buildCar( car, cs, g );
			Car c = buildCar( car );
			if ( c != null ) cars.add( c );
		}
		return cars;	
	}
	
	public Car buildCar( Element carNode ) {
        int start = Integer.parseInt(carNode.getAttributeValue("start"));
		int end = Integer.parseInt(carNode.getAttributeValue("end"));
		int id = Integer.parseInt(carNode.getAttributeValue("id"));
		//TODO: move validation somewhere else!!!!
		
//		List<GraphNode> nodes = g.getNodes();
//		try {
//			checkEndPoint(nodes, start, id, "Start" );
//			checkEndPoint(nodes, end, id, "End" );
//		} catch ( ConfigurationException e ) {
//			return null;
//		}
		//CarStrategy cs = defaultStrategy;
		if(carNode.getAttributeValue("strategy") != null) {
			String s = null;
			try {
				s = carNode.getAttributeValue("strategy");
				CarStrategy cs = (CarStrategy) Class.forName(s).newInstance();
				
				//TODO: link the graph somewhere else
				//cs.setGraph(g);
			} catch (Exception e) {
				logger.warn( "CarStrategy " + s + " not found. Using default." );
				//cs = defaultStrategy;
			}
		}
		//return new Car( g.getNode(start), g.getNode(end), cs, id );
		return new InternalCar( id, start, end );
	}
	
	/**
		 * This method is private, as it does not deal with a single XML element, but connects several.
		 * 
		 * @param nodes
		 * @param defaultStrategy
		 * @param capacity
		 * @return
		 */
		private List<GraphNode> buildNodes( List<Element> nodeElements, SpeedStrategy defaultStrategy, int capacity ) {
			List<GraphNode> nodes = new ArrayList<GraphNode>( nodeElements.size() );
			for( Element node : nodeElements ) {
				GraphNode gn = buildNode( node );
				if ( gn != null ) {
					//TODO: check for defaults
					nodes.add(gn);
				}
	//			int id = Integer.parseInt( node.getAttributeValue( "id" ) );
	//			if ( hasValidNeighbors(node) ) {
	//				SpeedStrategy ss = buildStrategy( node, defaultStrategy );
	//				nodes.add( new GraphNode( id, ss, getDelay(node ), getCapacity(node, capacity)) );
	//			}
			}
			for ( Element node: nodeElements ) {
				if ( hasValidNeighbors(node) ) {
					buildNeigbors(nodes, node );
				}
			}
			nodes = validate(nodes);
			return nodes;		
		}

	private boolean hasValidNeighbors( Element node ) {
		String n = node.getAttributeValue("neighbors").trim();
		return !( n.equals("") );
	}

	/**
	 * @param node
	 * @param defaultStrategy
	 * @return
	 */
	private SpeedStrategy buildStrategy(Element node) {
		SpeedStrategy ss = null;
		String ssn = node.getAttributeValue( "strategy" );
		if ( ssn != null ) {
			try {
				@SuppressWarnings("rawtypes")
				Class ssc = Class.forName( ssn );
				ss = (SpeedStrategy) ssc.newInstance();
			} catch (Exception e) {
			}
		}
		return ss;
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
			return Integer.parseInt(d); //Delay must be a valid integer due to schema
		}
	}
	
	private int getCapacity(Element node) {
		String cap = node.getAttributeValue("capacity");
		if (cap == null) {
			return -1;
		} else {
			return Integer.parseInt(cap);
		}
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
	
	private GraphNode getNode(List<GraphNode> nodes, int node) {
		for ( GraphNode n : nodes ) {
			if ( n.getID() == node ) return n;
		}
		return null;
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
	 * @param nodes
	 * @return
	 */
	private List<GraphNode> validate(List<GraphNode> nodes) {
		List<GraphNode> l = new ArrayList<GraphNode>( nodes );
		List<GraphNode> last;
		do {
			last = l;
			l = reduce( last );
		} while ( ! last.equals(l) );
		return l;
	}
	
	/**
	 * Removes nodes that have no neighbors, and removes links that point to invalid neighbors.
	 * @param nodes
	 * @return
	 */
	private List<GraphNode> reduce(List<GraphNode> nodes) {
		 List<GraphNode> l = new ArrayList<GraphNode>();
		for ( GraphNode node : nodes ) {
			boolean hasNeighbor = false;
			for ( GraphNode i : node.getNeighbors() ) {
				if ( nodes.contains( i ) ) {
					hasNeighbor = true;
				} else {
					node.removeEdge(i);
				}
			}
			if ( hasNeighbor ) {
				l.add( node );
			}
		}
		return l;
	}
}
