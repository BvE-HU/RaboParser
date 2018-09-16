package nl.bve.rabobank.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

final class TransactionParserCSV implements TransactionParser {
	private CsvParserSettings settings = new CsvParserSettings();
	private CsvParser parser = new CsvParser(settings);
	
	TransactionParserCSV(File transactionsFile) throws FileNotFoundException {
		settings.setHeaderExtractionEnabled(true);
		parser.beginParsing(new FileReader(transactionsFile));
	}
	
	@Override
	public Transaction nextTransaction() {
		Record record = parser.parseNextRecord();
		
		if (record != null) {
			String reference = record.getString("Reference");
			String description = record.getString("Description");
			String startBalance= record.getString("Start Balance");
			String mutation = record.getString("Mutation");
			String endBalance = record.getString("End Balance");
			
			return new Transaction(reference, description, startBalance, mutation, endBalance);
		}
		
		return null;
	}
}