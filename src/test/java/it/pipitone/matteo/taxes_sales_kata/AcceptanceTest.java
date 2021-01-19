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

    @Test
    public void parseOneItemImported() {
        Bucket bucket = new Bucket("1 imported box of chocolates at 10.00");
        String receipt = bucket.printReceipt();
        assertThat(receipt, is("1 imported box of chocolates: 10.50\n"+
                "Total: 10.50"));
    }

    private static class Bucket {

        private Item genericItem;

        public Bucket(String items) {
            String regex = "(^[0-9]*) ([a-zA-Z ]*) (at) ([0-9.]*)";
            Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(items);

            boolean found = matcher.find();
            int itemNumber = Integer.parseInt(matcher.group(1));
            String itemDescription = matcher.group(2);
            BigDecimal price = new BigDecimal(matcher.group(4));
            genericItem = new ItemFactory().createItem(itemNumber, itemDescription, price);
        }

        public String printReceipt() {
            return genericItem.printDescription() + "\n" +
                    "Total: " + genericItem.calculatePrice().toEngineeringString();
        }

        private static class GenericItem implements Item {

            protected final int numberOfItem;
            protected final String description;
            protected final BigDecimal price;
            private final BigDecimal taxes;

            public GenericItem(int numberOfItem, String description, BigDecimal price, BigDecimal taxes) {
                this.numberOfItem = numberOfItem;
                this.description = description;
                this.price = price;
                this.taxes = taxes;
            }

            @Override
            public BigDecimal calculatePrice() {
                return price.multiply(taxes, new MathContext(4, RoundingMode.HALF_UP));
            }

            @Override
            public String printDescription() {
                return numberOfItem + " " + description + ": " + calculatePrice();
            }
        }

        private static class ItemFactory {

            public ItemFactory() {
            }

            public Item createItem(int itemNumber, String itemDescription, BigDecimal price) {

                if(!itemDescription.contains("book") && !itemDescription.contains("chocolates")){

                    return new ItemTaxed(itemNumber, itemDescription, price);
                }
                if(itemDescription.contains("imported")){
                    return new ImportedItem(itemNumber, itemDescription, price);
                }
                return new ItemWithoutTaxed(itemNumber, itemDescription, price);
            }

            private static class ItemTaxed extends GenericItem {

                public ItemTaxed(int itemNumber, String itemDescription, BigDecimal price) {
                    super(itemNumber, itemDescription, price, new BigDecimal("1.10"));
                }
            }

            private static class ItemWithoutTaxed extends GenericItem {
                public ItemWithoutTaxed(int itemNumber, String itemDescription, BigDecimal price) {
                    super(itemNumber, itemDescription, price, new BigDecimal("1"));
                }
            }

            private static class ImportedItem extends GenericItem {
                public ImportedItem(int itemNumber, String itemDescription, BigDecimal price) {
                    super(itemNumber, itemDescription, price, new BigDecimal("1.05"));
                }
            }
        }
    }
}