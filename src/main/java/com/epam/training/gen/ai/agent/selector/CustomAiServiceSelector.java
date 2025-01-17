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
import java.util.Objects;

public class CustomAiServiceSelector extends BaseAIServiceSelector {
    public CustomAiServiceSelector(AIServiceCollection services) {
        super(services);
    }

    @Nullable
    @Override
    protected <T extends AIService> AIServiceSelection<T> trySelectAIService(Class<T> aClass, @Nullable KernelFunction<?> kernelFunction,
                                                                             @Nullable KernelFunctionArguments kernelFunctionArguments,
                                                                             Map<Class<? extends AIService>, AIService> map) {
        AIService service = null;
        PromptExecutionSettings executionSettings = null;
        if(Objects.nonNull(kernelFunction)) {
             executionSettings = kernelFunction.getExecutionSettings()
                    .values().stream().findFirst().orElse(null);
            if (Objects.nonNull(kernelFunctionArguments)) {
                String model = kernelFunctionArguments.get("model").getValue(String.class);
                switch (model) {
                    case "OpenAI" -> service = services.get(OpenAIChatCompletion.class);
                    case "Mistral" -> service = services.get(MistralDialChatCompletion.class);
                    default -> throw new IllegalStateException("Unexpected value: " + model);
                }
            }
        } else {
            service = services.get(aClass);
        }


        return new AIServiceSelection<>((T) service, executionSettings);
    }
}
