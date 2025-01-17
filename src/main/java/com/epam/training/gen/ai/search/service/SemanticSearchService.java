package com.epam.training.gen.ai.search.service;

import com.microsoft.semantickernel.services.ServiceNotFoundException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface SemanticSearchService {

    List<Float> embed(String text) throws ServiceNotFoundException;

    void storeEmbedding(String originText, List<Float> embedding) throws ExecutionException, InterruptedException;

    List<String> semanticSearch(String text, List<Float> queryEmbedding) throws ExecutionException, InterruptedException;
}
