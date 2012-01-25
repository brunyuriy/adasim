/**
 * Jonathan Ramaswamy
 * Speed Strategy
 * Interface for the speed limit at each node
 */
package traffic.strategy;

/**
 * This interface implement traffic delay functions.
 * Given the weight of a node, the number of cars and an additional parameter 
 * (the traffic capacity of a node), it returns the time required
 * to traverse that node.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public interface SpeedStrategy {
	
	public int getDelay(int weight, int cutoff, int number); //Returns the speed limit for the node depending on the number of cars

}
