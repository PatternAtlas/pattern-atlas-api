package io.github.patternatlas.api.rest.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.patternatlas.api.entities.DirectedEdge;
import io.github.patternatlas.api.entities.UndirectedEdge;
import io.github.patternatlas.api.rest.dto.AddDirectedEdgeToViewRequest;
import io.github.patternatlas.api.rest.dto.AddUndirectedEdgeToViewRequest;
import io.github.patternatlas.api.rest.dto.CreateDirectedEdgeRequest;
import io.github.patternatlas.api.rest.dto.CreateUndirectedEdgeRequest;
import io.github.patternatlas.api.rest.dto.DirectedEdgeModel;
import io.github.patternatlas.api.rest.dto.UndirectedEdgeModel;
import io.github.patternatlas.api.rest.dto.UpdateDirectedEdgeRequest;
import io.github.patternatlas.api.rest.dto.UpdateUndirectedEdgeRequest;
import io.github.patternatlas.api.service.PatternLanguageService;
import io.github.patternatlas.api.service.PatternViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@Tag(name = "Pattern Language Management")
@RequestMapping(value = "/", produces = "application/json")
public class PatternRelationDescriptorController {

    private final PatternLanguageService patternLanguageService;

    private final PatternViewService patternViewService;

    public PatternRelationDescriptorController(PatternLanguageService patternLanguageService,
                                               PatternViewService patternViewService) {
        this.patternLanguageService = patternLanguageService;
        this.patternViewService = patternViewService;
    }

    // Edges of Pattern Languages

