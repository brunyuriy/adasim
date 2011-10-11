package traffic.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import traffic.graph.Graph;

public class RandomStrategy implements CarStrategy{

	public RandomStrategy(){}
	
	public List<Integer> getPath(Graph g, int current, int destination) {
		int next = current;
		List<Integer> path = new ArrayList<Integer>();
		while(next != destination) {
			List<Integer> d = g.getDestinations(next);
			Random generator = new Random();
			int rand = generator.nextInt(d.size());
			next = d.get(rand);
			path.add(next);
		}
		return path;
	}

	public int redoPath(Graph g, int current) {
		boolean found = false;
		int n = -1;
		List<Integer> d = g.getDestinations(current);
		while(!found) {
			if(d.size() > 0) {
				Random generator = new Random();
				int rand = generator.nextInt(d.size());
				n = d.get(rand);
				if(g.getCarsAtNode(n) < 2) {
					found = true;
				}
				d.remove(rand);
			} else {
				found = true;
			}
		}
		return n;
	}

}
