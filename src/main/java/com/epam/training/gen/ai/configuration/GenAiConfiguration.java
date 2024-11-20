package com.epam.training.gen.ai.configuration;

import static com.azure.ai.openai.OpenAIServiceVersion.V2024_02_01;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DialConnectionProperties.class)
public class GenAiConfiguration {

    @Bean
    public OpenAIAsyncClient openAIAsyncClient(DialConnectionProperties connectionProperties) {
        return new OpenAIClientBuilder()
                .endpoint(connectionProperties.endPoint())
                .credential(new AzureKeyCredential(connectionProperties.key()))
                .serviceVersion(V2024_02_01)
                .buildAsyncClient();
    }

    @Bean
    public ChatCompletionService chatCompletionService(OpenAIAsyncClient client, DialConnectionProperties connectionProperties) {
        return OpenAIChatCompletion.builder()
                .withModelId(connectionProperties.deploymentName())
                .withOpenAIAsyncClient(client)
                .build();
    }

    @Bean
    public ChatHistory chatHistory() {
        return new ChatHistory();
    }

    @Bean
    public Kernel semanticKernel(ChatCompletionService chatCompletionService) {
        return Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .build();
    }
}
