/**
 * TrafficSimulator is the main program for running the simulator. It keeps track
 * of the grid and where all the cars are located on it, and outputs position information
 * to the GUI
 */

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

public class TrafficSimulator{
	private static TrafficSimulator instance = null;
	
	private static Logger logger = Logger.getLogger(TrafficSimulator.class);
	
	public static TrafficSimulator getInstance() {
		if (instance == null) {
			instance = new TrafficSimulator();
		}
		return instance;
	}
	
	private List<Car> cars;
	private int carNum;
	private Car[][] grid;
	private String fileName;
	
	public TrafficSimulator() {
		cars = new ArrayList<Car>();
		carNum = 0;
		grid = new Car[10][10];
		setGrid();
		fileName = "";
	}
	
	private void setGrid() {
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				grid[i][j] = null;
			}
		}
	}
	
	public Car getCar(int index) {
		return cars.get(index);
	}
	
	public int getNumberOfCars() {
		return carNum;
	}
	
	public void setFileName(String fn) {
		fileName = fn;
	}
	
	public void readPositions() {
		File positions = new File(fileName);
		try {
			Scanner input = new Scanner(positions);
			while(input.hasNextLine()) {
				String position = input.nextLine();
				cars.add(new Car(position, carNum));
				carNum++;
			}
			for(Car c: cars) {
				setCarPositions(c);
			}
			logger.info("Positions on grid set");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void setCarPositions(Car c) {
		Car d = grid[c.getCurrentX()][c.getCurrentY()];
		if(d == null) {
			d = c;
		} else {
			c.setConflict(d.getCarNumber());
		}
	}
	
	private boolean checkAllFinish() {
		for(Car c: cars) {
			if(!c.checkFinish()) {
				return false;
			}
		}
		return true;
	}

	public boolean runSim() {
		for(Car c: cars) {
			c.tryMove();
			setCarPositions(c);
		}
		return checkAllFinish();
	}
}
