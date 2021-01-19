package it.pipitone.matteo.taxes_sales_kata;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Test
    public void parseOneItemWithTaxes() {
        Bucket bucket = new Bucket("1 music CD at 14.99");
        String receipt = bucket.printReceipt();
        assertThat(receipt, is("1 music CD: 16.49\n"+
                "Total: 16.49"));
    }


    private static class Bucket {

        private final Item genericItem;

        public Bucket(String items) {
            String regex = "(^[0-9]*) ([a-zA-Z ]*) (at) ([0-9.]*)";
            Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(items);

            boolean found = matcher.find();
            int itemNumber = Integer.parseInt(matcher.group(1));
            String itemDescription = matcher.group(2);
            BigDecimal price = new BigDecimal(matcher.group(4));
            genericItem = new GenericItem(itemNumber, itemDescription, price);
        }

        public String printReceipt() {
            return genericItem.printDescription() + "\n" +
                    "Total: " + genericItem.calculatePrice();
        }

        private static class GenericItem implements Item {

            private final int numberOfItem;
            private final String description;
            private final BigDecimal price;

            public GenericItem(int numberOfItem, String description, BigDecimal price) {
                this.numberOfItem = numberOfItem;
                this.description = description;
                this.price = price;
            }

            public String printDescription() {
                return numberOfItem + " " + description + ": " + calculatePrice();
            }

            @Override
            public String calculatePrice() {
                if(!description.contains("book")){
                   return price.multiply(new BigDecimal("1.10"), new MathContext(4, RoundingMode.HALF_UP)).toEngineeringString();
                }
                return price.toEngineeringString();
            }
        }
    }
}