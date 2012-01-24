/*******************************************************************************
 * Copyright (c) 2011 - Jonathan Ramaswamy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jonathan Ramaswamy (ramaswamyj12@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Jan 10, 2011
 */

package traffic.model;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 *
 */

public class SimpleErrorHandler implements ErrorHandler {
    
	/**
	 * Thrown when an error that can possibly be handled is encountered
	 */
    public void error(SAXParseException exception) throws SAXParseException {
    	throw exception;    
    }
    
    /**
     * Thrown when a fatal error is encountered and the program must shut down
     */
    public void fatalError(SAXParseException exception) throws SAXParseException {
        throw exception;
    }
    
    /**
     * Thrown when the program encounters a possible problem but can still run
     */
    public void warning(SAXParseException exception) throws SAXParseException {
        throw exception;
    }
}