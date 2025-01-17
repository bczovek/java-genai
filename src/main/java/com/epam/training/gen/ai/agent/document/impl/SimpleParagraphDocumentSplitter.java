package com.epam.training.gen.ai.agent.document.impl;

import com.epam.training.gen.ai.agent.document.DocumentSplitter;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Component
public class SimpleParagraphDocumentSplitter implements DocumentSplitter {

    private static final String PARAGRAPH_DELIMITER = "\n\n";

    @Override
    public List<String> split(String document) {
        return Arrays.asList(document.split(PARAGRAPH_DELIMITER));
    }
}
