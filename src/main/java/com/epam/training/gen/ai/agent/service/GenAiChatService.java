package com.epam.training.gen.ai.agent.service;

import com.epam.training.gen.ai.agent.history.ChatHistoryWrapper;
import com.epam.training.gen.ai.agent.model.ChatInput;
import com.epam.training.gen.ai.agent.model.ChatOutput;
import com.epam.training.gen.ai.agent.history.repository.ChatHistoryRepository;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionArguments;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionFromPrompt;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
@AllArgsConstructor
public class GenAiChatService {

    private final ChatHistoryRepository chatHistoryRepository;
    private final Kernel kernel;

    public ChatOutput chat(ChatInput chatInput, Long id, double temp) {
        ChatHistoryWrapper chatHistory = chatHistoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No chat found with id " + id));
        if(Objects.nonNull(chatInput.getSystemMessage()) && chatHistory.hasNoSystemMessage()) {
            chatHistory.addSystemMessage(chatInput.getSystemMessage());
        }
        chatHistory.addUserMessage(chatInput.getInput());
        KernelFunctionArguments kernelFunctionArguments = KernelFunctionArguments.builder()
                .withVariable("model", chatHistory.getModel()).build();
        KernelFunction<?> function = KernelFunctionFromPrompt.builder()
                .withTemplate(chatInput.getInput())
                .withDefaultExecutionSettings(PromptExecutionSettings.builder()
                        .withTemperature(temp)
                        .build())
                .withOutputVariable("result", "java.lang.String")
                .build();
        String response = (String) kernel.invokeAsync(function)
                .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
                .withArguments(kernelFunctionArguments).block()
                .getResult();
        chatHistory.addChatResults(response);
        chatHistoryRepository.save(chatHistory);
        return chatHistory.createChatOutput();
    }

    public Long createChat(String model) {
        ChatHistoryWrapper chatHistory = chatHistoryRepository.create(model);
        return chatHistory.getId();
    }
}
