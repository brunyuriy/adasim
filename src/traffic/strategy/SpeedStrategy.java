/**
 * Jonathan Ramaswamy
 * Speed Strategy
 * Interface for the speed limit at each node
 */
package traffic.strategy;

public interface SpeedStrategy {
	
	public int getDelay(int n); //Returns the speed limit for the node depending on the number of cars

}
