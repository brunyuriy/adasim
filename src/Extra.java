import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import traffic.graph.Graph;
import traffic.strategy.CarStrategy;
import traffic.strategy.DijkstraStrategy;


public class Extra {

	public static void main(String[] args) {
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
		CarStrategy cs = new DijkstraStrategy();
		List<Integer> path = cs.getPath(graph, 4, 6);
		System.out.println(path);
	}
}
