package nl.bve.rabobank.parser;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

abstract class RaboParser {
	abstract Transaction nextTransaction();
	
	List<FailedTransaction> parse() throws FileNotFoundException {
		Map<String, String> allReferences = new HashMap<String, String>();
		Set<String> knownDuplicates = new HashSet<String>();
		List<FailedTransaction> failedTransactions = new ArrayList<FailedTransaction>();

		Transaction transaction;
		while ((transaction = nextTransaction()) != null) {
		    String reference = transaction.reference;
		    String description = transaction.description;
		    
			try {
			    BigDecimal startBalance = new BigDecimal(transaction.startBalance);
			    BigDecimal mutation = new BigDecimal(transaction.mutation);
			    BigDecimal endBalance = new BigDecimal(transaction.endBalance);

			    String replacedDescription = allReferences.put(reference, description);
			    boolean referenceIsDuplicate = replacedDescription != null; 
			    
			    if (referenceIsDuplicate) {
			    	// knownDuplicates.add returns true if the reference is not yet in the set
			    	boolean firstDuplicate = knownDuplicates.add(reference); 
			    	if (firstDuplicate) {
			    		failedTransactions.add(new FailedTransaction(reference, replacedDescription, INVALID.DUPLICATE));
			    	}
			    	
			    	failedTransactions.add(new FailedTransaction(reference, description, INVALID.DUPLICATE));
			    	continue;
			    }

			    if (startBalance.add(mutation).compareTo(endBalance) != 0) {
			    	failedTransactions.add(new FailedTransaction(reference, description, INVALID.WRONG_BALANCE));
			    }
			} catch (Exception e) {
				failedTransactions.add(new FailedTransaction(reference, description, INVALID.UNPARSEABLE));
			}
		}
		
		return failedTransactions;
	}
}