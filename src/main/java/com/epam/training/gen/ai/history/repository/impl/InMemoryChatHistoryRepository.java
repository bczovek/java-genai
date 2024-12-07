package com.epam.training.gen.ai.history.repository.impl;

import com.epam.training.gen.ai.history.ChatHistoryWrapper;
import com.epam.training.gen.ai.history.repository.ChatHistoryRepository;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryChatHistoryRepository implements ChatHistoryRepository {

    private final AtomicLong generator = new AtomicLong();
    private final ConcurrentHashMap<Long, ChatHistoryWrapper> repository = new ConcurrentHashMap<>();

    @Override
    public Optional<ChatHistoryWrapper> findById(Long id) {
        return Optional.ofNullable(repository.get(id));
    }

    @Override
    public ChatHistoryWrapper create(String model) {
        ChatHistoryWrapper chatHistoryWrapper = new ChatHistoryWrapper(generator.incrementAndGet(), new ChatHistory(), model);
        repository.put(chatHistoryWrapper.getId(), chatHistoryWrapper);
        return chatHistoryWrapper;
    }

    @Override
    public void save(ChatHistoryWrapper chatHistoryWrapper) {
        repository.put(chatHistoryWrapper.getId(), chatHistoryWrapper);
    }
}
