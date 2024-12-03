package com.epam.training.gen.ai.service;

import com.azure.ai.openai.models.CompletionsUsage;
import com.epam.training.gen.ai.model.ChatInput;
import com.epam.training.gen.ai.model.ChatMessage;
import com.epam.training.gen.ai.model.ChatOutput;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.services.ServiceNotFoundException;
import com.microsoft.semantickernel.services.chatcompletion.AuthorRole;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class GenAiChatService {

    private final ChatHistory chatHistory;
    private final Kernel kernel;

    public ChatOutput chat(ChatInput chatInput, double temp) throws ServiceNotFoundException {
        addSystemMessageToHistory(chatInput);
        chatHistory.addUserMessage(chatInput.getInput());
        InvocationContext invocationContext = InvocationContext.builder()
                .withPromptExecutionSettings(PromptExecutionSettings.builder()
                        .withTemperature(temp)
                        .build())
                .build();
        ChatCompletionService chatCompletionService = kernel.getService(ChatCompletionService.class);
        List<ChatMessageContent<?>> results = chatCompletionService
                .getChatMessageContentsAsync(chatHistory, kernel, invocationContext)
                .block();
        addResultsToHistory(results);
        return createChatOutput();
    }

    private void addSystemMessageToHistory(ChatInput chatInput) {
        if (chatInput.getSystemMessage() != null && historyHasNoSystemMessage(chatHistory)) {
            chatHistory.addSystemMessage(chatInput.getSystemMessage());
        }
    }

    private boolean historyHasNoSystemMessage(ChatHistory chatHistory) {
        for(ChatMessageContent<?> message : chatHistory) {
            if(message.getAuthorRole().equals(AuthorRole.SYSTEM)) {
                return false;
            }
        }
        return true;
    }

    private void addResultsToHistory(List<ChatMessageContent<?>> results) {
        if(Objects.nonNull(results)) {
            results.forEach(chatHistory::addMessage);
        }
    }

    private ChatOutput createChatOutput() {
        List<ChatMessage> messages = new ArrayList<>();
        int totalTokenUsage = 0;
        for (ChatMessageContent<?> message : chatHistory) {
            if (message.getMetadata() != null) {
                totalTokenUsage += getUsage(message).getTotalTokens();
            }
            messages.add(new ChatMessage(message.getAuthorRole().toString(), message.getContent()));
        }
        return new ChatOutput(messages, totalTokenUsage);
    }

    private static CompletionsUsage getUsage(ChatMessageContent<?> message) {
        return (CompletionsUsage) message.getMetadata().getUsage();
    }
}
