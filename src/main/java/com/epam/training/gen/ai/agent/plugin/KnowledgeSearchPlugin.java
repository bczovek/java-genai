package com.epam.training.gen.ai.agent.plugin;

import com.epam.training.gen.ai.vector.VectorStore;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.services.textembedding.TextEmbeddingGenerationService;
import lombok.RequiredArgsConstructor;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
public class KnowledgeSearchPlugin {

    private final TextEmbeddingGenerationService textEmbeddingGenerationService;
    private final VectorStore ragVectorStore;

    @DefineKernelFunction(name = "search", description = "Search for a document similar to the given query.",
            returnType = "java.util.List")
    public List<String> searchKnowledge(String query)
            throws ExecutionException, InterruptedException {
        List<Float> vector = textEmbeddingGenerationService.generateEmbeddingAsync(query).block()
                .getVector();
        return ragVectorStore.searchTexts(vector, 3, "knowledge", Collections.emptyList());
    }
}
