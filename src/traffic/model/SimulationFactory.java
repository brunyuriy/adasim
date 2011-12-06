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

	private SimulationFactory( File f ) {
		SAXBuilder b = new SAXBuilder(false);
		try {
			doc = b.build( f );
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static TrafficSimulator buildSimulator( String xmlString ) {
		return null;
	}

	public static TrafficSimulator buildSimulator( File config ) {
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
				String[] neighbors = children.get(i).getAttributeValue("neighbors").split(" ");
				for ( String n : neighbors ) {
					end.add(Integer.parseInt(n));					
				}
			}
			return new Graph(node, strategies, start, end);
		} catch (Exception e) {
			logger.error("Bad config file");
			return null;
		}
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
