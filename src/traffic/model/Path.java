package traffic.model;
import traffic.graph.Graph;


public interface Path {
	
	void makePath(Graph g, int s);
	void tryMove(Graph g);
	boolean redoPath(Graph g);

}

