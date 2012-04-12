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

package traffic.model.internal;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;

import traffic.agent.AdasimAgent;
import traffic.filter.AdasimFilter;
import traffic.filter.IdentityFilter;
import traffic.model.ConfigurationException;
import traffic.model.AdasimMap;
import traffic.model.GraphNode;
import traffic.model.Vehicle;
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
	 * @return the fully validated graph
	 * @throws ConfigurationException
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public AdasimMap buildGraph( Element graphNode ) throws ConfigurationException {
		try {
			@SuppressWarnings("unchecked")
			List<Element> children = graphNode.getChildren("node");		
			SpeedStrategy ss = (SpeedStrategy) loadClassFromAttribute(graphNode, "default_strategy" );
			int capacity = Integer.parseInt(graphNode.getAttributeValue("default_capacity"));
			AdasimFilter uf = (AdasimFilter) loadClassFromAttribute(graphNode, "uncertainty_filter");
			if ( uf == null ) {
				uf = new IdentityFilter();
			} 
			AdasimFilter pf = (AdasimFilter) loadClassFromAttribute(graphNode, "privacy_filter");
			return new AdasimMap( buildNodes( children, ss, uf, pf, capacity) );
		} catch (ClassCastException e ) {
			throw new ConfigurationException( "Error loading class: " + e.getMessage() );
		} catch (Exception e) {
			throw new ConfigurationException("Unexpected error: " + e.getMessage() );
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
	 * @return a partically configured node
	 */
	public GraphNode buildNode( Element nodeElement ) {
		int id = Integer.parseInt( nodeElement.getAttributeValue( "id" ) );
		SpeedStrategy ss = (SpeedStrategy)loadClassFromAttribute(nodeElement, "strategy" ); 
		GraphNode gn = new GraphNode( id, ss, getDelay(nodeElement ), getCapacity(nodeElement)) ;
		AdasimFilter f = (AdasimFilter) loadClassFromAttribute(nodeElement, "uncertainty_filter" ); 
		gn.setUncertaintyFilter(f);
		f = (AdasimFilter) loadClassFromAttribute(nodeElement, "privacy_filter" ); 
		gn.setPrivacyFilter(f);
		
		return gn;
	}

	/**
	 * Constructs a list of {@link Vehicle}s from the <code>&lt;vehicles&gt;</code> element.
	 * It will also assign default values to vehicles that don't declare them explicitly.
	 * <p>
	 * Not start/end point validation will occur, as these are not known at this stage.
	 * @param vehiclesNode
	 * @return the list of all fully configured vehicles
	 * @throws ConfigurationException
	 */
	public List<Vehicle> buildVehicles( Element vehiclesNode ) throws ConfigurationException {
		@SuppressWarnings("unchecked")
		List<Element> vehicleNodes = vehiclesNode.getChildren("car");
		VehicleStrategy cs = (VehicleStrategy)loadClassFromAttribute(vehiclesNode, "default_strategy" );
		List<Vehicle> vehicles = new ArrayList<Vehicle>();
		for ( Element vehicle : vehicleNodes ) {
			Vehicle c = buildVehicle( vehicle );
			if ( c != null ) vehicles.add( assignDefaultVehicleValues( c, cs ) );
		}
		return vehicles;	
	}

	/**
	 * @param c
	 * @param cs
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
	 * @return a partially configured vehicle (global default may be missing) 
	 */
	public Vehicle buildVehicle( Element vehicleNode ) {
		int id = Integer.parseInt(vehicleNode.getAttributeValue("id"));
		VehicleStrategy cs = null;
		if(vehicleNode.getAttributeValue("strategy") != null) {
			try {
				cs = (VehicleStrategy) loadClassFromAttribute(vehicleNode, "strategy");
			} catch (Exception e) {
				logger.warn( "VehicleStrategy " + vehicleNode.getAttributeValue("strategy") + " not found. Using default." );
			}
		}
		return new Vehicle(null, null, cs, id );
	}

	/**
	 * This method is private, as it does not deal with a single XML element, but connects several.
	 * It assigns default values where no explicit values are defined and performs other validation
	 * as required.
	 * 
	 * @return the list of fully validated GraphNodes
	 */
	private List<GraphNode> buildNodes( List<Element> nodeElements, SpeedStrategy defaultStrategy,
			AdasimFilter uncertaintyFilter, AdasimFilter privacyFilter, int capacity ) {
		List<GraphNode> nodes = new ArrayList<GraphNode>( nodeElements.size() );
		for( Element node : nodeElements ) {
			GraphNode gn = buildNode( node );
			if ( gn != null ) {
				nodes.add( assignDefaultNodeValues(gn, defaultStrategy, capacity) );
			}
			//assign default when needed
			if ( gn.getUncertaintyFilter() == null ) {
				gn.setUncertaintyFilter( uncertaintyFilter );
			}
			if ( gn.getSpeedStrategy() == null ) {
				gn.setSpeedStrategy( defaultStrategy );
			}
			if ( gn.getPrivacyFilter() == null ) {
				gn.setPrivacyFilter(privacyFilter);
			}
		}
		for ( Element node: nodeElements ) {
			buildNeigbors(nodes, node );
		}
		nodes = validate(nodes);
		return nodes;		
	}

	/**
	 * @param gn
	 * @return the updated GraphNode (should be reference equal)
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

	private Object loadClassFromAttribute(Element node, String attribute ) {
		Object t = null;
		String n = node.getAttributeValue( attribute );
		if ( n != null ) {
			try {
				@SuppressWarnings("rawtypes")
				Class c = Class.forName( n );
				t = c.newInstance();
			} catch (Exception e) {}
		}
		return t;
	}

	/**
	 * @param node
	 * @return the delay declared in the XML element or the default 1
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
		String nodeList = node.getAttributeValue("neighbors").trim();
		if ( nodeList.equals("") ) return;	//this node has no outgoing edges
		String[] neighbors = nodeList.split(" ");
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
	 * @return the GraphNode with the same ID as the XML element or <code>null</code>.
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
	 * @return the list of validated nodes
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
	 * Removes links that point to invalid neighbors.
	 * @param nodes
	 * @return the updated list
	 */
	private List<GraphNode> reduce(List<GraphNode> nodes) {
		List<GraphNode> l = new ArrayList<GraphNode>();
		for ( GraphNode node : nodes ) {
			for ( GraphNode i : node.getNeighbors() ) {
				if ( ! nodes.contains( i ) ) {
					node.removeEdge(i);
				}
			}
			l.add( node );
		}
		return l;
	}

	/**
	 * @param child
	 * @return a list of partially configured agents
	 * @throws ConfigurationException 
	 */
	public List<? extends AdasimAgent> buildAgents(Element child) throws ConfigurationException {
		List<AdasimAgent> agents = new ArrayList<AdasimAgent>();
		if ( child != null ) {
			@SuppressWarnings("unchecked")
			List<Element> agentNodes = child.getChildren( "agent" );
			for ( Element agentNode : agentNodes ) {
				AdasimAgent agent = buildAgent( agentNode );
				if ( agent != null ) {
					agents.add(agent);
				}
			}
		}
		return agents;
	}

	/**
	 * @param agent
	 * @return a partially configured agent
	 * @throws ConfigurationException 
	 */
	public AdasimAgent buildAgent(Element agent) throws ConfigurationException {
		String clazz = agent.getAttributeValue("class");
		assert clazz != null;
		String parameters = agent.getAttributeValue("parameters");

		AdasimAgent agt = null;
		try {
			Class<?> cls = this.getClass().getClassLoader().loadClass(clazz);
			Constructor<?> c = cls.getConstructor( String.class );
			agt = (AdasimAgent) c.newInstance( parameters );
		} catch (Exception e) {
			throw new ConfigurationException( "Invalid agent class " + clazz, e);
		} 

		return agt;
	}

}
