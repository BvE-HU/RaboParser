package nl.bve.rabobank.parser;

import java.io.File;
import java.io.FileReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

final class TransactionParserXMLStAX implements TransactionParser {
	private XMLStreamReader xmlSr;

	TransactionParserXMLStAX(File transactionsFile) throws Exception {
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		xmlSr = xmlInputFactory.createXMLStreamReader(new FileReader(transactionsFile));
	}

	@Override
    public Transaction nextTransaction() {
    	try {

    		// Een while-loop, omdat je misschien niet als eerste xml-element een 'record' tegenkomt.
            while (xmlSr.hasNext()) {
            	int eventType = xmlSr.next();
            	
            	if (eventType == XMLEvent.START_ELEMENT && xmlSr.getLocalName().equalsIgnoreCase("record")) {
        			Transaction newTransaction = new Transaction();
        			
        			// attribute 0, omdat het enige attribute de reference is. Mooier zou zijn om te checken of index 0 inderdaad de reference is.
        			newTransaction.setReference(xmlSr.getAttributeValue(0));            		
            		
        			
        			// while-loop om in 1 keer alle gegevens van een transaction in te lezen
            		while (xmlSr.hasNext()) {
            			xmlSr.next();
            			if (xmlSr.getEventType() == XMLEvent.START_ELEMENT) {
            				if (xmlSr.getLocalName().equalsIgnoreCase("description"))
            					newTransaction.setDescription(xmlSr.getElementText());
            				
            				if (xmlSr.getLocalName().equalsIgnoreCase("startBalance"))
            					newTransaction.setStartBalance(xmlSr.getElementText());

            				if (xmlSr.getLocalName().equalsIgnoreCase("endBalance"))
            					newTransaction.setEndBalance(xmlSr.getElementText());
            				
            				if (xmlSr.getLocalName().equalsIgnoreCase("mutation"))
            					newTransaction.setMutation(xmlSr.getElementText());
            			}
            			
            			if (xmlSr.getEventType() == XMLEvent.END_ELEMENT) {
            				if (xmlSr.getLocalName().equalsIgnoreCase("record")) {
            					return newTransaction;
            				}
            			}
            		}
            	}
            }
    	} catch (XMLStreamException ex) {
	    	ex.printStackTrace();
	    }
	    
	    return null;
    }
}