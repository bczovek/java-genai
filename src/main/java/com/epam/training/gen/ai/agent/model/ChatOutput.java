package com.epam.training.gen.ai.agent.model;

import java.util.List;

public record ChatOutput(List<ChatMessage> messages, String model) {
}
