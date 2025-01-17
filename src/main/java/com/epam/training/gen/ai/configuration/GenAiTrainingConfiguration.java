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
import com.epam.training.gen.ai.agent.plugin.KnowledgeSearchPlugin;
import com.epam.training.gen.ai.vector.VectorStore;
import com.epam.training.gen.ai.vector.impl.VectorStoreImpl;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.aiservices.openai.textembedding.OpenAITextEmbeddingGenerationService;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(DialConnectionProperties.class)
public class GenAiTrainingConfiguration {

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
                                 CurrencyConverterPlugin currencyConverterPlugin, KnowledgeSearchPlugin knowledgeSearchPlugin) {
        return Kernel.builder()
                .withPlugin(KernelPluginFactory.createFromObject(currencyConverterPlugin, "ExchangeRatePlugin"))
                .withPlugin(KernelPluginFactory.createFromObject(knowledgeSearchPlugin, "KnowledgeSearchPlugin"))
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
    public KnowledgeSearchPlugin knowledgeSearchPlugin(OpenAITextEmbeddingGenerationService openAITextEmbeddingGenerationService,
                                                       VectorStore ragVectorStore) {
        return new KnowledgeSearchPlugin(openAITextEmbeddingGenerationService, ragVectorStore);
    }

    @Bean
    public QdrantClient qdrantClient() {
        return new QdrantClient(QdrantGrpcClient.newBuilder("localhost", 6334, false)
                .build());
    }

    @Bean
    public VectorStore embeddingsVectorStore(QdrantClient qdrantClient) {
        return new VectorStoreImpl(qdrantClient, "embeddings");
    }

    @Bean
    public VectorStore ragVectorStore(QdrantClient qdrantClient) {
        return new VectorStoreImpl(qdrantClient, "rag");
    }
}
