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
 * Created: Jan 18, 2012
 */

package traffic.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;

import traffic.graph.Graph;
import traffic.graph.GraphNode;
import traffic.strategy.VehicleStrategy;
import traffic.strategy.SpeedStrategy;

/**
 * A builder that constructs elements in a traffic simulation 
 * from their corresponding XML nodes.
 * <p>
 * Each public method maps a node type from the XML configuration
 * to either an explicit simulation object (for example a GraphNode),
 * or a container of such objects (for example a list of GraphNodes).
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class SimulationXMLBuilder {

	private static final Logger logger = Logger.getLogger(SimulationXMLBuilder.class);


	/**
	 * Builds a graph from the <code>&lt;graph&gt;</code> element.
	 * <p>
	 * It constructs all nodes and passes on default values to those
	 * nodes that have explicit declarations.
	 * 
	 * @param graphNode
	 * @return
	 * @throws ConfigurationException
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public Graph buildGraph( Element graphNode ) throws ConfigurationException {
		try {
			@SuppressWarnings("unchecked")
			List<Element> children = graphNode.getChildren("node");		
			Class<?> cls = Class.forName(graphNode.getAttributeValue("default_strategy"));
			SpeedStrategy ss = (SpeedStrategy) cls.newInstance();
			int capacity = Integer.parseInt(graphNode.getAttributeValue("default_capacity"));
			return new Graph( buildNodes( children, ss, capacity) );
		} catch (Exception e) {
			throw new ConfigurationException("Invalid default graph strategy: " + graphNode.getAttributeValue("default_strategy"));
		}
	}

	/**
	 * Builds a {@link GraphNode} from the given <code>&lt;node&gt;</code> element.
	 * <p>
	 * It will assign only values declared in the XML Other values will be:
	 * <ul>
	 * <li> delay = -1
	 * <li> capacity = -1
	 * <li> strategy = null
	 * <li> neighbors = null (we have a list of numbers, but no nodes corresponding)
	 * </ul>
	 * 
	 * @param nodeElement
	 * @return
	 */
	public GraphNode buildNode( Element nodeElement ) {
		int id = Integer.parseInt( nodeElement.getAttributeValue( "id" ) );
		SpeedStrategy ss = buildStrategy( nodeElement );
		return new GraphNode( id, ss, getDelay(nodeElement ), getCapacity(nodeElement)) ;
	}

	/**
	 * Constructs a list of {@link Vehicle}s from the <code>&lt;vehicles&gt;</code> element.
	 * It will also assign default values to vehicles that don't declare them explicitly.
	 * <p>
	 * Not start/end point validation will occur, as these are not known at this stage.
	 * @param vehiclesNode
	 * @return
	 * @throws ConfigurationException
	 */
	public List<Vehicle> buildVehicles( Element vehiclesNode ) throws ConfigurationException {
		@SuppressWarnings("unchecked")
		List<Element> vehicleNodes = vehiclesNode.getChildren("car");
		VehicleStrategy cs = null;
		try {
			Class<?> cls = Class.forName(vehiclesNode.getAttributeValue("default_strategy"));
			cs = (VehicleStrategy) cls.newInstance();
			//TODO: assign this somewhere
			//cs.setGraph(g);
		} catch (Exception e ) {
			throw new ConfigurationException("Invalid default vehicle strategy: " + vehiclesNode.getAttributeValue("default_strategy"));
		}
		List<Vehicle> vehicles = new ArrayList<Vehicle>();
		for ( Element vehicle : vehicleNodes ) {
			//Vehicle c = buildVehicle( vehicle, cs, g );
			Vehicle c = buildVehicle( vehicle );
			if ( c != null ) vehicles.add( assignDefaultVehicleValues( c, cs ) );
		}
		return vehicles;	
	}

	/**
	 * @param c
	 * @param cs
	 * @return
	 */
	private Vehicle assignDefaultVehicleValues(Vehicle c, VehicleStrategy cs) {
		if ( c.getStrategy() == null ) {
			c.setStrategy(cs);
		}
		return c;
	}

	/**
	 * Builds a {@link Vehicle} from the given <code>&lt;vehicle&gt;</code> element.
	 * <p>
	 * No default values are assigned, as these are not known here. No start/end 
	 * validation is performed, as the graph is not known at this stage.
	 * 
	 * @param vehicleNode
	 * @return
	 */
	public Vehicle buildVehicle( Element vehicleNode ) {
//		int start = Integer.parseInt(vehicleNode.getAttributeValue("start"));
//		int end = Integer.parseInt(vehicleNode.getAttributeValue("end"));
		int id = Integer.parseInt(vehicleNode.getAttributeValue("id"));
		//TODO: move validation somewhere else!!!!

		//		List<GraphNode> nodes = g.getNodes();
		//		try {
		//			checkEndPoint(nodes, start, id, "Start" );
		//			checkEndPoint(nodes, end, id, "End" );
		//		} catch ( ConfigurationException e ) {
		//			return null;
		//		}
		//VehicleStrategy cs = defaultStrategy;
		VehicleStrategy cs = null;
		if(vehicleNode.getAttributeValue("strategy") != null) {
			String s = null;
			try {
				s = vehicleNode.getAttributeValue("strategy");
				cs = (VehicleStrategy) Class.forName(s).newInstance();

				//TODO: link the graph somewhere else
				//cs.setGraph(g);
			} catch (Exception e) {
				logger.warn( "VehicleStrategy " + s + " not found. Using default." );
			}
		}
		return new Vehicle(null, null, cs, id );
	}

	/**
	 * This method is private, as it does not deal with a single XML element, but connects several.
	 * It assigns default values where no explicit values are defined and performs other validation
	 * as required.
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
				nodes.add( assignDefaultNodeValues(gn, defaultStrategy, capacity) );
			}
		}
		for ( Element node: nodeElements ) {
			if ( hasValidNeighbors(node) ) {
				buildNeigbors(nodes, node );
			}
		}
		nodes = validate(nodes);
		return nodes;		
	}

	/**
	 * @param gn
	 * @return
	 */
	private GraphNode assignDefaultNodeValues(GraphNode gn, SpeedStrategy ss, int capacity ) {
		if ( gn.getSpeedStrategy() == null ) {
			gn.setSpeedStrategy(ss);
		}
		if ( gn.getCapacity() == -1 ) {
			gn.setCapacity(capacity);
		}
		return gn;
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
			} catch (Exception e) {}
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
		String[] neighbors = node.getAttributeValue("neighbors").trim().split(" ");
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
