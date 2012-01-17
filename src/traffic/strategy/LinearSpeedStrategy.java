/**
 * Jonathan Ramaswamy
 * Linear Speed Strategy
 * Adjusts the speed limit linearly based on the number of cars
 */
package traffic.strategy;

public class LinearSpeedStrategy implements SpeedStrategy {

	/**
	 * Sets the number of turns a car must wait at a node to be equal to
	 * the number of cars currently at the node
	 */
	public int getDelay(int weight, int capacity, int number) {
		if (number > capacity) {
			return number - capacity + weight;
		} else {
			return weight;
		}
	}

}
