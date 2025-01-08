package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.model.ChatInput;
import com.epam.training.gen.ai.model.ChatOutput;
import com.epam.training.gen.ai.service.GenAiChatService;
import com.microsoft.semantickernel.services.ServiceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class GenAiChatController {

    private final GenAiChatService genAiChatService;

    @PostMapping("/chat/{id}")
    public ChatOutput chat(@RequestBody ChatInput chatInput, @PathVariable("id") String id, @RequestParam(defaultValue = "0.5") double temp)
            throws ServiceNotFoundException {
        return genAiChatService.chat(chatInput, Long.parseLong(id), temp);
    }

    @GetMapping("openai/createChat")
    public Long createOpenAiChat() {
        return genAiChatService.createChat("OpenAI");
    }
}
