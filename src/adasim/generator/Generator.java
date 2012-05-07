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
 * Created: Dec 3, 2011
 */

package adasim.generator;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jdom.JDOMException;

import adasim.model.ConfigurationException;


/**
 * A simple generator for random simulation setups.
 * <p>
 * It supports full randomization of graphs of defined 
 * sizes with a defined number of cars, and it supports
 * loading graph and randomizing only cars for that graph.
 * 
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class Generator {

	/**
	 * @param args
	 * @throws JDOMException 
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		ConfigurationOptions opts = null;
		try {
			opts = ConfigurationOptions.parse(args);
		} catch (Exception e) {
			System.err.println( "Error parsing command line options: " + e.getMessage() );
			System.exit( 1 );
		}
		
		try {
			if ( opts.isBottleneck() ) {
				SimulationXMLWriter.write( new CongestedSimulationBuilder().build(opts), opts.getOutputFile() );
			} else {
				SimulationXMLWriter.write( new SimulationBuilder().build(opts), opts.getOutputFile() );
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
