/**
 * Jonathan Ramaswamy
 * CarFactory
 * Creates the cars used in the simulation from information given in a configuration file
 */
package traffic.factory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import traffic.model.Car;


public class CarFactory {

	private static Logger logger = Logger.getLogger(CarFactory.class);

	/**
	 * Reads in a file of positions and strategies for each car
	 * @param carFile The configuration file with all information about the cars
	 * @return The list of cars with their information set up
	 * @throws IOException 
	 */
	public static List<Car> loadCar(String carFile) {
		File positions = new File(carFile);
		try {
			Scanner input = new Scanner(positions);
			String num = input.nextLine();
			int numCars = Integer.parseInt(num);
			int carNum = 0;
			List<Car> cars = new ArrayList<Car>();
			for(int i = 0; i < numCars; i++) {
				String carPosition = input.nextLine();
				cars.add(new Car(Integer.parseInt(carPosition.substring(0,2)), 
						Integer.parseInt(carPosition.substring(3,5)),
						carPosition.substring(6,7), carNum));
				carNum++;
			}
			return cars;
		} catch (FileNotFoundException e) {
			logger.error("Configuration file not found");
			throw new IllegalArgumentException("File not found");
		} catch (Exception e) {
			logger.error("Configuration file has invalid format");
			throw new IllegalArgumentException("Invalid file");
		}
	}
	
}
