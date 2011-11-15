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
	private int delay; //The time the car must wait at its current node
	private boolean finish; //True if the car has reached its destination
	private List<Integer> path; //Path the car travels
	private NoiseStrategy noise; //The noise strategy
	
	public CarInfo(int start, int end, int num) {
		this.start = start;
		this.end = end;
		current = start;
		carNum = num;
		delay = -1;
		finish = false;
		path = new ArrayList<Integer>();
		noise = new RandomNoiseStrategy();
	}
	
	public int getStartNode() {
		return start;
	}
	
	public void setStartNode(int s) {
		start = s;
	}
	
	public int getEndNode() {
		return end;
	}
	
	public void setEndNode(int e) {
		end = e;
	}
	
	public int getCurrentPosition() {
		return current;
	}
	
	public void setCurrentPosition(int c) {
		current = c;
	}
	
	public int getCarNum() {
		return carNum;
	}
	
	public void setCarNum(int cn) {
		carNum = cn;
	}
	
	public int getDelay() {
		return delay;
	}
	
	public void setDelay(int l) {
		delay = l;
	}
	
	public boolean atDestination() {
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
