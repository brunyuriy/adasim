package traffic.factory;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import traffic.model.Car;
import traffic.strategy.DijkstraCarStrategy;



public class CarFactoryTest {
		
	@Test (expected=IllegalArgumentException.class)
	public void testFileNotFound() {
		CarFactory.loadCar("badfile");
	}
	
	@Test
	public void testBadConfig() {
		assertEquals(CarFactory.loadCar("resources/test/badconfig.xml"), null);
	}
	
	@Test
	public void testStart() {
		List<Car> cars = CarFactory.loadCar("config.xml");
		assertEquals(cars.get(0).getCurrent(), 0);
		assertEquals(cars.get(1).getCurrent(), 4);
		assertEquals(cars.get(2).getCurrent(), 3);
		assertEquals(cars.get(3).getCurrent(), 8);
		assertEquals(cars.get(4).getCurrent(), 3);
	}
	
	@Test
	public void testCarNum() {
		List<Car> cars = CarFactory.loadCar("config.xml");
		assertEquals(cars.get(0).getCarNumber(), 0);
		assertEquals(cars.get(1).getCarNumber(), 1);
		assertEquals(cars.get(2).getCarNumber(), 2);
		assertEquals(cars.get(3).getCarNumber(), 3);
		assertEquals(cars.get(4).getCarNumber(), 4);
	}
	
	@Test
	public void invalidStrategyDefaultsCorrectly() {
		List<Car> cars = CarFactory.loadCar("resources/test/invalid-strategy.xml");
		assertEquals( DijkstraCarStrategy.class, cars.get(1).getStrategy().getClass() );
	}
	
	@Test
	public void noCarThrows() {
		CarFactory.loadCar("resources/test/no-car.xml");
		fail( "This should throw a meaningful exception to be handled by main()" );
	}

	@Test
	public void noCarsThrows() {
		CarFactory.loadCar("resources/test/no-car.xml");
		fail( "This should throw a meaningful exception to be handled by main()" );
	}
	
	@Test
	public void invalidStartEndIsIgnored() {
		List<Car> cars = CarFactory.loadCar("resources/test/invalid-start.xml");
		assertEquals( 3, cars.size() );
		assertEquals( 0, cars.get(0).getCarNumber() );
		assertEquals( 3, cars.get(1).getCarNumber() );
		assertEquals( 4, cars.get(2).getCarNumber() );
	}
}
