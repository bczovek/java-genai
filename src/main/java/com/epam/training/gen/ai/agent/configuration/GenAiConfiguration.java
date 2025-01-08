package com.epam.training.gen.ai.agent.configuration;

import static com.azure.ai.openai.OpenAIServiceVersion.V2024_02_01;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.epam.training.gen.ai.agent.completion.mistral.MistralDialChatCompletion;
import com.epam.training.gen.ai.agent.history.repository.ChatHistoryRepository;
import com.epam.training.gen.ai.agent.history.repository.impl.InMemoryChatHistoryRepository;
import com.epam.training.gen.ai.agent.plugin.CurrencyConverterPlugin;
import com.epam.training.gen.ai.agent.selector.CustomAiServiceSelector;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(DialConnectionProperties.class)
public class GenAiConfiguration {

    @Value("${plugin.exchange.api.url}")
    private String exchangeRateApiUrl;

    @Bean
    public OpenAIAsyncClient openAIAsyncClient(DialConnectionProperties connectionProperties) {
        return new OpenAIClientBuilder()
                .endpoint(connectionProperties.endPoint())
                .credential(new AzureKeyCredential(connectionProperties.key()))
                .serviceVersion(V2024_02_01)
                .buildAsyncClient();
    }

    @Bean
    public OpenAIChatCompletion openAiCompletionService(OpenAIAsyncClient client, DialConnectionProperties connectionProperties) {
        return OpenAIChatCompletion.builder()
                .withModelId(connectionProperties.deployments().openai())
                .withOpenAIAsyncClient(client)
                .build();
    }

    @Bean
    public MistralDialChatCompletion mistralCompletionService(OpenAIAsyncClient client, DialConnectionProperties connectionProperties) {
        return new MistralDialChatCompletion(client, connectionProperties.deployments().mistral());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CurrencyConverterPlugin exchangeRatePlugin(RestTemplate restTemplate) {
        return new CurrencyConverterPlugin(restTemplate, exchangeRateApiUrl);
    }

    @Bean
    public Kernel semanticKernel(OpenAIChatCompletion openAiCompletionService, MistralDialChatCompletion mistralCompletionService,
                                 CurrencyConverterPlugin currencyConverterPlugin) {
        return Kernel.builder()
                .withPlugin(KernelPluginFactory.createFromObject(currencyConverterPlugin, "ExchangeRatePlugin"))
                .withAIService(OpenAIChatCompletion.class, openAiCompletionService)
                .withAIService(MistralDialChatCompletion.class, mistralCompletionService)
                .withServiceSelector(CustomAiServiceSelector::new)
                .build();
    }

    @Bean
    public ChatHistoryRepository chatHistoryRepository() {
        return new InMemoryChatHistoryRepository();
    }
}
