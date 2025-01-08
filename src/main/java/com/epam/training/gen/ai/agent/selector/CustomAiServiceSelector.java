package com.epam.training.gen.ai.agent.selector;

import com.epam.training.gen.ai.agent.completion.mistral.MistralDialChatCompletion;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionArguments;
import com.microsoft.semantickernel.services.AIService;
import com.microsoft.semantickernel.services.AIServiceCollection;
import com.microsoft.semantickernel.services.AIServiceSelection;
import com.microsoft.semantickernel.services.BaseAIServiceSelector;
import javax.annotation.Nullable;
import java.util.Map;

public class CustomAiServiceSelector extends BaseAIServiceSelector {
    public CustomAiServiceSelector(AIServiceCollection services) {
        super(services);
    }

    @Nullable
    @Override
    protected <T extends AIService> AIServiceSelection<T> trySelectAIService(Class<T> aClass, @Nullable KernelFunction<?> kernelFunction,
                                                                             @Nullable KernelFunctionArguments kernelFunctionArguments,
                                                                             Map<Class<? extends AIService>, AIService> map) {
        PromptExecutionSettings executionSettings = kernelFunction.getExecutionSettings()
                .values().stream().findFirst().orElseThrow();
        String model = kernelFunctionArguments.get("model").getValue(String.class);
        T service;
        switch (model) {
            case "OpenAI" -> {
                service = (T) services.get(OpenAIChatCompletion.class);
            }
            case "Mistral" -> {
                service = (T) services.get(MistralDialChatCompletion.class);
            }
            default -> throw new IllegalStateException("Unexpected value: " + model);
        }

        return new AIServiceSelection<>(service, executionSettings);
    }
}
