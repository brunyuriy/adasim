/**
 * Jonathan Ramaswamy
 * Random Noise Strategy creates a random value for the 
 * amount of noise experienced at a node
 */
package traffic.strategy;

import java.util.Random;

public class RandomNoiseStrategy implements NoiseStrategy{
	
	public double getNoise() {
		Random generator = new Random();
		double rand = generator.nextDouble();
		return rand;
	}

}
