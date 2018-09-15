package nl.bve.rabobank.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class App {
	public static void main(String[] args) {
		try {
			if (args.length != 1) {
				System.out.println("Usage: RCSParser <filename.CSV | filename.XML>");
			} else if (! Files.exists(new File(args[0]).toPath())) {
				System.out.println("File " +args[0]+ " does not exist!");
			} else if (args[0].endsWith(".csv") && args[0].length() > 4) {
				RaboCSVParser csvParser = new RaboCSVParser(new File(args[0]));
				List<FailedTransaction> failedTransactions = csvParser.parse();
				
				Collections.sort(failedTransactions);
				failedTransactions.forEach(System.out::println);
				
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
