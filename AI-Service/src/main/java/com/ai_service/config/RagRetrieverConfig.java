package com.ai_service.config;

import com.ai_service.agent.UserContext;
import com.ai_service.client.ProfileClient;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RagRetrieverConfig {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final ProfileClient profileClient;

    /// Retrieves Contents from an underlying data source using a given Query.
    @Bean
    public ContentRetriever contentRetriever() {
        return query -> {

            log.info("..RAG SEARCH..");

            if (query == null || query.text() == null || query.text().isBlank()) {
                log.warn("Search query is empty! Skipping vector retrieval.");
                return List.of();
            }

            log.info("Question: {}", query.text());

            Long userId = UserContext.getUserId();

            log.info("RAG userId = {}", userId);

            Long patientId = profileClient.getPatientIdByUserId(userId);

            log.info("RAG patientId = {}", patientId);

            Embedding queryEmbedding = embeddingModel.embed(query.text()).content();

            log.info("QUERY EMBEDDING LENGTH = {}", queryEmbedding.vector().length);

            if (queryEmbedding.vector().length == 0) {
                log.error("Query embedding came back EMPTY for question: {}", query.text());
                return List.of();
            }

            EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                    .queryEmbedding(queryEmbedding)
                    .maxResults(5)
                    .minScore(0.3)
                    .filter(metadataKey("patientId").isEqualTo(String.valueOf(patientId)))
                    .build();

            EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);

            List<Content> results = searchResult.matches().stream()
                    .map(match -> Content.from(match.embedded()))
                    .collect(Collectors.toList());

            log.info("Retrieved documents = {}", results.size());
            for (Content content : results) {
                log.info("Retrieved text = {}", content.textSegment().text());
                log.info("Retrieved metadata = {}", content.textSegment().metadata());
            }
            return results;
        };
    }
}