package com.epam.training.gen.ai.agent.history.repository;

import com.epam.training.gen.ai.agent.history.ChatHistoryWrapper;
import java.util.Optional;

public interface ChatHistoryRepository {

    Optional<ChatHistoryWrapper> findById(Long id);

    ChatHistoryWrapper create(String model);

    void save(ChatHistoryWrapper chatHistoryWrapper);
}
