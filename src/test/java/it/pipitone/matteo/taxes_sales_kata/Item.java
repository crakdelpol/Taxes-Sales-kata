package it.pipitone.matteo.taxes_sales_kata;

import java.math.BigDecimal;

public interface Item {

    BigDecimal calculatePrice();

    String printDescription();

    BigDecimal getTaxes();
}
