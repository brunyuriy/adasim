/*******************************************************************************
 * Copyright (c) 2011 - Jochen Wuttke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jochen Wuttke (wuttkej@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Dec 12, 2011
 */

package adasim.generator;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import adasim.generator.SimulationXMLWriter;
import adasim.model.ConfigurationException;
import adasim.model.TrafficSimulator;
import adasim.model.internal.SimulationXMLReader;
import static org.junit.Assert.assertTrue;



/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
public class SimulationXMLWriterTest {
	
	@Test
	public void readWriteDoesntCrash() throws ConfigurationException, IOException {
		File inFile = new File( "resources/test/config.xml" );
		TrafficSimulator sim = SimulationXMLReader.buildSimulator( inFile );
		File outFile = new File( "output.xml" );
		SimulationXMLWriter.write(sim, outFile );
		assertTrue( outFile.exists() );
		assertTrue( outFile.length() > 0 );
		outFile.delete();
	}

}
