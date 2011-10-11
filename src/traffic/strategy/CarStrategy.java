package traffic.strategy;

import java.util.List;

import traffic.graph.Graph;

public interface CarStrategy {
	
	public List<Integer> getPath(Graph g, int current, int destination);
	public int redoPath(Graph g, int current);

}
