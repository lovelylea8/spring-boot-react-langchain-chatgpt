package com.example.application;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "spring-boot-react-langchain-chatgpt")
public class Application implements AppShellConfigurator {

    @Bean
    Tokenizer tokenizer() {
        return new OpenAiTokenizer("gpt-4o-mini");
    }

    @Bean
    OllamaEmbeddingModel embeddingModel(){
        return OllamaEmbeddingModel.builder().baseUrl("http://localhost:11434/api/embeddings/").modelName("chatfire/bge-m3:q8_0").build();
    }

    @Bean
    EmbeddingStore<TextSegment> embeddingStore(){
        return new InMemoryEmbeddingStore<>();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
