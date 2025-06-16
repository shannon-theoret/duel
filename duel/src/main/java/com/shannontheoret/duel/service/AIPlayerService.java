package com.shannontheoret.duel.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shannontheoret.duel.component.SystemPromptProvider;
import com.shannontheoret.duel.entity.Game;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.StructuredOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shannontheoret.duel.AIMove;

import java.util.List;

@Service
public class AIPlayerService {
    private final ChatClient chatClient;
    private final SystemPromptProvider systemPromptProvider;
    //private final ChatOptions chatOptions;
    private final ObjectMapper objectMapper;
    private final String initialUserPrompt = "Make the best move given the following current game state. ";
    StructuredOutputConverter<AIMove> converter = new BeanOutputConverter<>(AIMove.class);

    @Autowired
    public AIPlayerService(ChatClient.Builder chatClientBuilder, SystemPromptProvider systemPromptProvider, ObjectMapper objectMapper)  {
        this.chatClient = chatClientBuilder.build();
        this.systemPromptProvider = systemPromptProvider;
        this.objectMapper = objectMapper;
        /*this.chatOptions = OpenAiChatOptions.builder()
                //.model(OpenAiApi.ChatModel.O4_MINI.getValue())
                //.reasoningEffort("high")
                .model(OpenAiApi.ChatModel.O3.getValue())
                .temperature(1.0)
                .build();*/
    }

    public AIMove makeAIMove(Game game, Integer effort) {
        ChatOptions chatOptions;
        switch (effort) {
            case 1: {
                chatOptions = OpenAiChatOptions.builder()
                    .model(OpenAiApi.ChatModel.O4_MINI.getValue())
                    .reasoningEffort("low")
                    .temperature(1.0)
                    .build();
                break;
            }
            case 2: {
                chatOptions = OpenAiChatOptions.builder()
                        .model(OpenAiApi.ChatModel.O4_MINI.getValue())
                        .reasoningEffort("medium")
                        .temperature(1.0)
                        .build();
                break;
            }
            case 3: {
                chatOptions = OpenAiChatOptions.builder()
                        .model(OpenAiApi.ChatModel.O4_MINI.getValue())
                        .reasoningEffort("high")
                        .temperature(1.0)
                        .build();
                break;
            }
            case 4: {
                chatOptions = OpenAiChatOptions.builder()
                        .model(OpenAiApi.ChatModel.O3.getValue())
                        .reasoningEffort("low")
                        .temperature(1.0)
                        .build();
                break;
            }
            case 5: {
                chatOptions = OpenAiChatOptions.builder()
                        .model(OpenAiApi.ChatModel.O3.getValue())
                        .reasoningEffort("medium")
                        .temperature(1.0)
                        .build();
                break;
            }
            case 6: {
                chatOptions = OpenAiChatOptions.builder()
                        .model(OpenAiApi.ChatModel.O3.getValue())
                        .reasoningEffort("high")
                        .temperature(1.0)
                        .build();
                break;
            }
            default: {
                chatOptions = OpenAiChatOptions.builder()
                        .model(OpenAiApi.ChatModel.O4_MINI.getValue())
                        .reasoningEffort("medium")
                        .temperature(1.0)
                        .build();
                break;
            }
        }
        Prompt prompt = new Prompt(List.of(systemPromptProvider.getSystemMessage(), createUserMessage(game)), chatOptions);
        String response = chatClient.prompt(prompt).call().content();
        return converter.convert(response);
    }

    private UserMessage createUserMessage(Game game) {
        try {
            UserMessage userMessage = new UserMessage(initialUserPrompt + objectMapper.writeValueAsString(game));
            return userMessage;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
