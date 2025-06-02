package com.shannontheoret.duel.component;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class SystemPromptProvider {

    private SystemMessage systemMessage;

    @PostConstruct
    public void init() throws IOException {
        ClassPathResource resource = new ClassPathResource("duel_rules.txt");
        String content = Files.readString(Path.of(resource.getURI()));
        this.systemMessage = new SystemMessage(content);
    }

    public SystemMessage getSystemMessage() {
        return systemMessage;
    }
}