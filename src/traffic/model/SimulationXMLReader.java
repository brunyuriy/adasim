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

	private SimulationXMLReader( File f ) throws FileNotFoundException, ConfigurationException {
		builder = new SimulationXMLBuilder();
		SAXBuilder sbuilder = new SAXBuilder(true);
		sbuilder.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
		sbuilder.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", "/Users/Jonathan/workspace/TrafficSim/resources/xml/adasim.xsd");
		sbuilder.setErrorHandler(new SimpleErrorHandler());

		try {
			doc = sbuilder.build(f);
		} catch ( FileNotFoundException e) {
			throw e;
		} catch (JDOMException e) {
			throw new ConfigurationException(e);
		} catch (IOException e) {
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
			TrafficSimulator sim = new TrafficSimulator( g, factory.buildVehicles( factory.doc.getRootElement().getChild("vehicles" ), g ) );
			return sim;
		} catch ( ConfigurationException e ) {
			buildError(e);
			throw e;
		}
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
	private List<Vehicle> buildVehicles( Element vehiclesNode, Graph g ) throws ConfigurationException {
		List<Vehicle> vehicles = builder.buildVehicles(vehiclesNode);
		List<Vehicle> l = new ArrayList<Vehicle>();
		@SuppressWarnings("unchecked")
		List<Element> vehicleNodes = vehiclesNode.getChildren( "vehicle" );
		for ( Element vehicle : vehicleNodes ) {
			Vehicle c = validateVehicle(vehicle, vehicles, g );
			if ( c != null ) {
				l.add(c);
			}
		}
		return l;	
	}

	/**
	 * @param vehicle
	 * @param g
	 */
	private Vehicle validateVehicle(Element vehicle, List<Vehicle> vehicles, Graph g) {
		int start = Integer.parseInt(vehicle.getAttributeValue("start"));
		int end = Integer.parseInt(vehicle.getAttributeValue("end"));
		int id = Integer.parseInt(vehicle.getAttributeValue("id"));

		List<GraphNode> nodes = g.getNodes();
		try {
			Vehicle c = getVehicle( id, vehicles );
			GraphNode node = checkEndPoint(nodes, start, id, "Start" );
			c.setStartNode(node);
			node = checkEndPoint(nodes, end, id, "End" );
			c.setEndNode(node);
			c.getStrategy().setGraph(g);
			return c;
		} catch ( ConfigurationException e ) {
			return null;
		}
	}

	/**
	 * @param id
	 * @param vehicles
	 * @return
	 */
	private Vehicle getVehicle(int id, List<Vehicle> vehicles) {
		for ( Vehicle c : vehicles ) {
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
			logger.warn( s + " node " + end + " for vehicle " + id + " does not exist");
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
