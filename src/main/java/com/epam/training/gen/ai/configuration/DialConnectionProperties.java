package com.epam.training.gen.ai.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(DialConnectionProperties.PREFIX)
public record DialConnectionProperties(String endPoint, String key, Deployments deployments) {
    public static final String PREFIX = "client.dial";
    record Deployments(String openai, String mistral) {
    }
}
