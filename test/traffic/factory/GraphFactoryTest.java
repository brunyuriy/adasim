package traffic.factory;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import traffic.graph.Graph;
import traffic.graph.GraphNode;

public class GraphFactoryTest {
	
	@Test (expected=IllegalArgumentException.class)
	public void testFileNotFound() {
		//Tests for proper exception thrown when file is not found
		GraphFactoryXML.loadGraph("notfound");
	}
	
	@Test
	public void testStrategies() {
		Graph g = GraphFactoryXML.loadGraph("graph.xml");
		assertEquals(g.getDelayAtNode(6), 0);
		g.addCarAtNode(0, 6);
		g.addCarAtNode(1, 6);
		assertEquals(g.getDelayAtNode(6), 4); //Tests Quadratic Speed Strategy
		assertEquals(g.getDelayAtNode(1), 0);
		g.addCarAtNode(2, 1);
		g.addCarAtNode(3, 1);
		assertEquals(g.getDelayAtNode(1), 2); //Tests Linear Speed Strategy
	}
	
	@Test
	public void testNeighbors() {
		Graph g = GraphFactoryXML.loadGraph("graph.xml");
		List<Integer> neighbors = g.getNodes().get(2).getNeighbors();
		int first = neighbors.get(0);
		assertEquals(first, 4);
		int second = neighbors.get(1);
		assertEquals(second, 7);
		int third = neighbors.get(2);
		assertEquals(third, 9);
	}

}
