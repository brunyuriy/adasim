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
	public void testBadConfig() {
		assertEquals(CarFactoryXML.loadCar("badconfig.xml"), null);
	}
	
	@Test
	public void testStart() {
		List<Car> cars = CarFactoryXML.loadCar("config.xml");
		assertEquals(cars.get(0).getCurrent(), 0);
		assertEquals(cars.get(1).getCurrent(), 4);
		assertEquals(cars.get(2).getCurrent(), 3);
		assertEquals(cars.get(3).getCurrent(), 8);
		assertEquals(cars.get(4).getCurrent(), 3);
	}
	
	@Test
	public void testCarNum() {
		List<Car> cars = CarFactoryXML.loadCar("config.xml");
		assertEquals(cars.get(0).getCarNumber(), 0);
		assertEquals(cars.get(1).getCarNumber(), 1);
		assertEquals(cars.get(2).getCarNumber(), 2);
		assertEquals(cars.get(3).getCarNumber(), 3);
		assertEquals(cars.get(4).getCarNumber(), 4);
	}

}
