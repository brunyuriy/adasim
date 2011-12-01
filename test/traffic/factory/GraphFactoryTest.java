package traffic.factory;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import traffic.graph.Graph;
import traffic.model.Car;
import traffic.strategy.DijkstraCarStrategy;
import traffic.strategy.QuadraticSpeedStrategy;

public class GraphFactoryTest {
	
	@Test (expected=IllegalArgumentException.class)
	public void testFileNotFound() {
		//Tests for proper exception thrown when file is not found
		GraphFactory.loadGraph("notfound");
	}
	
	@Test
	public void testJDOMException() {
		assertEquals(GraphFactory.loadGraph("badconfig.xml"), null);
	}
	
	@Test
	public void testStrategies() {
		Graph g = GraphFactory.loadGraph("config.xml");
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
		Graph g = GraphFactory.loadGraph("config.xml");
		List<Integer> neighbors = g.getNodes().get(2).getNeighbors();
		int first = neighbors.get(0);
		assertEquals(first, 4);
		int second = neighbors.get(1);
		assertEquals(second, 7);
		int third = neighbors.get(2);
		assertEquals(third, 9);
	}
	
	@Test
	public void emptyNeighborListDoesNotCrash() {
		Graph g = GraphFactory.loadGraph("unconnected-node.xml");
		//this test passes if no exception is thrown
	}
	
	@Test
	public void noNodesThrows() {
		GraphFactory.loadGraph("no-nodes.xml");
		fail( "This should throw a meaningful exception to be handled by main()" );
	}

	@Test
	public void noGraphThrows() {
		GraphFactory.loadGraph("no-graph.xml");
		fail( "This should throw a meaningful exception to be handled by main()" );
	}
	
	@Test
	public void invalidNeighborIsIgnored() {
		Graph g = GraphFactory.loadGraph("invalid-neighbor.xml");
		List<Integer> neighbors = g.getNeighbors( 0 ); 
		assertEquals(1, neighbors.size() );
		assertEquals(4, (int)neighbors.get(0) );
	}
	
	@Test
	public void invalidStrategyDefaultsCorrectly() {
		Graph g = GraphFactory.loadGraph("invalid-strategy2.xml");
		assertEquals( QuadraticSpeedStrategy.class, g.getNodes().get(1).getSpeedStrategy().getClass() );
	}

}
