package it.pipitone.matteo.taxes_sales_kata;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        assertThat(receipt, is("1 book: 12.49\nSales Taxes: 0.00\n"+
                "Total: 12.49"));
    }

    @Test
    public void parseOneItemWithTaxes() {
        Bucket bucket = new Bucket("1 music CD at 14.99");
        String receipt = bucket.printReceipt();
        assertThat(receipt, is("1 music CD: 16.49\nSales Taxes: 1.50\n"+
                "Total: 16.49"));
    }

    @Test
    public void parseOneItemImported() {
        Bucket bucket = new Bucket("1 imported box of chocolates at 10.00");
        String receipt = bucket.printReceipt();
        assertThat(receipt, is("1 imported box of chocolates: 10.50\nSales Taxes: 0.50\nTotal: 10.50"));
    }

    @Test
    public void parseImportedItemWithTaxes() {
        Bucket bucket = new Bucket("1 imported bottle of perfume at 47.50");
        String receipt = bucket.printReceipt();
        assertThat(receipt, is("1 imported bottle of perfume: 54.65\nSales Taxes: 7.15\nTotal: 54.65"));
    }

    private static class Bucket {

        private final List<Item> genericItem = new ArrayList<>();

        public Bucket(String itemsString) {
            String[] items = itemsString.split("\n");
            String regex = "(^[0-9]*) ([a-zA-Z ]*) (at) ([0-9.]*)";
            Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

            for (int i = 0; i < items.length; i++) {
                Matcher matcher = pattern.matcher(items[i]);
                boolean found = matcher.find();
                int itemNumber = Integer.parseInt(matcher.group(1));
                String itemDescription = matcher.group(2);
                BigDecimal price = new BigDecimal(matcher.group(4));
                genericItem.add(new ItemFactory().createItem(itemNumber, itemDescription, price));
            }

        }

        public String printReceipt() {
            String itemList = genericItem.stream().map(Item::printDescription).collect(Collectors.joining("\n"));
            String total = genericItem.stream().map(Item::calculatePrice).reduce(BigDecimal.ZERO, BigDecimal::add).toEngineeringString();
            String taxes = genericItem.stream().map(Item::getTaxes).reduce(BigDecimal.ZERO, BigDecimal::add).toEngineeringString();
            return itemList+ "\n" +
                    "Sales Taxes: " + taxes + "\n" +
                    "Total: " + total;
        }

        private static class GenericItem implements Item {

            protected final int numberOfItem;
            protected final String description;
            protected final BigDecimal price;
            private final BigDecimal taxesPercents;
            private BigDecimal taxes;

            public GenericItem(int numberOfItem, String description, BigDecimal price, BigDecimal taxesPercents) {
                this.numberOfItem = numberOfItem;
                this.description = description;
                this.price = price;
                this.taxesPercents = taxesPercents;
            }

            @Override
            public BigDecimal calculatePrice() {
                taxes = price.multiply(this.taxesPercents.subtract(BigDecimal.ONE), new MathContext(3, RoundingMode.HALF_UP));

                BigDecimal result = price.add(taxes);
                if(description.contains("imported")){
                    BigDecimal decimalValue= result.remainder(BigDecimal.ONE).movePointRight(result.scale()).abs();
                    BigDecimal decimalValueRounded = BigDecimal.valueOf(5 * (Math.ceil(Math.abs(decimalValue.divide(new BigDecimal("5"), RoundingMode.HALF_UP).doubleValue())))).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                    BigDecimal taxesToLeave = decimalValue.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                    taxes = taxes.subtract(taxesToLeave).add(decimalValueRounded).setScale(2, RoundingMode.HALF_UP);
                    result =  result.subtract(taxesToLeave).add(decimalValueRounded).setScale(2, RoundingMode.HALF_UP);
                }
                return result;
            }

            @Override
            public String printDescription() {
                return numberOfItem + " " + description + ": " + calculatePrice();
            }

            @Override
            public BigDecimal getTaxes() {
                return taxes;
            }
        }

        private static class ItemFactory {

            public ItemFactory() {
            }

            public Item createItem(int itemNumber, String itemDescription, BigDecimal price) {

                boolean isImportedItem = itemDescription.contains("imported");
                
                if(!itemDescription.contains("book") && !itemDescription.contains("chocolate")){
                    if(isImportedItem){
                        return new ImportedItem(itemNumber, itemDescription, price);
                    }
                    return new ItemTaxed(itemNumber, itemDescription, price);
                }
                if(isImportedItem){
                    return new ImportedItemWithoutTaxes(itemNumber, itemDescription, price);
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

            private static class ImportedItemWithoutTaxes extends GenericItem {
                public ImportedItemWithoutTaxes(int itemNumber, String itemDescription, BigDecimal price) {
                    super(itemNumber, itemDescription, price, new BigDecimal("1.05"));
                }
            }

            private static class ImportedItem extends GenericItem{
                public ImportedItem(int itemNumber, String itemDescription, BigDecimal price) {
                    super(itemNumber, itemDescription, price, new BigDecimal("1.15"));
                }
            }
        }
    }
}