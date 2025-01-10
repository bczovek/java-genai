package com.epam.training.gen.ai.agent.plugin;

import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CurrencyConverterPlugin {

    private final RestTemplate restTemplate;
    private final String exchangeRateApiUrl;

    public CurrencyConverterPlugin(RestTemplate restTemplate, String exchangeRateApiUrl) {
        this.restTemplate = restTemplate;
        this.exchangeRateApiUrl = exchangeRateApiUrl;
    }

    @DefineKernelFunction(name = "exchange", description = "Converts currencies. Takes in an amount of money in one currency referenced by "
            + "it's currency code and converts it to one or more currencies (also referenced by their currency codes) based on the latest exchange rate.",
            returnType = "java.util.Map")
    public Map<String, Double> exchange(
            @KernelFunctionParameter(name = "amount", description = "Amount of money to convert to the target currencies", defaultValue = "1") int amount,
            @KernelFunctionParameter(name = "sourceCurrencyCode", description = "The currency code of the currency to convert from") String sourceCurrencyCode,
            @KernelFunctionParameter(name = "targetCurrencyCodes", description = "The currency code or codes to convert into. Separated by comma") String targetCurrencyCodes) {

        ExchangeRates exchangeRates =
                restTemplate.getForEntity(UriComponentsBuilder.fromUriString(exchangeRateApiUrl)
                        .path(sourceCurrencyCode)
                        .build()
                        .toUri(), ExchangeRates.class).getBody();

        String[] targetCurrencies = targetCurrencyCodes.split(",");
        Map<String, Double> result = new HashMap<>();
        Arrays.stream(targetCurrencies)
                .forEach(s -> {
                    Double exchangeRate = exchangeRates.rates.get(s);
                    result.put(s, amount * exchangeRate);
                });
        return result;
    }

    private record ExchangeRates(Map<String, Double> rates) { }
}
