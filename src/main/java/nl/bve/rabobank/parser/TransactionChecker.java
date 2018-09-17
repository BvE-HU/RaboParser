package nl.bve.rabobank.parser;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class TransactionChecker {
	private TransactionParser parser;
	
	TransactionChecker(TransactionParser parser) {
		this.parser = parser;
	}
	
	List<FailedTransaction> check() throws FileNotFoundException {
		Map<String, String> allReferencesAndDescriptions = new HashMap<String, String>();
		Set<String> knownDuplicateReferences = new HashSet<String>();
		List<FailedTransaction> failedTransactions = new ArrayList<FailedTransaction>();

		Transaction transaction;
		while ((transaction = parser.nextTransaction()) != null) {
		    String reference = transaction.getReference();
		    String description = transaction.getDescription();
		    
			try {
			    BigDecimal startBalance = new BigDecimal(transaction.getStartBalance());
			    BigDecimal mutation = new BigDecimal(transaction.getMutation());
			    BigDecimal endBalance = new BigDecimal(transaction.getEndBalance());

			    String replacedDescription = allReferencesAndDescriptions.put(reference, description);
			    boolean referenceIsDuplicate = replacedDescription != null; 
			    
			    if (referenceIsDuplicate) {
			    	// knownDuplicates.add returns true if the reference is not yet in the set
			    	boolean firstDuplicate = knownDuplicateReferences.add(reference); 
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