package com.epam.training.gen.ai.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(DialConnectionProperties.PREFIX)
public record DialConnectionProperties(String endPoint, String key, Models models) {
    public static final String PREFIX = "client.dial";
    record Models(String openai, String mistral, String embedding) {
    }
}
