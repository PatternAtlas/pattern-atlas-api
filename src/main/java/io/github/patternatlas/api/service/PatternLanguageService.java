package io.github.patternatlas.api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import io.github.patternatlas.api.entities.DirectedEdge;
import io.github.patternatlas.api.entities.Pattern;
import io.github.patternatlas.api.entities.PatternLanguage;
import io.github.patternatlas.api.entities.PatternSchema;
import io.github.patternatlas.api.exception.UndirectedEdgeNotFoundException;
import io.github.patternatlas.api.rest.dto.CreateDirectedEdgeRequest;
import io.github.patternatlas.api.rest.dto.CreateUndirectedEdgeRequest;
import io.github.patternatlas.api.entities.UndirectedEdge;

public interface PatternLanguageService {

    PatternLanguage createPatternLanguage(PatternLanguage patternLanguage);

    List<PatternLanguage> getPatternLanguages();

    PatternLanguage getPatternLanguageById(UUID patternLanguageId);

    PatternLanguage getPatternLanguageByUri(String uri);

    PatternLanguage updatePatternLanguage(PatternLanguage patternLanguage);

    void deletePatternLanguage(UUID patternLanguageId);

    Pattern createPatternAndAddToPatternLanguage(UUID patternLanguageId, Pattern pattern);

    List<Pattern> getPatternsOfPatternLanguage(UUID patternLanguageId);

    Pattern getPatternOfPatternLanguageById(UUID patternLanguageId, UUID patternId);

    void deletePatternOfPatternLanguage(UUID patternLanguageId, UUID patternId);

    PatternSchema createPatternSchemaAndAddToPatternLanguage(UUID patternLanguageId, PatternSchema patternSchema);

    PatternSchema getPatternSchemaByPatternLanguageId(UUID patternLanguageId);

    PatternSchema updatePatternSchemaOfPatternLanguage(UUID patternLanguageId, PatternSchema patternSchema);

    Object getGraphOfPatternLanguage(UUID patternLanguageId);

    Object createGraphOfPatternLanguage(UUID patternLanguageId, Object graph);

    Object updateGraphOfPatternLanguage(UUID patternLanguageId, Object graph);

    void deleteGraphOfPatternLanguage(UUID patternLanguageId);

    DirectedEdge createDirectedEdgeAndAddToPatternLanguage(UUID patternLanguageId, CreateDirectedEdgeRequest createDirectedEdgeRequest);

    List<DirectedEdge> getDirectedEdgesOfPatternLanguage(UUID patternLanguageId);

    DirectedEdge getDirectedEdgeOfPatternLanguageById(UUID patternLanguageId, UUID directedEdgeId);

    DirectedEdge updateDirectedEdgeOfPatternLanguage(UUID patternLanguageId, DirectedEdge directedEdge);

    void removeDirectedEdgeFromPatternLanguage(UUID patternLanguageId, UUID directedEdgeId);

    UndirectedEdge createUndirectedEdgeAndAddToPatternLanguage(UUID patternLanguageId, CreateUndirectedEdgeRequest createUndirectedEdgeRequest);

    List<UndirectedEdge> getUndirectedEdgesOfPatternLanguage(UUID patternLanguageId);

    UndirectedEdge getUndirectedEdgeOfPatternLanguageById(UUID patternLanguageId, UUID undirectedEdgeId) throws UndirectedEdgeNotFoundException;

    UndirectedEdge updateUndirectedEdgeOfPatternLanguage(UUID patternLanguageId, UndirectedEdge undirectedEdge);

    void removeUndirectedEdgeFromPatternLanguage(UUID patternLanguageId, UUID undirectedEdgeId);
}
