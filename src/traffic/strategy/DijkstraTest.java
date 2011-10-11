package traffic.strategy;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import traffic.graph.Graph;
import traffic.model.Car;


public class DijkstraTest {
	
	@Test
	public void Test() {
		Graph graph = new Graph();
		File graphP = new File("graph2.txt");
		try {
			Scanner input = new Scanner(graphP);
			int nodes = Integer.parseInt(input.nextLine());
			for(int i = 0; i < nodes; i++) {
				String stop = input.nextLine();
				int s = Integer.parseInt(stop.substring(2,3));
				graph.addNode(i, s);
			}
			while(input.hasNextLine()) {
				String edge = input.nextLine();
				int i = Integer.parseInt(edge.substring(0,1));
				int o = Integer.parseInt(edge.substring(2,3));
				graph.addEdge(i, o);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		/*int carNum = 0;
		List<Car> cars = new ArrayList<Car>();
		File positions = new File("cars2.txt");
		try {
			Scanner input = new Scanner(positions);
			while(input.hasNextLine()) {
				String position = input.nextLine();
				Car c = new Car(position, carNum);
				cars.add(c);
				graph.addCarAtNode(carNum, c.getCurrent());
				carNum++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/
		CarStrategy cs = new DijkstraStrategy();
		List<Integer> path = cs.getPath(graph, 4, 6);
		assertEquals(null, path);
	}

}
