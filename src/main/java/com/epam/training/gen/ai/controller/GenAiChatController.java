package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.model.ChatInput;
import com.epam.training.gen.ai.model.ChatOutput;
import com.epam.training.gen.ai.service.GenAiChatService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class GenAiChatController {

    private final GenAiChatService genAiChatService;

    @PostMapping("/chat")
    public ChatOutput chat(@RequestBody ChatInput chatInput, @RequestParam(defaultValue = "0.5") double temp) {
        return genAiChatService.chat(chatInput, temp);
    }
}
