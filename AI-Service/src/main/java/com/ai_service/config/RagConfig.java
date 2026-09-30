package com.ai_service.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagConfig {

    @Bean
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore(
            @Value("${rag.qdrant.host}") String host,
            @Value("${rag.qdrant.port:6334}") Integer port,
            @Value("${rag.qdrant.api-key}") String apiKey,
            @Value("${rag.qdrant.collection}") String collection
    ) {
        return QdrantEmbeddingStore.builder()
                .host(host)
                .port(port)
                .useTls(true)
                .apiKey(apiKey)
                .collectionName(collection)
                .payloadTextKey("text")
                .build();
    }
}

/// tem memory

//
//    @Bean
//    public EmbeddingModel embeddingModel() {
//        ///this is local model (eg. AllMiniLmL6V2EmbeddingModel, BgeSmallEnV15, E5SmallV2)
//        return new AllMiniLmL6V2EmbeddingModel();
//    }
//
//    @Bean
//    public EmbeddingStore<TextSegment> embeddingStore() {
//        return new InMemoryEmbeddingStore<>();
//    }

//}