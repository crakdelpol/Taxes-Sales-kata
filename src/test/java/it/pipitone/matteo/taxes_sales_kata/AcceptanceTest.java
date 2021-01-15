package it.pipitone.matteo.taxes_sales_kata;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AcceptanceTest {


    @Test
    public void firstAcceptanceTest(){

        Bucket bucket = new Bucket("1 book at 12.49\n" +
                "1 music CD at 14.99\n" +
                "1 chocolate bar at 0.85");

        String receipt = bucket.printReceipt();

        assertThat(receipt, is("1 book: 12.49\n" +
                "1 music CD: 16.49\n" +
                "1 chocolate bar: 0.85\n" +
                "Sales Taxes: 1.50\n" +
                "Total: 29.83"));

    }

    private static class Bucket {
        public Bucket(String items) {

        }

        public String printReceipt() {
            return null;
        }
    }
}