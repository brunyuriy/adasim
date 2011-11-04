/**
 * Jonathan Ramaswamy
 * Path
 * An path interface for the car or any other object on the graph to follow
 */
package traffic.model;
import traffic.graph.Graph;

public interface Path {
	
	void makePath(Graph g, int s); //Makes the path the object will follow
	void tryMove(Graph g); //Tries to move the object along the path

}

