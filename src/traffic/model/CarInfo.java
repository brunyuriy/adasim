package traffic.model;

import java.util.ArrayList;
import java.util.List;

import traffic.strategy.CarStrategy;
import traffic.strategy.NoiseStrategy;
import traffic.strategy.RandomNoiseStrategy;

public class CarInfo {
	
	private int start; //Starting position
	private int end; //Destination position
	private int current; //Current position
	private int carNum; //This car's number in the list of cars
	private int limit; //The time the car must wait at its current node
	private boolean finish; //True if the car has reached its destination
	private List<Integer> path; //Path the car travels
	private NoiseStrategy noise; //The noise strategy
	
	public CarInfo(String positions, int num) {
		start = Integer.parseInt(positions.substring(0, 2));
		end = Integer.parseInt(positions.substring(3, 5));
		current = start;
		carNum = num;
		limit = -1;
		finish = false;
		path = new ArrayList<Integer>();
		noise = new RandomNoiseStrategy();
	}
	
	public int getStart() {
		return start;
	}
	
	public void setStart(int s) {
		start = s;
	}
	
	public int getEnd() {
		return end;
	}
	
	public void setEnd(int e) {
		end = e;
	}
	
	public int getCurrent() {
		return current;
	}
	
	public void setCurrent(int c) {
		current = c;
	}
	
	public int getCarNum() {
		return carNum;
	}
	
	public void setCarNum(int cn) {
		carNum = cn;
	}
	
	public int getLimit() {
		return limit;
	}
	
	public void setLimit(int l) {
		limit = l;
	}
	
	public boolean getFinish() {
		return finish;
	}
	
	public void setFinish() {
		finish = current == end;
	}
	
	public List<Integer> getPath() {
		return path;
	}
	
	public void setPath(List<Integer> p) {
		path = p;
	}

}
