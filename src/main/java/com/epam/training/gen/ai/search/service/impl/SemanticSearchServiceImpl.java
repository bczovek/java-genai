package com.epam.training.gen.ai.search.service.impl;

import com.epam.training.gen.ai.search.service.SemanticSearchService;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.textembedding.OpenAITextEmbeddingGenerationService;
import com.microsoft.semantickernel.services.ServiceNotFoundException;
import com.microsoft.semantickernel.services.textembedding.Embedding;
import io.qdrant.client.PointIdFactory;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.ValueFactory;
import io.qdrant.client.VectorsFactory;
import io.qdrant.client.grpc.Points;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

@AllArgsConstructor
@Service
public class SemanticSearchServiceImpl implements SemanticSearchService {

    protected static final String ORIGIN_TEXT_PAYLOAD_KEY = "originText";
    protected static final String VECTORS_COLLECTION_NAME = "vectors";
    private final Kernel kernel;
    private final QdrantClient qdrantClient;
    private final AtomicLong id = new AtomicLong();

    @Override
    public List<Float> embed(String text) throws ServiceNotFoundException {
        OpenAITextEmbeddingGenerationService embeddingService = kernel.getService(OpenAITextEmbeddingGenerationService.class);
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
        Points.PointStruct vector = Points.PointStruct.newBuilder()
                .setId(PointIdFactory.id(id.incrementAndGet()))
                .setVectors(VectorsFactory.vectors(embedding))
                .putAllPayload(Map.of(ORIGIN_TEXT_PAYLOAD_KEY, ValueFactory.value(originText)))
                .build();
        qdrantClient.upsertAsync(VECTORS_COLLECTION_NAME, List.of(vector)).get();
    }

    @Override
    public List<String> semanticSearch(String text, List<Float> queryEmbedding) throws ExecutionException, InterruptedException {
        List<Points.ScoredPoint> points = qdrantClient.searchAsync(Points.SearchPoints.newBuilder()
                        .setWithPayload(Points.WithPayloadSelector.newBuilder()
                                .setInclude(Points.PayloadIncludeSelector.newBuilder()
                                        .addFields(ORIGIN_TEXT_PAYLOAD_KEY)
                                        .build())
                                .build())
                        .setCollectionName(VECTORS_COLLECTION_NAME)
                        .addAllVector(queryEmbedding)
                        .setLimit(3)
                        .build())
                .get();
        return points.stream()
                .map(scoredPoint -> scoredPoint.getPayloadMap().get(ORIGIN_TEXT_PAYLOAD_KEY).getStringValue())
                .toList();
    }
}
