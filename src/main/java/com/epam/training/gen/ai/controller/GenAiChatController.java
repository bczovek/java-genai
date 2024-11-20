package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.model.ChatInput;
import com.epam.training.gen.ai.model.ChatOutput;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
public class GenAiChatController {

    private final ChatCompletionService chatCompletionService;
    private final ChatHistory chatHistory;
    private final Kernel kernel;

    @PostMapping("/chat")
    public List<ChatOutput> chat(@RequestBody ChatInput chatInput) {
        chatHistory.addUserMessage(chatInput.input());
        List<ChatMessageContent<?>> results = chatCompletionService
                .getChatMessageContentsAsync(chatHistory, null, null)
                .block();
        for(ChatMessageContent<?> message : results) {
            chatHistory.addMessage(message);
        }
        List<ChatOutput> response = new ArrayList<>();
        chatHistory.forEach(message -> response.add(new ChatOutput(message.getAuthorRole().toString(), message.getContent())));
        return response;
    }
}
