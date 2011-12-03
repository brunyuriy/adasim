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
 * Created: Dec 3, 2011
 */

package traffic.generator;

import org.jdom.DefaultJDOMFactory;
import org.jdom.Document;
import org.jdom.Element;

/**
 * @author Jochen Wuttke - wuttkej@gmail.com
 *
 */
class SimulationConfigBuilder {
	
	static Document build( ConfigurationOptions opts ) {
		DefaultJDOMFactory factory = new DefaultJDOMFactory();
		Document doc = factory.document( new Element( "simulation" ) );
		return doc;
	}

}
