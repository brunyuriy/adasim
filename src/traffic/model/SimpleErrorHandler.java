package traffic.model;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public class SimpleErrorHandler implements ErrorHandler {
       
    public void error(SAXParseException exception) {
        System.out.println("error: "+ exception.getMessage());
    }
         
    public void fatalError(SAXParseException exception) {
        System.out.println("fatalError: "+ exception.getMessage());
    }
         
    public void warning(SAXParseException exception) {
        System.out.println("warning: "+ exception.getMessage());
    }
}