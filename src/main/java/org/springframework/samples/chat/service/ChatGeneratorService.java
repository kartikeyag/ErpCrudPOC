package org.springframework.samples.chat.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class ChatGeneratorService {
    private final ChatClient chatClient;

    public ChatGeneratorService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        ChatMemory memory = new InMemoryChatMemory();
        this.chatClient = chatClientBuilder.
        defaultSystem("You are a ERP chatbot")
                .defaultAdvisors(
                        new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults())
                        //new PromptChatMemoryAdvisor(memory)
                ).build();
    }
    public String generate(String messageChat,String chatId)
    {
        System.out.println("Calling model");
        return chatClient.prompt()
                .user(messageChat)
                .advisors(advisorSpec -> advisorSpec.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,chatId)
                        .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY,100))
                .call().content();
    }
}
