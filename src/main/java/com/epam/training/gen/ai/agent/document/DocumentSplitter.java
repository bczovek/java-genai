package com.epam.training.gen.ai.agent.document;

import java.util.List;

public interface DocumentSplitter {

    List<String> split(String document);

}
