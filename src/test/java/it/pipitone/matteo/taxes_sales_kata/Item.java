package it.pipitone.matteo.taxes_sales_kata;

import java.math.BigDecimal;

public interface Item {

    String getName();

    Integer getNumber();

    BigDecimal calculatePrice();

    String printDescription(Integer number, String name, BigDecimal price);
}
