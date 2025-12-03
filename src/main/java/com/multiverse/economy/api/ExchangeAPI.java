package com.multiverse.economy.api;

import com.multiverse.economy.models.ExchangeRate;
import com.multiverse.economy.models.Currency;

import java.util.List;

public class ExchangeAPI {

    public static double exchange(String fromCurrency, String toCurrency, double amount, List<ExchangeRate> rates) {
        for (ExchangeRate rate : rates) {
            if (rate.getFromCurrency().equals(fromCurrency) && rate.getToCurrency().equals(toCurrency)) {
                return amount * rate.getRate();
            }
        }
        return 0;
    }

    // Additional exchange logic can be added as needed
}