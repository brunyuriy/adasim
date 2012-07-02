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

package adasim.model.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;

import adasim.agent.AdasimAgent;
import adasim.algorithm.delay.TrafficDelayFunction;
import adasim.algorithm.routing.RoutingAlgorithm;
import adasim.filter.AdasimFilter;
import adasim.model.AdasimMap;
import adasim.model.ConfigurationException;
import adasim.model.RoadSegment;
import adasim.model.Vehicle;
import adasim.util.ReflectionException;
import adasim.util.ReflectionUtils;


/**
 * A builder that constructs elements in a adasim simulation 
 * from their corresponding XML nodes.
 * <p>
 * Each public method maps a node type from the XML configuration
 * to either an explicit simulation object (for example a RoadSegment),
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
	public AdasimMap buildGraph( Element graphNode, FilterMap defaultFilters ) throws ConfigurationException {
		try {
			@SuppressWarnings("unchecked")
			List<Element> children = graphNode.getChildren("node");		
			TrafficDelayFunction ss = (TrafficDelayFunction) loadClassFromAttribute(graphNode, "default_strategy" );
			int capacity = Integer.parseInt(graphNode.getAttributeValue("default_capacity"));
			FilterMap fm = buildFilters( graphNode.getChild("filters"), defaultFilters, RoadSegment.class );
			return new AdasimMap( buildNodes( children, ss, fm, capacity) );
		} catch (ClassCastException e ) {
			throw new ConfigurationException( "Error loading class: " + e.getMessage() );
		} catch (Exception e) {
			throw new ConfigurationException("Unexpected error: " + e.getMessage() );
		}
	}

	/**
	 * Builds a {@link RoadSegment} from the given <code>&lt;node&gt;</code> element.
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
	public RoadSegment buildNode( Element nodeElement, FilterMap defaultFilters ) {
		int id = Integer.parseInt( nodeElement.getAttributeValue( "id" ) );
		int delay = Integer.parseInt( nodeElement.getAttributeValue( "delay" ) );
		TrafficDelayFunction ss = (TrafficDelayFunction)loadClassFromAttribute(nodeElement, "strategy" ); 
		RoadSegment gn = new RoadSegment( id, ss, delay, getCapacity(nodeElement)) ;
		Element filters = nodeElement.getChild( "filters" );
		FilterMap fm = buildFilters( filters, defaultFilters, RoadSegment.class );
		assignFilters( gn, fm.get(RoadSegment.class) );
		return gn;
	}

	/**
	 * @param gn
	 * @param fm
	 */
	private void assignFilters(AdasimAgent gn, Filters fm) {
		gn.setUncertaintyFilter( fm.uncertaintyFilter );
		for ( Class<?> c : fm.pMap ) {
			gn.setPrivacyFilter(fm.pMap.getFilter(c), c);
		}
	}

	/**
	 * Processes a <code><filters></code> element, if it exists. 
	 * @param filters
	 * @param defaultFilters
	 * 
	 * @return a new filter map that contains all filters from the argument map, 
	 * all newly declared filters, and that updates filters mapped by duplicate keys. 
	 * This way, the latest declaration will always take precedence.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	FilterMap buildFilters(Element filters, FilterMap defaultFilters, Class defaultAgent ) {		
		if ( filters != null ) {
			FilterMap resultMap = new FilterMap();
			resultMap.updateAll( defaultFilters );	//make a deep copy
			for ( Element f : (List<Element>)filters.getChildren( "filter") ) {
				String agent = f.getAttributeValue("agent");
				Class<?> agentClass = null;
				try {
					agentClass = (agent == null ? defaultAgent : Class.forName(agent) );
				} catch (ClassNotFoundException e1) {
					//TODO: log warning
				}
				if ( agentClass == null ) {
					agentClass = defaultAgent;
				} 
				Filters newMap = resultMap.get(agentClass);
				String type = f.getAttributeValue( "type" );
				AdasimFilter filter = (AdasimFilter)loadClassFromAttribute(f, "filter" );
				if ( type.equals("uncertainty") && filter != null ) {
					newMap.uncertaintyFilter = filter;
				} else if (type.equals( "privacy" ) && filter != null ) {
					String c = f.getAttributeValue("criterion" );
					try {
						Class<?> criterion = Class.forName(c);
						newMap.pMap.addFilter(filter, criterion);
					} catch (ClassNotFoundException e) {
						logger.warn( "Declared illegal criterion type \"" + c + "\". Declaration ignored." );
					}
				} else {
					logger.warn( "Declared illegal filter type \"" + type + "\". Declaration ignored." );
				}
				resultMap.update(agentClass, newMap);
			}
			return resultMap;
		} else {
			return defaultFilters;
		}
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
		Object o = loadClassFromAttribute(vehiclesNode, "default_strategy" );
		if ( o == null ) {
			throw new ConfigurationException( "Invalid default strategy " + vehiclesNode.getAttributeValue("default_strategy" ) );
		}
		@SuppressWarnings("unchecked")
		Class<? extends RoutingAlgorithm> cs = (Class<RoutingAlgorithm>)o.getClass();
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
	 * @throws ConfigurationException 
	 */
	private Vehicle assignDefaultVehicleValues(Vehicle c, Class<? extends RoutingAlgorithm> cs) throws ConfigurationException {
		if ( c.getStrategy() == null ) {
			try {
				c.setStrategy(cs.newInstance());
			} catch (Exception e) {
				throw new ConfigurationException( "Cannot instantiate default routing algorithm " + cs.getCanonicalName() );
			} 
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
		RoutingAlgorithm cs = null;
		if(vehicleNode.getAttributeValue("strategy") != null) {
			try {
				cs = (RoutingAlgorithm) loadClassFromAttribute(vehicleNode, "strategy");
			} catch (Exception e) {
				logger.warn( "RoutingAlgorithm " + vehicleNode.getAttributeValue("strategy") + " not found. Using default." );
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
	private List<RoadSegment> buildNodes( List<Element> nodeElements, TrafficDelayFunction defaultStrategy,
			FilterMap defaultFilters, int capacity ) {
		List<RoadSegment> nodes = new ArrayList<RoadSegment>( nodeElements.size() );
		for( Element node : nodeElements ) {
			RoadSegment gn = buildNode( node, defaultFilters );
			if ( gn != null ) {
				nodes.add( assignDefaultNodeValues(gn, defaultStrategy, capacity) );
			}
			if ( gn.getSpeedStrategy() == null ) {
				gn.setSpeedStrategy( defaultStrategy );
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
	 * @return the updated RoadSegment (should be reference equal)
	 */
	private RoadSegment assignDefaultNodeValues(RoadSegment gn, TrafficDelayFunction ss, int capacity ) {
		if ( gn.getSpeedStrategy() == null ) {
			gn.setSpeedStrategy(ss);
		}
		try {
			if ( ReflectionUtils.<Integer>getProperty(gn, "getCapacity" ) == -1 ) {
				gn.setCapacity(capacity);
			}
		} catch (NoSuchMethodException e) {
			//this should never happen
			e.printStackTrace();
		} catch (ReflectionException e) {
			//this should never happen
			e.printStackTrace();
		} 
		return gn;
	}

	/**
	 * Tries to return an instance of the given class. It first
	 * attempts to call the singleton method <code>getInstance()</code>,
	 * and if that does not return an object, calls the default constructor.
	 * If neither returns an object or any exception is thrown, this method
	 * returns <code>null</code>.
	 * @param node
	 * @param attribute
	 * @return an instance of the specified class, or <code>null</code> if an 
	 * instance could not be created by reflection.
	 */
	private Object loadClassFromAttribute(Element node, String attribute ) {
		Object t = null;
		String n = node.getAttributeValue( attribute );
		if ( n != null ) {
			try {
				@SuppressWarnings("rawtypes")
				Class c = Class.forName( n );
				try {
					@SuppressWarnings("unchecked")
					Method m = c.getDeclaredMethod("getInstance", new Class[0] );
					if ( m != null ) {
						t = m.invoke(null, new Object[0] );
					} 
				} catch (NoSuchMethodException e ) {} 
				if ( t == null )t = c.newInstance();
			} catch (Exception e) {}
		}
		return t;
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
	private void buildNeigbors(List<RoadSegment> nodes, Element node) {
		String nodeList = node.getAttributeValue("neighbors").trim();
		if ( nodeList.equals("") ) return;	//this node has no outgoing edges
		String[] neighbors = nodeList.split(" ");
		RoadSegment gn = getNode( nodes, node );
		for ( String n : neighbors ) {
			int nn = Integer.parseInt(n);
			gn.addEdge( RoadSegment.getRoadSegment( nodes, nn ));					
		}
	}

	/**
	 * @param nodes
	 * @param node
	 * @return the RoadSegment with the same ID as the XML element or <code>null</code>.
	 */
	private RoadSegment getNode(List<RoadSegment> nodes, Element node) {
		int id = Integer.parseInt( node.getAttributeValue( "id" ) );
		return RoadSegment.getRoadSegment(nodes, id);
	}

	/**
	 * @param nodes
	 * @return the list of validated nodes
	 */
	private List<RoadSegment> validate(List<RoadSegment> nodes) {
		List<RoadSegment> l = new ArrayList<RoadSegment>( nodes );
		List<RoadSegment> last;
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
	private List<RoadSegment> reduce(List<RoadSegment> nodes) {
		List<RoadSegment> l = new ArrayList<RoadSegment>();
		for ( RoadSegment node : nodes ) {
			for ( RoadSegment i : node.getNeighbors() ) {
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
	public List<? extends AdasimAgent> buildAgents(Element child, FilterMap defaultFilters ) throws ConfigurationException {
		List<AdasimAgent> agents = new ArrayList<AdasimAgent>();
		if ( child != null ) {
			FilterMap fm = buildFilters(child.getChild("filters"), defaultFilters, null);
			@SuppressWarnings("unchecked")
			List<Element> agentNodes = child.getChildren( "agent" );
			for ( Element agentNode : agentNodes ) {
				AdasimAgent agent = buildAgent( agentNode, fm);
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
	public AdasimAgent buildAgent(Element agent, FilterMap defaultFilters ) throws ConfigurationException {
		String clazz = agent.getAttributeValue("class");
		try {
			Class<?> cls = this.getClass().getClassLoader().loadClass(clazz);
			assert clazz != null;
			assert cls != null;
			String parameters = agent.getAttributeValue("parameters");
			int id = Integer.parseInt(agent.getAttributeValue("id"));
			
			FilterMap fm = buildFilters(agent.getChild("filters"), defaultFilters, cls );
			
			AdasimAgent agt = null;
			Constructor<?> c = cls.getConstructor( String.class );
			agt = (AdasimAgent) c.newInstance( parameters );
			agt.setID(id);
			assignFilters(agt, fm.get(cls) );
			return agt;
		} catch (Exception e) {
			throw new ConfigurationException( "Invalid agent class " + clazz, e);
		} 
	}
}
