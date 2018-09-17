package nl.bve.rabobank.parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

public class AppTest {
    @Test
    public void testFailuresShouldBeThree() throws FileNotFoundException {
        TransactionParserCSV parser = new TransactionParserCSV(new File("records.csv"));

        // assert statements
        TransactionChecker checker = new TransactionChecker(parser);
        List<FailedTransaction> failedTransactions = checker.check();
        
        assertEquals(3, failedTransactions.size());
    }
}