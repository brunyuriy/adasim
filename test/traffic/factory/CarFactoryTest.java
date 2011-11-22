package traffic.factory;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import traffic.model.Car;



public class CarFactoryTest {
		
	@Test (expected=IllegalArgumentException.class)
	public void testFileNotFound() {
		CarFactory.loadCar("badfile");
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testBadFile() {
		CarFactory.loadCar("invalidcars.txt");
	}
	
	@Test
	public void testStart() {
		List<Car> cars = CarFactory.loadCar("cars2.txt");
	}

}
