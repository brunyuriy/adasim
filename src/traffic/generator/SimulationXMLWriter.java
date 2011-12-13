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
import java.io.FileNotFoundException;
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
import traffic.model.Car;
import traffic.model.TrafficSimulator;

/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class SimulationXMLWriter {
	
	private static final String DEFAULT_SPEED_STRATEGY = "traffic.strategy.LinearSpeedStrategy";
	private static final String DEFAULT_CAR_STRATEGY = "traffic.strategy.LookaheadShortestPathCarStrategy";
	
	private DefaultJDOMFactory factory = new DefaultJDOMFactory();

	
	private SimulationXMLWriter( ) {}
	
	static void write( TrafficSimulator sim, File f ) throws IOException {
		new SimulationXMLWriter().writeSim( f, sim );
	}

	/**
	 * @param f
	 * @param sim
	 * @throws IOException 
	 */
	private void writeSim(File f, TrafficSimulator sim) throws IOException {
		Document doc = factory.document( factory.element( "simulation" ) );
		doc = writeGraph( doc, sim.getGraph() );
		doc = writeCars( doc, sim.getCars() );
		FileOutputStream out = new FileOutputStream( f );
		XMLOutputter p = new XMLOutputter( Format.getPrettyFormat() );
		p.output(doc, out);
		out.close();
	}

	/**
	 * @param doc
	 * @param cars
	 * @return
	 */
	private Document writeCars(Document doc, List<Car> cars) {
		Element c = factory.element( "cars" );
		c.setAttribute( factory.attribute( "default_strategy", DEFAULT_CAR_STRATEGY ) );
		for ( Car car : cars ) {
			writeCar( c, car );
		}
		doc.addContent(c);
		return doc;
	}
/**
	 * @param c
	 * @param car
	 */
	private void writeCar(Element cars, Car car) {
		Element c = factory.element( "car" );
		c.setAttribute( factory.attribute( "start", "" + car.getInfo().getStartNode() ) );
		c.setAttribute( factory.attribute( "end", "" + car.getInfo().getEndNode() ) );
		c.setAttribute( factory.attribute( "id", "" + car.getInfo().getCarNum() ) );
		c.setAttribute( factory.attribute( "strategy", "" + car.getInfo().getStrategy().getClass().getCanonicalName() ) );
	}


	/**
	 * @param doc
	 * @param graph
	 * @return
	 */
	private Document writeGraph(Document doc, Graph graph) {
		Element g = factory.element( "graph" );
		g.setAttribute( factory.attribute( "default_strategy", DEFAULT_SPEED_STRATEGY) );
		for ( GraphNode node : graph.getNodes() ) {
			writeNode(g, node );
		}
		doc.addContent(g);
		return doc;
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
