package com.example.application;

import com.example.application.services.CustomerService;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.retriever.Retriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

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

    @Value("${openai.api.key}")
    private String OPENAI_API_KEY;

    @Bean
    StreamingChatLanguageModel streamingChatLanguageModel() {
        return OpenAiStreamingChatModel.builder()
                .apiKey(OPENAI_API_KEY)
                .modelName("gpt-4o-mini")
                .build();
    }

    @Bean
    Tokenizer tokenizer() {
        return new OpenAiTokenizer("gpt-4o-mini");
    }

    @Bean
    EmbeddingModel embeddingModel(){
        return OllamaEmbeddingModel.builder().baseUrl("http://localhost:11434/api/embeddings/").modelName("chatfire/bge-m3:q8_0").build();
    }

    @Bean
    EmbeddingStore<TextSegment> embeddingStore(){
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    Retriever<TextSegment> retriever(
            EmbeddingModel embeddingModel,
            EmbeddingStore<TextSegment> embeddingStore) {
        return EmbeddingStoreRetriever.from(
                embeddingStore,
                embeddingModel,
                1,
                0.6
        );
    }

    @Bean
    CommandLineRunner docsToEmbeddings(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore, Tokenizer tokenizer, ResourceLoader loader){
        return args -> {
            var resource = loader.getResource("classpath:documents/pdfPlumber_result.txt");
            var doc = FileSystemDocumentLoader.loadDocument(resource.getFile().toPath());

            var splitter = DocumentSplitters.recursive(100, 0, tokenizer);

            var ingestor = EmbeddingStoreIngestor.builder()
                    .documentSplitter(splitter)
                    .embeddingModel(embeddingModel)
                    .embeddingStore(embeddingStore)
                    .build();

            ingestor.ingest(doc);
        };
    }

    @Bean
    CustomerService customerService(StreamingChatLanguageModel streamingChatLanguageModel, Tokenizer tokenizer, Retriever<TextSegment> retriever) {
        return AiServices.builder(CustomerService.class)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .chatMemoryProvider(chatId -> TokenWindowChatMemory.builder()
                        .id(chatId)
                        .maxTokens(500, tokenizer)
                        .build())
                .build();
    }



    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
