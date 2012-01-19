/**
 * Jonathan Ramaswamy
 * Quadratic Speed Strategy
 * Sets the speed limit to be the square of the number of cars at the node
 */
package traffic.strategy;

public class QuadraticSpeedStrategy implements SpeedStrategy {

	/**
	 * The speed limit is set to be proportionate to the square of the number of cars at the node
	 */
	public int getDelay(int weight, int capacity, int number) {
		if (number > capacity) {
			return number - capacity + (weight * weight);
		} else {
			return weight * weight;
		}
	}

}
