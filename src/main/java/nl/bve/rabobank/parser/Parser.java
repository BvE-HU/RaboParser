package nl.bve.rabobank.parser;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

public class Parser {
	private enum INVALID { DUPLICATE, WRONG_BALANCE }
	
	private static class FailedTransaction implements Comparable<FailedTransaction> {
		private String reference;
		private String description;
		private INVALID cause;
		
		public FailedTransaction(String reference, String description, INVALID cause) {
			this.reference = reference;
			this.description = description;
			this.cause = cause;
		}
		
		@Override
		public int compareTo(FailedTransaction other) {
			int referenceCompare = reference.compareTo(other.reference);
			return (referenceCompare == 0 ? cause.compareTo(other.cause) : referenceCompare);
		}
		
		public String toString() {
			return cause+ ": " +reference+ "; " +description;
		}
	}
	
	
	public static void csvParser(String filename) throws IOException {
		CsvParserSettings settings = new CsvParserSettings();
		settings.setHeaderExtractionEnabled(true);
		
		CsvParser parser = new CsvParser(settings);
		parser.beginParsing(new FileReader(filename));
		
		Map<String, String> allReferences = new HashMap<String, String>();
		Set<String> knownDuplicates = new HashSet<String>();
		List<FailedTransaction> failed = new ArrayList<FailedTransaction>();

		Record transaction;
		while ((transaction = parser.parseNextRecord()) != null) {
		    String reference = transaction.getString("Reference");
		    String description = transaction.getString("Description");

		    String replacedDescription = allReferences.put(reference, description);
		    boolean duplicateReference = replacedDescription != null; 
		    
		    if (duplicateReference) {
		    	// knownDuplicates.add returns true if the reference is not yet in the set
		    	boolean firstDuplicate = knownDuplicates.add(reference); 
		    	
		    	if (firstDuplicate) {
		    		failed.add(new FailedTransaction(reference, replacedDescription, INVALID.DUPLICATE));
		    	}
		    	
		    	failed.add(new FailedTransaction(reference, description, INVALID.DUPLICATE));
		    	continue;
		    }
		    
		    try {
			    BigDecimal transactionStartBalance = new BigDecimal(transaction.getString("Start Balance"));
			    BigDecimal transactionMutation = new BigDecimal(transaction.getString("Mutation"));
			    BigDecimal transactionEndBalance = new BigDecimal(transaction.getString("End Balance"));
			    
			    if (transactionStartBalance.add(transactionMutation).compareTo(transactionEndBalance) != 0) {
			    	failed.add(new FailedTransaction(reference, description, INVALID.WRONG_BALANCE));
			    }
		    } catch (NullPointerException npe) {
		    	System.out.println("Failed to parse row with reference: " +reference);
		    }
		}
		
		Collections.sort(failed);
		failed.forEach(System.out::println);
	}


	public static void main(String[] args) {
		try {
			if (args.length != 1) {
				System.out.println("Usage: RCSParser <filename.CSV | filename.XML>");
			} else if (! Files.exists(new File(args[0]).toPath())) {
				System.out.println("File " +args[0]+ " does not exist!");
			} else if (args[0].endsWith(".csv") && args[0].length() > 4) {
				csvParser(args[0]);
			} else if (args[0].endsWith(".xml") && args[0].length() > 4) {
				System.out.println("XML parser is not yet implemented!");
			} else {
				System.out.println("Usage: RCSParser <filename.CSV | filename.XML>");
			}
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
}
