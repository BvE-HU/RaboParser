package nl.bve.rabobank.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

final public class RaboCSVParser {
	private File transactionsFile = null;
	private StringBuilder log = new StringBuilder();
	private CsvParserSettings settings = new CsvParserSettings();
	private CsvParser parser = new CsvParser(settings);
	
	protected RaboCSVParser(File transactionsFile) {
		this.transactionsFile = transactionsFile;
		settings.setHeaderExtractionEnabled(true);
	}
	
	protected List<FailedTransaction> parse() throws FileNotFoundException {
		parser.beginParsing(new FileReader(transactionsFile));
		
		Map<String, String> allReferences = new HashMap<String, String>();
		Set<String> knownDuplicates = new HashSet<String>();
		List<FailedTransaction> failed = new ArrayList<FailedTransaction>();

		Record transaction;
		while ((transaction = parser.parseNextRecord()) != null) {
			try {
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
			    
			    BigDecimal transactionStartBalance = new BigDecimal(transaction.getString("Start Balance"));
			    BigDecimal transactionMutation = new BigDecimal(transaction.getString("Mutation"));
			    BigDecimal transactionEndBalance = new BigDecimal(transaction.getString("End Balance"));

			    if (transactionStartBalance.add(transactionMutation).compareTo(transactionEndBalance) != 0) {
			    	failed.add(new FailedTransaction(reference, description, INVALID.WRONG_BALANCE));
			    }
			} catch (Exception e) {
			    log.append("Failed to parse row with reference: " +transaction.getString("Reference")+"\n");
			}
		}
		
		return failed;
	}
	
	public void printLog() {
		if (log.length() > 0) {
			System.out.println(log.toString());
		}
	}
}