

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import traffic.graph.Graph;
import traffic.model.Car;
import traffic.strategy.CarStrategy;
import traffic.strategy.DijkstraCarStrategy;


public class DijkstraTest {
	
	@SuppressWarnings("deprecation")
	@Test
	public void Test() {
		Graph graph = new Graph();
		File graphP = new File("graph2.txt");
		try {
			Scanner input = new Scanner(graphP);
			int nodes = Integer.parseInt(input.nextLine());
			for(int i = 0; i < nodes; i++) {
				String speed = input.nextLine();
				String s = speed.substring(2,3);
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
		CarStrategy cs = new DijkstraCarStrategy();
		List<Integer> path = cs.getPath(graph, 4, 6);
		Object[] expect = {4, 7, 8, 6};
		assertEquals(expect , path.toArray());
	}

}
