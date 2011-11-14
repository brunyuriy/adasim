package traffic.factory;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import traffic.model.Car;


public class CarFactory {

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
			e.printStackTrace();
			return null;
		}
	}
	
}
