/*******************************************************************************
 * Copyright (C) 2011 - 2012 Jochen Wuttke, Jonathan Ramaswamy
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Contributors:
 *    Jochen Wuttke (wuttkej@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Dec 6, 2011
 */

package adasim.model.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import adasim.agent.AdasimAgent;
import adasim.model.AdasimMap;
import adasim.model.ConfigurationException;
import adasim.model.RoadSegment;
import adasim.model.TrafficSimulator;
import adasim.model.Vehicle;
import adasim.util.ReflectionException;
import adasim.util.ReflectionUtils;



/**
 * Reads in an XML configuration file, validates it against the Adasim schema
 * and constructs a simulation.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
final public class SimulationXMLReader {

	private static final Logger logger = Logger.getLogger(SimulationXMLReader.class);

	private Document doc;
	private static SimulationXMLBuilder builder;

	private SimulationXMLReader( File f ) throws ConfigurationException {
		builder = new SimulationXMLBuilder();
		SAXBuilder sbuilder = new SAXBuilder(true);
		sbuilder.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
		try {
			URL res = SimulationXMLReader.class.getClassLoader().getResource("resources/xml/adasim.xsd");
			if (res == null ) {
				throw new ConfigurationException( "XML Schema adasim.xsd not found on classpath" );
			}
			sbuilder.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", res.openStream() );
			sbuilder.setErrorHandler(new SimpleErrorHandler());
			doc = sbuilder.build(f);
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
	 * @return the configured instance of TrafficSimulator
	 * @throws FileNotFoundException
	 * @throws ConfigurationException
	 */
	public static TrafficSimulator buildSimulator( File config ) throws FileNotFoundException, ConfigurationException {
		try {
			SimulationXMLReader factory = new SimulationXMLReader(config);
			Element defaults = factory.doc.getRootElement().getChild("defaults");
			FilterMap fm = builder.buildFilters( (defaults == null? null : defaults.getChild("filters") ), new FilterMap(), Object.class );
			AdasimMap g = builder.buildGraph( factory.doc.getRootElement().getChild("graph" ), fm );
			VehicleManager m = new VehicleManager();
			TrafficSimulator sim = new TrafficSimulator( g, m, factory.allAgents( g, m, fm ) ); 
			return sim;
		} catch ( ConfigurationException e ) {
			buildError(e);
			throw e;
		}
	}

	/**
	 * @throws ConfigurationException 
	 */
	private List<AdasimAgent> allAgents( AdasimMap g, VehicleManager m, FilterMap defaultFilters ) throws ConfigurationException {
		List<AdasimAgent> agents;
		agents = new ArrayList<AdasimAgent>(buildVehicles( doc.getRootElement().getChild("cars" ), g, m ) );
		agents.addAll( builder.buildAgents( doc.getRootElement().getChild("agents" ), defaultFilters ) );
		return agents;
	}
	
	/**
	 * @param e
	 */
	private static void buildError(Exception e) {
		logger.error( "Problem parsing configuration file: " + e.getMessage() );
	}

	/**
	 * @throws ConfigurationException 
	 */
	private List<AdasimAgent> buildVehicles( Element vehiclesNode, AdasimMap g, VehicleManager m ) throws ConfigurationException {
		List<Vehicle> vehicles = builder.buildVehicles(vehiclesNode);
		List<AdasimAgent> l = new ArrayList<AdasimAgent>();
		@SuppressWarnings("unchecked")
		List<Element> vehicleNodes = vehiclesNode.getChildren( "car" );
		for ( Element vehicle : vehicleNodes ) {
			Vehicle c = validateVehicle(vehicle, vehicles, g );
			long time = Long.parseLong( vehicle.getAttributeValue("start_time") );
			if ( c != null ) {
				if ( time == 1 ) {
					l.add(c);
					//add valid vehicle to their start node
					try {
						((RoadSegment) ReflectionUtils.getProperty( c, "getStartNode")).enterNode(c);
					} catch (NoSuchMethodException e) {
						//this should never happen
						e.printStackTrace();
					} catch (ReflectionException e) {
						//this should never happen
						e.printStackTrace();
					}
				} else {
					m.addVehicle(c, time);
				}
			}
		}
		return l;	
	}

	/**
	 * @param vehicle
	 * @param g
	 */
	private Vehicle validateVehicle(Element vehicle, List<Vehicle> vehicles, AdasimMap g) {
		int start = Integer.parseInt(vehicle.getAttributeValue("start"));
		int end = Integer.parseInt(vehicle.getAttributeValue("end"));
		int id = Integer.parseInt(vehicle.getAttributeValue("id"));

		List<RoadSegment> nodes = g.getRoadSegments();
		try {
			Vehicle c = getVehicle( id, vehicles );
			RoadSegment node = checkEndPoint(nodes, start, id, "Start" );
			c.setStartNode(node);
			node = checkEndPoint(nodes, end, id, "End" );
			c.setEndNode(node);
			c.getStrategy().setMap(g);
			return c;
		} catch ( ConfigurationException e ) {
			return null;
		}
	}

	/**
	 * @param id
	 * @param vehicles
	 * @return the vehicle with ID <code>id</code> in the list or <code>null</code>
	 */
	private Vehicle getVehicle(int id, List<Vehicle> vehicles) {
		for ( Vehicle c : vehicles ) {
			if ( c.getID() == id ) return c;
		}
		return null;
	}

	/**
	 * Checks if the end node is valid, prints a warning and throws an exception if not.
	 * @param nodes
	 * @param end
	 * @param id
	 * @param s
	 * @throws ConfigurationException 
	 */
	private RoadSegment checkEndPoint(List<RoadSegment> nodes, int end, int id, String s) throws ConfigurationException {
		RoadSegment n = RoadSegment.getRoadSegment(nodes, end );
		if ( n == null ) { 
			logger.warn( s + " node " + end + " for vehicle " + id + " does not exist");
			throw new ConfigurationException("");
		}
		return n;
	}

}
