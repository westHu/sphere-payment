package com.paysphere.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class AmountFormatterUtil {

    public static String formatAmount(BigDecimal amount) {
        return formatAmount(amount.doubleValue());
    }

    public static String formatAmount(double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
        return formatter.format(amount);
    }

    public static void main(String[] args) {
        double amount = 10000.0000;
        String formattedAmount = formatAmount(amount);
        System.out.println(formattedAmount);
    }
}
