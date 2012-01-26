/*******************************************************************************
 * Copyright (c) 2012 - Jonathan Ramaswamy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jonathan Ramaswamy (ramaswamyj12@gmail.com) - initial API and implementation
 ********************************************************************************
 *
 * Created: Jan 1, 2012
 */

package traffic.model;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * This is a simple error handler for errors found when parsing the XML file
 * 
 * @author Jonathan Ramaswamy - ramaswamyj12@gmail.com
 *
 */
public class SimpleErrorHandler implements ErrorHandler {
       
    public void error(SAXParseException exception) throws SAXParseException {
    	throw exception;    
    }
         
    public void fatalError(SAXParseException exception) throws SAXParseException {
        throw exception;
    }
         
    public void warning(SAXParseException exception) throws SAXParseException {
        throw exception;
    }
}