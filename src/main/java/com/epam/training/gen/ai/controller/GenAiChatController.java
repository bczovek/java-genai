package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.model.ChatInput;
import com.epam.training.gen.ai.model.ChatOutput;
import com.epam.training.gen.ai.service.GenAiService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class GenAiChatController {

    private final GenAiService genAiService;

    @PostMapping("/chat")
    public ChatOutput chat(@RequestBody ChatInput chatInput) {
        return genAiService.chat(chatInput);
    }
}
