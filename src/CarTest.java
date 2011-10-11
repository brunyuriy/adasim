import org.junit.*;

import traffic.graph.Graph;
import traffic.model.Car;

public class CarTest {
	
	private Graph g;
	
	@Test
	public void test() {
		Car c = new Car("01:03", 0);
		g = new Graph();
	}

}
