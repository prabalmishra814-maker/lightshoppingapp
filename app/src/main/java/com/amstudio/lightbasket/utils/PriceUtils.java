package com.amstudio.lightbasket.utils;

import java.util.Locale;

public class PriceUtils {

    /**
     * Parses a price string (e.g., "₹1,299.50") into a double.
     * Removes currency symbols, commas, and other non-numeric characters except the decimal point.
     */
    public static double parsePrice(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) return 0;
        try {
            String clean = priceStr.replaceAll("[^0-9.]", "");
            if (clean.isEmpty()) return 0;
            return Double.parseDouble(clean);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Returns the price as an integer for simple displays or transactions.
     */
    public static int parsePriceInt(String priceStr) {
        return (int) Math.round(parsePrice(priceStr));
    }

    /**
     * Formats an amount with currency symbol and thousands separator.
     */
    public static String formatPrice(double amount) {
        return "₹" + String.format(Locale.getDefault(), "%,.0f", amount);
    }
}

