package nl.bve.rabobank.parser;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class RaboTransactionChecker {
	public static void main(String[] args) {
		try {
			TransactionParser parser = null;
			
			if (args.length != 1) {
				throw new Exception("Usage: RaboTransactionChecker <filename.CSV | filename.XML>");
			} else if (! Files.exists(new File(args[0]).toPath())) {
				throw new Exception("File " +args[0]+ " does not exist!");
			} else if (args[0].endsWith(".csv") && args[0].length() > 4) {
				parser = new TransactionParserCSV(new File(args[0]));
			} else if (args[0].endsWith(".xml") && args[0].length() > 4) {
				throw new Exception("XML parser is not yet implemented!");
			} else {
				throw new Exception("Usage: RaboTransactionChecker <filename.CSV | filename.XML>");
			}
			
			TransactionChecker checker = new TransactionChecker(parser);
			List<FailedTransaction> failedTransactions = checker.parse();
			
			Collections.sort(failedTransactions);
			failedTransactions.forEach(System.out::println);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
