package com.epam.training.gen.ai.vector;

import io.qdrant.client.grpc.JsonWithInt;
import io.qdrant.client.grpc.Points;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface VectorStore {

    void insert(List<Float> vector, Map<String, JsonWithInt.Value> payload)
            throws ExecutionException, InterruptedException;

    List<Points.ScoredPoint> searchVectors(List<Float> queryVector, int limit, List<String> includePayloadFields)
            throws ExecutionException, InterruptedException;

    List<String> searchTexts(List<Float> queryVector, int limit, String textFieldName, List<String> includePayloadFields)
            throws ExecutionException, InterruptedException;
}
