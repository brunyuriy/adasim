package traffic.model;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

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