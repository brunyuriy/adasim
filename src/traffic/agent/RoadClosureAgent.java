/*******************************************************************************
 * Copyright (C) 2011 - 2012 Jochen Wuttke, Jonathan Ramaswamy
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Contributors:
 *    Jochen Wuttke (wuttkej@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Mar 14, 2012
 */
package traffic.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import traffic.model.RoadSegment;

/**
 * The RoadClosureAgent randomly selects roads to be closed
 * to traffic, and randomly opens them again for traffic
 * at a later time. This is a naive example that mostly illustrates 
 * how road-closures, for example due to accidents or 
 * traffic control measures can be simulated.
 * <p>
 * The agent takes two parameters: the probability with which
 * a road is closed in a cycle, and the time it stays closed.
 * <p>
 *
 * @author Jochen Wuttke - wuttkej@gmail.com
 */

public final class RoadClosureAgent extends AbstractAdasimAgent {

	private static Logger logger = Logger.getLogger(RoadClosureAgent.class);

	private Random randomizer;
	private double closureProbability = 0.0;
	private int closureDuration = 0;
	private Map<RoadSegment, Integer> closedNodes;
	
	public RoadClosureAgent( String args) {
		randomizer = new Random();
		closedNodes = new HashMap<RoadSegment, Integer>();
		setParameters(args);
	}
	
	/**
	 * This constructor is intended for testing only. It should guarantee
	 * the same random results on each run given a fixed seed.
	 * 
	 * @param seed
	 */
	RoadClosureAgent( String args, long seed ) {
		randomizer = new Random(seed);		
		closedNodes = new HashMap<RoadSegment, Integer>();
		setParameters(args);
	}

	/**
	 * The parameter string must consist of the string representation of
	 * a positive double value, a colon, and the string representation of a positive integer. 
	 * For example, <code>0.0034:42</code>. The string representations of
	 * numbers must parse with the Double.parseDouble() and Integer.parseInt()
	 * methods, respectively.
	 * <p>
	 * The first value is the probability with which a road becomes closed in any cycle,
	 * and the integer is the duration for which it will stay closed. 
	 * In principle, both the probability and the duration should be greater
	 * than 0. 
	 * <p>
	 * If the agent decided in cycle <em>k</em> that a road is closed, the road will 
	 * remain closed up to and including cycle <em>k+duration</em>.
	 * 
	 */
	@Override
	public void setParameters(String params) {
		String[] pars = params.trim().split(":");
		if ( pars.length != 2 ) {
			throw new IllegalArgumentException( "RoadClosureAgent.setParameters(): wrong number of parameters.\nSee JavaDoc for details." );
		}
		closureProbability = Double.parseDouble(pars[0]);
		closureDuration = Integer.parseInt(pars[1]);
		if ( closureDuration < 0 || closureProbability < 0.0 ) {
			throw new IllegalArgumentException( "RoadClosureAgent.setParameters(): arguments must be positive.\nSee JavaDoc for details." );
		}
		logger.debug( "Configuring RoadClosureAgent: " + closureProbability + "; " + closureDuration );
	}

	/* (non-Javadoc)
	 * @see traffic.model.AdasimAgent#takeSimulationStep(long)
	 */
	@Override
	public void takeSimulationStep(long cycle) {
		for ( RoadSegment r : simulator.getGraph().getRoadSegments() ) {
			if ( r.isClosed() ) {
				int d = closedNodes.get(r);
				if ( d < closureDuration) {
					closedNodes.put(r, d + 1);
				} else {
					r.setClosed(false);
					closedNodes.remove(r);
					logger.info( "OPEN: Road: " + r.getID() );
				}
			} else {
				boolean close = randomizer.nextDouble() < closureProbability;
				if ( close ) {
					r.setClosed( true );
					closedNodes.put(r, 0);
					logger.info( "CLOSE: Road: " + r.getID() );
				}
			}
		}
	}

	/* 
	 * Inspection methods for testing
	 */
	
	/**
	 * @return the closureDuration
	 */
	int getClosureDuration() {
		return closureDuration;
	}
	
	/**
	 * @return the closureProbability
	 */
	double getClosureProbability() {
		return this.closureProbability ;
	}
	
}
