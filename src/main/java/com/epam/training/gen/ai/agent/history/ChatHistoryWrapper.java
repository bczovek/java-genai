package com.epam.training.gen.ai.agent.history;

import com.azure.ai.openai.models.CompletionsUsage;
import com.epam.training.gen.ai.agent.model.ChatMessage;
import com.epam.training.gen.ai.agent.model.ChatOutput;
import com.microsoft.semantickernel.services.chatcompletion.AuthorRole;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class ChatHistoryWrapper {

    private final long id;
    private final ChatHistory chatHistory;
    private final String model;

    public void addSystemMessage(String systemMessage) {
        chatHistory.addSystemMessage(systemMessage);
    }

    public void addUserMessage(String message) {
        chatHistory.addUserMessage(message);
    }

    public boolean hasNoSystemMessage() {
        for (ChatMessageContent<?> message : chatHistory) {
            if (message.getAuthorRole().equals(AuthorRole.SYSTEM)) {
                return false;
            }
        }
        return true;
    }

    public void addChatResults(String result) {
        if(Objects.nonNull(result)) {
            chatHistory.addAssistantMessage(result);
        }
    }

    public ChatOutput createChatOutput() {
        List<ChatMessage> messages = new ArrayList<>();
        for (ChatMessageContent<?> message : chatHistory) {
            messages.add(new ChatMessage(message.getAuthorRole().toString(), message.getContent()));
        }
        return new ChatOutput(messages, model);
    }
}
