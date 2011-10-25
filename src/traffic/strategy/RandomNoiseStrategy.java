package traffic.strategy;

import java.util.Random;

public class RandomNoiseStrategy implements NoiseStrategy{
	
	public double getNoise() {
		Random generator = new Random();
		double rand = generator.nextDouble();
		return rand;
	}

}
