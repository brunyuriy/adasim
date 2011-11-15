package traffic.factory;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import traffic.model.Car;



public class CarFactoryTest {
		
	@Test (expected=IllegalArgumentException.class)
	public void FactoryTest1() {
		CarFactory.loadCar("invalidcars.txt");
	}
	
	@Test
	public void FactoryTest2() {
		List<Car> cars = CarFactory.loadCar("invalidcars2.txt");
		assertEquals(cars.size(), 5);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void FactoryTest3() {
		CarFactory.loadCar("invalidcars3.txt");
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void FactoryTest4() {
		CarFactory.loadCar("invalidcars4.txt");
	}
	
	@Test
	public void FactoryTest5() {
		assertEquals(CarFactory.loadCar("invalidcars5.txt"), null);
	}

}
