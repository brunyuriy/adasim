import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class TrafficGUI {
	private JFrame frame;
	private TrafficSimulator tsim;
	private JLabel file;
	private JTextField fileName;
	private List<JLabel> carPaths;
	private JButton go;
	private JButton step;
	private JButton end;
	
	public TrafficGUI() {
		tsim = TrafficSimulator.getInstance();
		createComponents();
		setGUI();
		handleEvents();
		frame.setVisible(true);
	}
	
	private void createComponents() {
		frame = new JFrame("Traffic Simulator");
		frame.setSize(500, 500);
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		file = new JLabel("File name?");
		fileName = new JTextField(10);
		carPaths = new ArrayList<JLabel>();
		go = new JButton("Go!");
		step = new JButton("Take one step");
		step.setEnabled(false);
		end = new JButton("Skip to end");
		end.setEnabled(false);
	}
	
	private void setGUI() {
		Container x = new JPanel();
		x.add(file);
		x.add(fileName);
		x.add(go);
		frame.add(x, BorderLayout.NORTH);
		Container y = new JPanel();
		y.add(step);
		y.add(end);
		frame.add(y, BorderLayout.SOUTH);
		setLabels();
	}
	
	private void setLabels() {
		Container c = new JPanel(new GridLayout(10, 0));
		for(int i = 0; i < 10; i++) {
			carPaths.add(new JLabel("Car" + i));
			c.add(carPaths.get(i));
		}
		frame.add(c, BorderLayout.CENTER);
	}
	
	private void setCarText() {
		for(int i = 0; i < tsim.getNumberOfCars(); i++) {
			carPaths.get(i).setText(tsim.getCar(i).toString());
		}
	}
	
	private void handleEvents() {
		ActionListener sim = new SimListener();
		go.addActionListener(sim);
		ActionListener stp = new StepListener();
		step.addActionListener(stp);
		ActionListener en = new EndListener();
		end.addActionListener(en);
	}
	
	private class SimListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			tsim.setFileName(fileName.getText());
			tsim.readPositions();
			step.setEnabled(true);
			end.setEnabled(true);
			setCarText();
		}
		
	}
	
	private class StepListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			tsim.takeStep();
			setCarText();
		}
		
	}
	
	private class EndListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			
		}
		
	}
}
