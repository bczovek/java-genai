package com.epam.training.gen.ai.search.service.impl;

import com.epam.training.gen.ai.search.service.SemanticSearchService;
import com.epam.training.gen.ai.vector.VectorStore;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.textembedding.OpenAITextEmbeddingGenerationService;
import com.microsoft.semantickernel.services.ServiceNotFoundException;
import com.microsoft.semantickernel.services.textembedding.Embedding;
import io.qdrant.client.ValueFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@AllArgsConstructor
@Service
public class SemanticSearchServiceImpl implements SemanticSearchService {

    protected static final String TEXT_FIELD_NAME = "originText";
    private final Kernel agentKernel;
    private final VectorStore embeddingsVectorStore;

    @Override
    public List<Float> embed(String text) throws ServiceNotFoundException {
        OpenAITextEmbeddingGenerationService embeddingService = agentKernel.getService(OpenAITextEmbeddingGenerationService.class);
        Embedding embedding = embeddingService.generateEmbeddingAsync(text)
                .block();
        if (Objects.nonNull(embedding)) {
            return embedding.getVector();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void storeEmbedding(String originText, List<Float> embedding) throws ExecutionException, InterruptedException {
        embeddingsVectorStore.insert(embedding, Map.of(TEXT_FIELD_NAME, ValueFactory.value(originText)));
    }

    @Override
    public List<String> semanticSearch(List<Float> queryEmbedding) throws ExecutionException, InterruptedException {
        return embeddingsVectorStore.searchTexts(queryEmbedding, 3, TEXT_FIELD_NAME, null);
    }
}
