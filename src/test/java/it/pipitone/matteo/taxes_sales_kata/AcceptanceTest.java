package it.pipitone.matteo.taxes_sales_kata;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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

    @Test
    public void parseOneItem() {
        Bucket bucket = new Bucket("1 book at 12.49");
        String receipt = bucket.printReceipt();
        assertThat(receipt, is("1 book: 12.49\n"+
                "Total: 12.49"));
    }

    private static class Bucket {

        private final Item item;

        public Bucket(String items) {
            String[] s = items.split(" ");
            List<String> parameterPristine = Arrays.stream(s).filter(s1 -> !s1.equals("at")).collect(Collectors.toList());
            item = new Item(Integer.parseInt(parameterPristine.get(0)), parameterPristine.get(1), new BigDecimal(parameterPristine.get(2)));
        }

        public String printReceipt() {
            return item.printDescription() + "\n" +
                    "Total: " + item.price.toEngineeringString();
        }

        private class Item {

            private final int numberOfItem;
            private final String description;
            private final BigDecimal price;

            public Item(int numberOfItem, String description, BigDecimal price) {
                this.numberOfItem = numberOfItem;
                this.description = description;
                this.price = price;
            }

            public String printDescription() {
                return numberOfItem + " " + description + ": " + price.toEngineeringString();
            }
        }
    }
}