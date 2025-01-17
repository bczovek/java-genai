package com.epam.training.gen.ai.configuration;

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
import com.microsoft.semantickernel.aiservices.openai.textembedding.OpenAITextEmbeddingGenerationService;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.ExecutionException;

@Configuration
@EnableConfigurationProperties(DialConnectionProperties.class)
public class GenAiTrainingConfiguration {

    protected static final String COLLECTION_NAME = "embeddings";
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
                .withModelId(connectionProperties.models().openai())
                .withOpenAIAsyncClient(client)
                .build();
    }

    @Bean
    public MistralDialChatCompletion mistralCompletionService(OpenAIAsyncClient client, DialConnectionProperties connectionProperties) {
        return new MistralDialChatCompletion(client, connectionProperties.models().mistral());
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
                                 OpenAITextEmbeddingGenerationService textEmbeddingGenerationService,
                                 CurrencyConverterPlugin currencyConverterPlugin) {
        return Kernel.builder()
                .withPlugin(KernelPluginFactory.createFromObject(currencyConverterPlugin, "ExchangeRatePlugin"))
                .withAIService(OpenAIChatCompletion.class, openAiCompletionService)
                .withAIService(MistralDialChatCompletion.class, mistralCompletionService)
                .withAIService(OpenAITextEmbeddingGenerationService.class, textEmbeddingGenerationService)
                .withServiceSelector(CustomAiServiceSelector::new)
                .build();
    }

    @Bean
    public ChatHistoryRepository chatHistoryRepository() {
        return new InMemoryChatHistoryRepository();
    }

    @Bean
    public OpenAITextEmbeddingGenerationService openAITextEmbeddingGenerationService(DialConnectionProperties dialConnectionProperties,
                                                                               OpenAIAsyncClient openAIAsyncClient) {
        return OpenAITextEmbeddingGenerationService.builder()
                .withOpenAIAsyncClient(openAIAsyncClient)
                .withModelId(dialConnectionProperties.models().embedding())
                .build();
    }

    @Bean
    public QdrantClient qdrantClient() throws ExecutionException, InterruptedException {
        QdrantClient qdrantClient = new QdrantClient(QdrantGrpcClient.newBuilder("localhost", 6334, false)
                .build());
        Boolean isCollectionExists = qdrantClient.collectionExistsAsync(COLLECTION_NAME).get();
        if(!isCollectionExists) {
            qdrantClient.createCollectionAsync(COLLECTION_NAME, Collections.VectorParams.newBuilder()
                            .setDistance(Collections.Distance.Euclid)
                            .setSize(1536)
                            .build())
                    .get();
        }
        return qdrantClient;
    }
}
