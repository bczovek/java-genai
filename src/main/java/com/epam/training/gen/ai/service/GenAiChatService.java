package com.epam.training.gen.ai.service;

import com.azure.ai.openai.models.CompletionsUsage;
import com.epam.training.gen.ai.model.ChatInput;
import com.epam.training.gen.ai.model.ChatMessage;
import com.epam.training.gen.ai.model.ChatOutput;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.services.chatcompletion.AuthorRole;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GenAiChatService {

    private final ChatCompletionService chatCompletionService;
    private final ChatHistory chatHistory;
    private final Kernel kernel;

    public ChatOutput chat(ChatInput chatInput, double temp) {
        addSystemMessageToHistory(chatInput);
        chatHistory.addUserMessage(chatInput.getInput());
        InvocationContext invocationContext = InvocationContext.builder()
                .withPromptExecutionSettings(PromptExecutionSettings.builder()
                        .withTemperature(temp)
                        .build())
                .build();
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
        if(results != null) {
            for (ChatMessageContent<?> message : results) {
                chatHistory.addMessage(message);
            }
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
