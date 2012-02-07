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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.jdom.DefaultJDOMFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import traffic.graph.Graph;
import traffic.graph.GraphNode;
import traffic.model.Vehicle;
import traffic.model.TrafficSimulator;
import traffic.strategy.VehicleStrategy;

/**
 * This class takes a {@link TrafficSimulator} and some additional options
 * and produces an XML file that contains the descirption of the
 * {@link TrafficSimulator}. If optional fields like {@link VehicleStrategy} are not
 * assigned for some objects, this write will create an explicit field for them.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class SimulationXMLWriter {
	
	private static final String DEFAULT_SPEED_STRATEGY = "traffic.strategy.LinearSpeedStrategy";
	private static final String DEFAULT_CAR_STRATEGY = "traffic.strategy.LookaheadShortestPathVehicleStrategy";
	private static final String DEFAULT_NODE_CAPACITY = "0";
	
	private DefaultJDOMFactory factory = new DefaultJDOMFactory();

	
	private SimulationXMLWriter( ) {}
	
	/**
	 * This is the main interface to the {@link SimulationXMLWriter}. Passing in
	 * a {@link TrafficSimulator} and a {@link File} will generate the XML
	 * describing <code>sim</code> in <code>f</code>.
	 * @param sim
	 * @param f
	 * @throws IOException
	 */
	static void write( TrafficSimulator sim, File f ) throws IOException {
		new SimulationXMLWriter().writeSim( f, sim );
	}

	/**
	 * @param f
	 * @param sim
	 * @throws IOException 
	 */
	private void writeSim(File f, TrafficSimulator sim) throws IOException {
		Element s = factory.element( "simulation" );
		Document doc = factory.document( s );
		writeGraph( s, sim.getGraph() );
		writeVehicles( s, sim.getVehicles() );
		FileOutputStream out = new FileOutputStream( f );
		XMLOutputter p = new XMLOutputter( Format.getPrettyFormat() );
		p.output(doc, out);
		out.close();
	}

	/**
	 * @param doc
	 * @param vehicles
	 * @return
	 */
	private void writeVehicles(Element doc, List<Vehicle> vehicles) {
		Element c = factory.element( "vehicles" );
		c.setAttribute( factory.attribute( "default_strategy", DEFAULT_CAR_STRATEGY ) );
		for ( Vehicle vehicle : vehicles ) {
			writeVehicle( c, vehicle );
		}
		doc.addContent(c);
	}
/**
	 * @param c
	 * @param vehicle
	 */
	private void writeVehicle(Element vehicles, Vehicle vehicle) {
		Element c = factory.element( "vehicle" );
		c.setAttribute( factory.attribute( "start", "" + vehicle.getStartNode().getID() ) );
		c.setAttribute( factory.attribute( "end", "" + vehicle.getEndNode().getID() ) );
		c.setAttribute( factory.attribute( "id", "" + vehicle.getID() ) );
		c.setAttribute( factory.attribute( "strategy", "" + vehicle.getStrategy().getClass().getCanonicalName() ) );
		vehicles.addContent(c);
	}


	/**
	 * @param doc
	 * @param graph
	 * @return
	 */
	private void writeGraph(Element doc, Graph graph) {
		Element g = factory.element( "graph" );
		g.setAttribute( factory.attribute( "default_strategy", DEFAULT_SPEED_STRATEGY) );
		g.setAttribute( factory.attribute( "default_capacity", DEFAULT_NODE_CAPACITY ) );
		for ( GraphNode node : graph.getNodes() ) {
			writeNode(g, node );
		}
		doc.addContent(g);
	}

	/**
	 * @param g
	 * @param node
	 */
	private void writeNode(Element g, GraphNode node) {
		Element n = factory.element( "node");
		n.setAttribute( factory.attribute( "id", "" + node.getID() ) );
		n.setAttribute( factory.attribute( "delay", "" + node.getDelay() ) );
		n.setAttribute( factory.attribute( "neighbors", writeNeighbors(node.getNeighbors() ) ) );
		n.setAttribute( factory.attribute( "capacity", "" + node.getCapacity() ) );
		g.addContent(n);
	}

	/**
	 * @param neighbors
	 * @return
	 */
	private String writeNeighbors(List<GraphNode> neighbors) {
		assert neighbors != null;
		StringBuffer buf = new StringBuffer();
		for ( GraphNode node : neighbors ) {
			buf.append( node.getID() );
			buf.append( ' ');
		}
		return buf.toString().trim();
	}

}
