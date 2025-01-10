package com.epam.training.gen.ai.agent.completion.mistral;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;

// Class to call Mistral through DIAL's OpenAI like API. Needed to be able to register both in the kernel.
public class MistralDialChatCompletion extends OpenAIChatCompletion {
    public MistralDialChatCompletion(OpenAIAsyncClient client, String deploymentName) {
        super(client, deploymentName, deploymentName, null);
    }
}
