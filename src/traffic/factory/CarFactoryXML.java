/**
 * Jonathan Ramaswamy
 * CarFactoryXML
 * Creates the cars used in the simulation from information given in an XML configuration file
 */
package traffic.factory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.*;

import org.apache.log4j.Logger;
import org.jdom.*;
import org.jdom.input.SAXBuilder;

import traffic.model.Car;
import traffic.strategy.CarStrategy;
import traffic.strategy.DijkstraCarStrategy;

public class CarFactoryXML {
	
	private static Logger logger = Logger.getLogger(CarFactoryXML.class);
	
	/**
	 * Reads in a file of positions and strategies for each car
	 * @param carFile The configuration file with all information about the cars
	 * @return The list of cars with their information set up
	 * @throws IllegalArgumentException if file is not found
	 */
	public static List<Car> loadCar(String carFile) {
		Document doc = null;
		SAXBuilder builder = new SAXBuilder(false);
		File carPositions = new File(carFile);
		try {
			doc = builder.build(carPositions);
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
