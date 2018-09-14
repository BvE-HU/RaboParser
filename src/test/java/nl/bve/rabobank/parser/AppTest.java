package nl.bve.rabobank.parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

public class AppTest {
    @Test
    public void testFailuresShouldBeThree() throws FileNotFoundException {
        RaboCSVParser parser = new RaboCSVParser(new File("records.csv"));

        // assert statements
        List<FailedTransaction> failedTransactions = parser.parse();
        
        assertEquals(3, failedTransactions.size());
    }
}