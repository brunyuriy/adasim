package traffic.factory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.*;
import org.jdom.input.SAXBuilder;

import traffic.model.Car;

public class CarFactoryXML {
	
	private static Logger logger = Logger.getLogger(CarFactoryXML.class);
	
	public static List<Car> loadCar(String carFile) {
		Document doc = null;
		SAXBuilder builder = new SAXBuilder(false);
		File carPositions = new File(carFile);
		try {
			doc = builder.build(carPositions);
			Element root = doc.getRootElement();
			List<Element> children = root.getChildren("car");
			int size = children.size();
			int carNum = 0;
			List<Car> cars = new ArrayList<Car>();
			for(int i = 0; i < size; i++) {
				cars.add(new Car(Integer.parseInt(children.get(i).getAttributeValue("start")), 
						Integer.parseInt(children.get(i).getAttributeValue("end")),
						children.get(i).getAttributeValue("strategy"), carNum));
				carNum++;
			}
			return cars;
		} catch (JDOMException e) {
			logger.error("JDOMException");
			return null;
		} catch (IOException e) {
			logger.error("JDOMException");
			throw new IllegalArgumentException("File not found");
		}
	}

}
