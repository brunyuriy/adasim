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
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
final public class SimulationFactory {

	private static final Logger logger = Logger.getLogger(SimulationFactory.class);


	private Document doc;

	private SimulationFactory( String config ) {
		throw new RuntimeException( "method not implemented" );
	}

	private SimulationFactory( File f ) throws JDOMException, IOException {
		SAXBuilder b = new SAXBuilder(false);
		doc = b.build( f );
	}
	
	public static TrafficSimulator buildSimulator( String xmlString ) {
		return null;
	}

	public static TrafficSimulator buildSimulator( File config ) throws JDOMException, IOException {
		SimulationFactory factory = new SimulationFactory(config);
		return new TrafficSimulator( factory.buildGraph(), factory.buildCars() );
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
	 */
	private List<Car> buildCars() {
		try {
			Element root = doc.getRootElement();
			List<Element> carChild = root.getChildren("cars");
			Element carList = carChild.get(0);
			List<Element> children = carList.getChildren("car");
			int size = children.size();
			List<Car> cars = new ArrayList<Car>();
			for(int i = 0; i < size; i++) {
				Class<?> cls = Class.forName(carList.getAttributeValue("default_strategy"));
				CarStrategy cs = (CarStrategy) cls.newInstance();
				if(children.get(i).getAttributeValue("strategy") == null) {
					cars.add(new Car(Integer.parseInt(children.get(i).getAttributeValue("start")), 
							Integer.parseInt(children.get(i).getAttributeValue("end")),
							cs, Integer.parseInt(children.get(i).getAttributeValue("id"))));
				} else {
					cars.add(new Car(Integer.parseInt(children.get(i).getAttributeValue("start")), 
							Integer.parseInt(children.get(i).getAttributeValue("end")),
							(CarStrategy) Class.forName(children.get(i).getAttributeValue("strategy")).newInstance() ,
							Integer.parseInt(children.get(i).getAttributeValue("id"))));
				}
			}
			return cars;
		} catch (Exception e) {
			logger.error("Bad config file");
			return null;
		}	
	}

}
