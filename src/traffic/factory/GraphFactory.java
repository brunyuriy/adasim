package traffic.factory;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import traffic.graph.Graph;


public class GraphFactory {

	public static Graph loadGraph(String g) {
		File graphP = new File(g);
		try {
			Scanner input = new Scanner(graphP);
			int nodes = Integer.parseInt(input.nextLine());
			List<Integer> node = new ArrayList<Integer>();
			List<String> strats = new ArrayList<String>();
			for(int i = 0; i < nodes; i++) {
				String speed = input.nextLine();
				String s = speed.substring(2,3);
				node.add(i);
				strats.add(s);
			}
			List<Integer> start = new ArrayList<Integer>();
			List<Integer> end = new ArrayList<Integer>();
			while(input.hasNextLine()) {
				String edge = input.nextLine();
				start.add(Integer.parseInt(edge.substring(0,1)));
				end.add(Integer.parseInt(edge.substring(2,3)));
			}
			return new Graph(node, strats, start, end);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
