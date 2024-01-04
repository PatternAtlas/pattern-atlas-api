package io.github.patternatlas.api.rest.controller;

import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.text.CaseUtils;
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

import io.github.patternatlas.api.entities.Pattern;
import io.github.patternatlas.api.entities.designmodel.DesignModel;
import io.github.patternatlas.api.entities.designmodel.DesignModelEdgeType;
import io.github.patternatlas.api.entities.designmodel.DesignModelPatternEdge;
import io.github.patternatlas.api.entities.designmodel.DesignModelPatternInstance;
import io.github.patternatlas.api.rest.dto.ConcreteSolutionModel;
import io.github.patternatlas.api.rest.dto.DesignModelModel;
import io.github.patternatlas.api.rest.dto.EdgeDTO;
import io.github.patternatlas.api.rest.dto.FileDTO;
import io.github.patternatlas.api.rest.dto.PatternInstanceDTO;
import io.github.patternatlas.api.rest.dto.PositionDTO;
import io.github.patternatlas.api.service.ConcreteSolutionService;
import io.github.patternatlas.api.service.DesignModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.apachecommons.CommonsLog;

@RestController
@CommonsLog
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping(value = "/design-models", produces = "application/json")
public class DesignModelController {

    private final DesignModelService designModelService;

    private final ConcreteSolutionService concreteSolutionService;

    public DesignModelController(DesignModelService designModelService, ConcreteSolutionService concreteSolutionService) {
        this.designModelService = designModelService;
        this.concreteSolutionService = concreteSolutionService;
    }

    @GetMapping("")
    public List<DesignModelModel> getDesignModels() {
        return this.designModelService.getAllDesignModels()
            .stream()
            .map(DesignModelModel::toModel)
            .collect(toList());
    }

    @PostMapping("")
    @CrossOrigin(exposedHeaders = "Location")
    @ResponseStatus(HttpStatus.OK)
    public DesignModel createDesignModel(@RequestBody DesignModel designModel) {
        String nameAsCamelCase = CaseUtils.toCamelCase(designModel.getName(), false);
        String uri = String.format("https://patternpedia.org/designModels/%s", nameAsCamelCase);
        designModel.setUri(uri);
        return this.designModelService.createDesignModel(designModel);
    }

    @GetMapping("/edge-types")
    public Map<String, List<String>> getDesignModelPatternEdgeTypes() {

        List<String> edgeTypes = this.designModelService.getDesignModelEdgeTypes().stream()
            .map(DesignModelEdgeType::getName)
            .collect(toList());

        return Collections.singletonMap("edgeTypes", edgeTypes);
    }

    @GetMapping("/{designModelId}")
    public DesignModel getDesignModel(@PathVariable UUID designModelId) {
        return this.designModelService.getDesignModel(designModelId);
    }

    @GetMapping("/{designModelId}/patterns")
    public List<PatternInstanceDTO> getDesignModelPatternInstances(@PathVariable UUID designModelId) {
        List<DesignModelPatternInstance> patternInstances = this.designModelService.getDesignModel(designModelId).getPatterns();
        return patternInstances.stream()
            .map(PatternInstanceDTO::from)
            .collect(toList());
    }

    @PostMapping("/{designModelId}/patterns")
    @CrossOrigin(exposedHeaders = "Location")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> addDesignModelPatternInstance(@PathVariable UUID designModelId, @RequestBody Pattern pattern) {
        this.designModelService.addPatternInstance(designModelId, pattern.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{designModelId}/patterns/{patternInstanceId}/position")
    public ResponseEntity<?> putDesignModelPatternInstancePosition(@PathVariable UUID designModelId, @PathVariable UUID patternInstanceId,
                                                                   @RequestBody PositionDTO position) {
        this.designModelService.updatePatternInstancePosition(designModelId, patternInstanceId, position.getX(), position.getY());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{designModelId}/patterns/{patternInstanceId}")
    public ResponseEntity<?> deleteDesignModelPatternInstance(@PathVariable UUID designModelId, @PathVariable UUID patternInstanceId) {
        this.designModelService.deletePatternInstance(designModelId, patternInstanceId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{designModelId}/edges")
    @CrossOrigin(exposedHeaders = "Location")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> addDesignModelEdge(@PathVariable UUID designModelId, @RequestBody EdgeDTO edgeDTO) {
        this.designModelService.addEdge(designModelId, edgeDTO.getFirstPatternId(), edgeDTO.getSecondPatternId(),
            edgeDTO.isDirectedEdge(), edgeDTO.getType(), edgeDTO.getDescription());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{designModelId}/edges")
    public List<EdgeDTO> getDesignModelPatternEdges(@PathVariable UUID designModelId) {
        List<DesignModelPatternEdge> designModelPatternEdges = this.designModelService.getEdges(designModelId);
        return designModelPatternEdges.parallelStream()
            .map(EdgeDTO::from)
            .collect(toList());
    }

    @DeleteMapping("/{designModelId}/edges/{sourceId}/{targetId}")
    public ResponseEntity<?> getDesignModelPatternEdges(@PathVariable UUID designModelId, @PathVariable UUID sourceId, @PathVariable UUID targetId) {
        this.designModelService.deleteEdge(designModelId, sourceId, targetId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{designModelId}/concrete-solutions")
    public List<ConcreteSolutionModel> checkConcreteSolutions(@PathVariable UUID designModelId) {
        List<DesignModelPatternInstance> patternInstanceList = this.designModelService.getDesignModel(designModelId).getPatterns();
        Set<String> patternUris =
            patternInstanceList.stream().map(patternInstance -> patternInstance.getPattern().getUri()).collect(Collectors.toSet());
        Set<ConcreteSolutionModel> concreteSolutionSet = new HashSet<>();

        for (String uri : patternUris) {
            this.concreteSolutionService.getConcreteSolutions(URI.create(uri))
                .forEach(concreteSolution -> concreteSolutionSet.add(ConcreteSolutionModel.from(concreteSolution)));
        }

        return new ArrayList<>(concreteSolutionSet);
    }

    @PostMapping("/{designModelId}/aggregate")
    public List<FileDTO> aggregateConcreteSolutions(@PathVariable UUID designModelId, @RequestBody Map<UUID, UUID> patternConcreteSolutionMap) {

        DesignModel designModel = this.designModelService.getDesignModel(designModelId);
        List<DesignModelPatternInstance> patternInstanceList = designModel.getPatterns();
        List<DesignModelPatternEdge> directedEdgeList = designModel.getDirectedEdges();

        return this.concreteSolutionService.aggregate(patternInstanceList, directedEdgeList, patternConcreteSolutionMap);
    }

    @Operation(operationId = "deleteDesignModelById", responses = {@ApiResponse(responseCode = "204"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Delete DesignModel by id")
    @DeleteMapping(value = "/{designModelId}")
    public ResponseEntity<?> deleteDesignModel(@PathVariable UUID designModelId) {
        this.designModelService.deleteDesignModel(designModelId);
        return ResponseEntity.noContent().build();
    }
}
