package traffic.factory;

import static org.junit.Assert.*;

import org.junit.Test;

import traffic.graph.Graph;

public class GraphFactoryTest {
	
	@Test (expected=IllegalArgumentException.class)
	public void testFileNotFound() {
		//Tests for proper exception thrown when file is not found
		GraphFactory.loadGraph("notfound");
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testInvalidFirstLine() {
		//Tests for proper exception thrown when file does not have number of nodes as first line
		GraphFactory.loadGraph("badgraph1.txt");
	}
	
	@Test
	public void testStrategies() {
		Graph g = GraphFactory.loadGraph("graph3.txt");
		assertEquals(g.getDelayAtNode(6), 0);
		g.addCarAtNode(0, 6);
		g.addCarAtNode(1, 6);
		assertEquals(g.getDelayAtNode(6), 4); //Tests Quadratic Speed Strategy
		assertEquals(g.getDelayAtNode(1), 0);
		g.addCarAtNode(2, 1);
		g.addCarAtNode(3, 1);
		assertEquals(g.getDelayAtNode(1), 2); //Tests Linear Speed Strategy
	}

}
