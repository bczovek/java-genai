package com.epam.training.gen.ai.search.controller;

import com.epam.training.gen.ai.search.service.impl.SemanticSearchServiceImpl;
import com.microsoft.semantickernel.services.ServiceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@AllArgsConstructor
public class SemanticSearchController {

    private final SemanticSearchServiceImpl semanticSearchServiceImpl;

    @PostMapping("/embed")
    public List<Float> embed(@RequestBody SemanticSearchInput semanticSearchInput) throws ServiceNotFoundException {
        return semanticSearchServiceImpl.embed(semanticSearchInput.text());
    }

    @PostMapping("/store")
    public List<Float> embedAndStore(@RequestBody SemanticSearchInput semanticSearchInput)
            throws ServiceNotFoundException, ExecutionException, InterruptedException {
        List<Float> embedding = semanticSearchServiceImpl.embed(semanticSearchInput.text());
        semanticSearchServiceImpl.storeEmbedding(semanticSearchInput.text(), embedding);
        return embedding;
    }

    @PostMapping("/search")
    public SemanticSearchResult search(@RequestBody SemanticSearchInput semanticSearchInput)
            throws ServiceNotFoundException, ExecutionException, InterruptedException {
        List<Float> embedding = semanticSearchServiceImpl.embed(semanticSearchInput.text());
        List<String> nearestFinds = semanticSearchServiceImpl.semanticSearch(semanticSearchInput.text(), embedding);
        return new SemanticSearchResult(nearestFinds);
    }

    public record SemanticSearchInput(String text) {}
    public record SemanticSearchResult(List<String> nearestFinds) {}
}
