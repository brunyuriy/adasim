package traffic.factory;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import traffic.model.Car;



public class CarFactoryTest {
		
	@Test (expected=IllegalArgumentException.class)
	public void testFileNotFound() {
		CarFactoryXML.loadCar("badfile");
	}
	
	@Test
	public void testStart() {
		List<Car> cars = CarFactoryXML.loadCar("config.xml");
	}

}
