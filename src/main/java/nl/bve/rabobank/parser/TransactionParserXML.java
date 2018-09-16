package nl.bve.rabobank.parser;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class TransactionParserXML implements TransactionParser {
	private int current = 0;
	private NodeList nList; 
	
	TransactionParserXML(File transactionsFile) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(transactionsFile);
        doc.getDocumentElement().normalize();
        nList = doc.getElementsByTagName("record");
	}
	
	@Override
	public Transaction nextTransaction() {
		if (current < nList.getLength()) {
			Node nNode = nList.item(current++);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String reference = eElement.getAttribute("reference");
                String description = eElement.getElementsByTagName("description").item(0).getTextContent();
                String startBalance = eElement.getElementsByTagName("startBalance").item(0).getTextContent();
                String mutation = eElement.getElementsByTagName("mutation").item(0).getTextContent();
                String endBalance = eElement.getElementsByTagName("endBalance").item(0).getTextContent();
                
                return new Transaction(reference, description, startBalance, mutation, endBalance);
            }			
		}
		
		return null;
	}
}