    @Operation(operationId = "addDirectedEdgeToPatternLanguage", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Adds directed edge to pattern language")
    @PostMapping(value = "/pattern-languages/{patternLanguageId}/directed-edges")
    @CrossOrigin(exposedHeaders = "Location")
    @ResponseStatus(HttpStatus.OK)
    public DirectedEdgeModel addDirectedEdgeToPatternLanguage(@PathVariable UUID patternLanguageId,
                                                              @RequestBody CreateDirectedEdgeRequest createDirectedEdgeRequest) {
        DirectedEdge directedEdge =
            this.patternLanguageService.createDirectedEdgeAndAddToPatternLanguage(patternLanguageId, createDirectedEdgeRequest);
        return ResponseEntity.ok(DirectedEdgeModel.from(directedEdge)).getBody();
    }

    @Operation(operationId = "getDirectedEdgesOfPatternLanguage", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Get directed edges of pattern language")
    @GetMapping(value = "/pattern-languages/{patternLanguageId}/directed-edges")
    public List<DirectedEdgeModel> getDirectedEdgesOfPatternLanguage(@PathVariable UUID patternLanguageId) {
        return this.patternLanguageService.getDirectedEdgesOfPatternLanguage(patternLanguageId)
            .stream()
            .map(DirectedEdgeModel::from)
            .collect(Collectors.toList());
    }

    @Operation(operationId = "getDirectedEdgeOfPatternLanguageById", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Get directed edge of pattern language")
    @GetMapping(value = "/pattern-languages/{patternLanguageId}/directed-edges/{directedEdgeId}")
    public DirectedEdgeModel getDirectedEdgeOfPatternLanguageById(@PathVariable UUID patternLanguageId, @PathVariable UUID directedEdgeId) {
        DirectedEdge directedEdge = this.patternLanguageService.getDirectedEdgeOfPatternLanguageById(patternLanguageId, directedEdgeId);
        return DirectedEdgeModel.from(directedEdge);
    }

    @Operation(operationId = "updateDirectedEdgeOfPatternLanguage", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Update directed edge of pattern language")
    @PutMapping(value = "/pattern-languages/{patternLanguageId}/directed-edges/{directedEdgeId}")
    public DirectedEdgeModel putDirectedEdgeToPatternLanguage(@PathVariable UUID patternLanguageId, @PathVariable UUID directedEdgeId,
                                                              @RequestBody DirectedEdge directedEdge) {
        directedEdge = this.patternLanguageService.updateDirectedEdgeOfPatternLanguage(patternLanguageId, directedEdge);
        return DirectedEdgeModel.from(directedEdge);
    }

    @Operation(operationId = "removeDirectedEdgeFromPatternLanguage", responses = {@ApiResponse(responseCode = "204"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Remove directed edge of pattern language")
    @DeleteMapping(value = "/pattern-languages/{patternLanguageId}/directed-edges/{directedEdgeId}")
    public ResponseEntity<?> removeDirectedEdgeFromPatternLanguage(@PathVariable UUID patternLanguageId, @PathVariable UUID directedEdgeId) {
        this.patternLanguageService.removeDirectedEdgeFromPatternLanguage(patternLanguageId, directedEdgeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(operationId = "addUndirectedEdgeToPatternLanguage", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Add undirected edge to pattern language")
    @PostMapping(value = "/pattern-languages/{patternLanguageId}/undirected-edges")
    @CrossOrigin(exposedHeaders = "Location")
    @ResponseStatus(HttpStatus.OK)
    public UndirectedEdgeModel addUndirectedEdgeToPatternLanguage(@PathVariable UUID patternLanguageId,
                                                                  @RequestBody CreateUndirectedEdgeRequest undirectedEdgeRequest) {
        UndirectedEdge undirectedEdge =
            this.patternLanguageService.createUndirectedEdgeAndAddToPatternLanguage(patternLanguageId, undirectedEdgeRequest);
        return UndirectedEdgeModel.from(undirectedEdge);
    }

    @Operation(operationId = "getUndirectedEdgesOfPatternLanguage", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Get undirected edge of pattern language")
    @GetMapping(value = "/pattern-languages/{patternLanguageId}/undirected-edges")
    public List<UndirectedEdgeModel> getUndirectedEdgesOfPatternLanguage(@PathVariable UUID patternLanguageId) {
        return this.patternLanguageService.getUndirectedEdgesOfPatternLanguage(patternLanguageId)
            .stream()
            .map(UndirectedEdgeModel::from)
            .collect(Collectors.toList());
    }

    @Operation(operationId = "getUndirectedEdgeOfPatternLanguageById", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Get undirected edge of pattern language by id")
    @GetMapping(value = "/pattern-languages/{patternLanguageId}/undirected-edges/{undirectedEdgeId}")
    public UndirectedEdgeModel getUndirectedEdgeOfPatternLanguageById(@PathVariable UUID patternLanguageId, @PathVariable UUID undirectedEdgeId) {
        UndirectedEdge undirectedEdge = this.patternLanguageService.getUndirectedEdgeOfPatternLanguageById(patternLanguageId, undirectedEdgeId);
        return UndirectedEdgeModel.from(undirectedEdge);
    }

    @Operation(operationId = "updateUndirectedEdgeOfPatternLanguageById", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404")}, description = "Get undirected edge of pattern language by id")
    @PutMapping(value = "/pattern-languages/{patternLanguageId}/undirected-edges/{undirectedEdgeId}")
    public UndirectedEdgeModel putUndirectedEdgeToPatternLanguage(@PathVariable UUID patternLanguageId, @PathVariable UUID undirectedEdgeId,
                                                                  @RequestBody UndirectedEdge undirectedEdge) {
        undirectedEdge = this.patternLanguageService.updateUndirectedEdgeOfPatternLanguage(patternLanguageId, undirectedEdge);
        return UndirectedEdgeModel.from(undirectedEdge);
    }

    @Operation(operationId = "removeUndirectedEdgeFromPatternLanguage", responses = {@ApiResponse(responseCode = "204"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Remove undirected edge of pattern language by id")
    @DeleteMapping(value = "/pattern-languages/{patternLanguageId}/undirected-edges/{undirectedEdgeId}")
    public ResponseEntity<?> removeUndirectedEdgeFromPatternLanguage(@PathVariable UUID patternLanguageId, @PathVariable UUID undirectedEdgeId) {
        this.patternLanguageService.removeUndirectedEdgeFromPatternLanguage(patternLanguageId, undirectedEdgeId);
        return ResponseEntity.noContent().build();
    }

}
