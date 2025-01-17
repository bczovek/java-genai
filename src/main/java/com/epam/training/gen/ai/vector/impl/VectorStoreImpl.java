package com.epam.training.gen.ai.vector.impl;

import com.epam.training.gen.ai.vector.VectorStore;
import io.qdrant.client.PointIdFactory;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.VectorsFactory;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.JsonWithInt;
import io.qdrant.client.grpc.Points;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;


@RequiredArgsConstructor
public class VectorStoreImpl implements VectorStore {

    private final QdrantClient qdrantClient;
    private final String collectionName;
    private final AtomicLong id = new AtomicLong();

    @PostConstruct
    public void init() throws ExecutionException, InterruptedException {
        boolean isCollectionExists = qdrantClient.collectionExistsAsync(collectionName).get();
        if(!isCollectionExists) {
            qdrantClient.createCollectionAsync(collectionName, Collections.VectorParams.newBuilder()
                            .setDistance(Collections.Distance.Euclid)
                            .setSize(1536)
                            .build())
                    .get();
        }
    }

    @Override
    public void insert(List<Float> vector, Map<String, JsonWithInt.Value> payload) throws ExecutionException, InterruptedException {
        Points.PointStruct pointStruct = Points.PointStruct.newBuilder()
                .setId(PointIdFactory.id(id.incrementAndGet()))
                .setVectors(VectorsFactory.vectors(vector))
                .putAllPayload(payload)
                .build();
        qdrantClient.upsertAsync(collectionName, List.of(pointStruct)).get();
    }

    @Override
    public List<Points.ScoredPoint> searchVectors(List<Float> queryVector, int limit, List<String> includePayloadFields)
            throws ExecutionException, InterruptedException {
        return qdrantClient.searchAsync(Points.SearchPoints.newBuilder()
                        .setWithPayload(Points.WithPayloadSelector.newBuilder()
                                .setInclude(Points.PayloadIncludeSelector.newBuilder()
                                        .addAllFields(includePayloadFields)
                                        .build())
                                .build())
                        .setCollectionName(collectionName)
                        .addAllVector(queryVector)
                        .setLimit(limit)
                        .build())
                .get();
    }

    @Override
    public List<String> searchTexts(List<Float> queryVector, int limit, String textFieldName, List<String> includePayloadFields)
            throws ExecutionException, InterruptedException {
        List<String> include = new ArrayList<>(includePayloadFields);
        include.add(textFieldName);
        List<Points.ScoredPoint> vectors = searchVectors(queryVector, limit, include);
        return vectors.stream()
                .map(scoredPoint -> scoredPoint.getPayloadMap().get(textFieldName).getStringValue())
                .toList();
    }
}
