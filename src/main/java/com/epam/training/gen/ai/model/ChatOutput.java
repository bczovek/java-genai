package com.epam.training.gen.ai.model;

import java.util.List;

public record ChatOutput(List<ChatMessage> messages, String model) {
}
