/**
 * Jonathan Ramaswamy
 * Random Speed Strategy
 * Picks a random number to be the speed limit
 */
package traffic.strategy;

import java.util.Random;

public class RandomSpeedStrategy implements SpeedStrategy{

	/**
	 * Picks a random number between 0 and 9 to be the speed limit
	 */
	public int getDelay(int n) {
		Random generator = new Random();
		return generator.nextInt(10);
	}

}
