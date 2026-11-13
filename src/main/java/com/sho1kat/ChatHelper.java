package com.sho1kat;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiStreamingChatModel;
import dev.langchain4j.service.AiServices;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static dev.langchain4j.model.chat.Capability.RESPONSE_FORMAT_JSON_SCHEMA;

public final class ChatHelper {

    private static final Map<Object, MessageWindowChatMemory> MEMORIES = new ConcurrentHashMap<>();

    private ChatHelper() {
    }

    private static String apiKey() {
        String key = System.getenv("GEMINI_API_KEY");
        if (key == null || key.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY is not set.");
        }
        return key;
    }

    private static ChatModel model() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey())
                .modelName("gemini-3.1-flash-lite")
                .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
                .build();
    }

    public static Tutor tutor() {
        return AiServices.builder(Tutor.class)
                .streamingChatModel(GoogleAiGeminiStreamingChatModel.builder()
                        .apiKey(apiKey())
                        .modelName("gemini-3.1-flash-lite")
                        .build())
                .chatModel(model())
                .tools(new Tutor.Tools())
                .chatMemoryProvider(id -> MEMORIES.computeIfAbsent(id,
                        key -> MessageWindowChatMemory.withMaxMessages(20)))
                .build();
    }

    public static void forget() {
        MEMORIES.values().forEach(MessageWindowChatMemory::clear);
        MEMORIES.clear();
    }
}
