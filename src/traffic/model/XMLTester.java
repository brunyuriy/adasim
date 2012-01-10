package traffic.model;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;


public class XMLTester {
    
    public static final void main(String[] args) {
        
        if ( args.length != 1 ) {
            System.out.println("Usage: java XmlTester myFile.xml");
            System.exit(-1);
        }
        String xmlFile = args[0];
        
        try {
            XMLTester xmlTester = new XMLTester(xmlFile);
        }
        catch (Exception e) {
            System.out.println( e.getClass().getName() +": "+ e.getMessage() );
        }
    }
    
    public XMLTester(String xmlFile) throws ParserConfigurationException, ConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        factory.setNamespaceAware(true);
        factory.setValidating(true);
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", "sim.xsd");
        
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new SimpleErrorHandler());

		try {
			Document document = builder.parse(xmlFile);
			Node rootNode  = document.getFirstChild();
	        System.out.println("Root node: "+ rootNode.getNodeName()  );
		} catch (Exception e) {
			throw new ConfigurationException("invalid XML file");
		}
    }
}