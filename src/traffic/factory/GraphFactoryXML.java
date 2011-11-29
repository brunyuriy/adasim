/**
 * Jonathan Ramaswamy
 * GraphFactoryXML
 * Creates the graph used in the simulation from information given in an XML configuration file
 */
package traffic.factory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.*;
import org.jdom.input.SAXBuilder;

import traffic.graph.Graph;
import traffic.model.Car;
import traffic.strategy.SpeedStrategy;

public class GraphFactoryXML {
	
	private static Logger logger = Logger.getLogger(GraphFactoryXML.class);
	
	/**
	 * Reads in a file of nodes with their neighbors and speed limits
	 * @param g The configuration file with all information about the graph
	 * @return The fully configured graph object
	 */
	public static Graph loadGraph(String g) {
		Document doc = null;
		SAXBuilder builder = new SAXBuilder(false);
		File graphP = new File(g);
		try {
			doc = builder.build(graphP);
			Element root = doc.getRootElement();
			List<Element> graphChild = root.getChildren("graph");
			Element graphList = graphChild.get(0);
			List<Element> children = graphList.getChildren("node");
			int nodes = children.size();
			List<Integer> node = new ArrayList<Integer>();
			List<SpeedStrategy> strategies = new ArrayList<SpeedStrategy>();
			for(int i = 0; i < nodes; i++) {
				Class<?> cls = Class.forName(graphList.getAttributeValue("default_strategy"));
				SpeedStrategy ss = (SpeedStrategy) cls.newInstance();
				node.add(Integer.parseInt(children.get(i).getAttributeValue("id")));
				if(children.get(i).getAttributeValue("strategy") == null) {
					strategies.add(ss);
				} else {
					strategies.add((SpeedStrategy) Class.forName(children.get(i).getAttributeValue("strategy")).newInstance());
				}
			}
			List<Integer> start = new ArrayList<Integer>();
			List<Integer> end = new ArrayList<Integer>();
			for(int i = 0; i < nodes; i++) {
				String neighbors = children.get(i).getAttributeValue("neighbors");
				for(int j = 0; j < neighbors.length(); j+=2) {
					start.add(i);
					end.add(Integer.parseInt(neighbors.substring(j, j+1)));
				}
			}
			return new Graph(node, strategies, start, end);
		} catch (JDOMException e) {
			logger.error("JDOMException");
			return null;
		} catch (IOException e) {
			logger.error("JDOMException");
			throw new IllegalArgumentException("File not found");
		} catch (Exception e) {
			logger.error("Bad config file");
			return null;
		}
	}

}
