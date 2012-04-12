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

import traffic.agent.AdasimAgent;
import traffic.model.Graph;
import traffic.model.GraphNode;
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
		writeVehicles( s, sim.getAgents() );
		FileOutputStream out = new FileOutputStream( f );
		XMLOutputter p = new XMLOutputter( Format.getPrettyFormat() );
		p.output(doc, out);
		out.close();
	}

	/**
	 * @param doc
	 * @param vehicles
	 */
	private void writeVehicles(Element doc, List<AdasimAgent> vehicles) {
		Element c = factory.element( "cars" );
		c.setAttribute( factory.attribute( "default_strategy", DEFAULT_CAR_STRATEGY ) );
		for ( AdasimAgent car : vehicles ) {
			writeCar( c, (Vehicle)car );
		}
		doc.addContent(c);
	}
	
	/**
	 * @param vehicles
	 * @param v
	 */
	private void writeCar(Element vehicles, Vehicle v) {
		Element c = factory.element( "car" );
		c.setAttribute( factory.attribute( "start", "" + v.getStartNode().getID() ) );
		c.setAttribute( factory.attribute( "end", "" + v.getEndNode().getID() ) );
		c.setAttribute( factory.attribute( "id", "" + v.getID() ) );
		c.setAttribute( factory.attribute( "strategy", "" + v.getStrategy().getClass().getCanonicalName() ) );
		vehicles.addContent(c);
	}


	/**
	 * @param doc
	 * @param graph
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
	 * @return a string representing the list of neighbors 
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
