package io.github.patternatlas.api.rest.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.text.CaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.patternatlas.api.entities.PatternLanguage;
import io.github.patternatlas.api.entities.PatternSchema;
import io.github.patternatlas.api.rest.dto.GraphModel;
import io.github.patternatlas.api.rest.dto.PatternLanguageModel;
import io.github.patternatlas.api.rest.dto.shared.PatternLanguageSchemaModel;
import io.github.patternatlas.api.service.PatternLanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping(value = "/pattern-languages", produces = "application/json")
public class PatternLanguageController {

    private final PatternLanguageService patternLanguageService;

    private final ObjectMapper objectMapper;

    @Autowired
    public PatternLanguageController(PatternLanguageService patternLanguageService,
                                     ObjectMapper objectMapper) {
        this.patternLanguageService = patternLanguageService;
        this.objectMapper = objectMapper;
    }

    @Operation(operationId = "getAllPatternLanguageModels", responses = {
        @ApiResponse(responseCode = "200")}, description = "Retrieve all pattern languages")
    @GetMapping(value = "")
    List<PatternLanguageModel> getAllPatternLanguages() {
        return this.patternLanguageService.getPatternLanguages()
            .stream()
            .map(PatternLanguageModel::toModel).collect(Collectors.toList());
    }

    @Operation(operationId = "getPatternLanguageByURI", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Retrieve pattern language by URI")
    @GetMapping(value = "/findByUri")
    PatternLanguageModel findPatternLanguageByUri(@RequestParam String encodedUri) throws UnsupportedEncodingException {
        String uri = URLDecoder.decode(encodedUri, StandardCharsets.UTF_8.toString());
        PatternLanguage patternLanguage = this.patternLanguageService.getPatternLanguageByUri(uri);

        return PatternLanguageModel.toModel(patternLanguage);
    }

    @Operation(operationId = "getPatternLanguageByID", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Retrieve pattern language by ID")
    @GetMapping(value = "/{patternLanguageId}")
    PatternLanguageModel getPatternLanguageById(@PathVariable UUID patternLanguageId) {
        PatternLanguage patternLanguage = this.patternLanguageService.getPatternLanguageById(patternLanguageId);
        return PatternLanguageModel.toModel(patternLanguage);
    }

    @Operation(operationId = "createPatternLanguage", responses = {@ApiResponse(responseCode = "200")}, description = "Create pattern language")
    @PostMapping
    @CrossOrigin(exposedHeaders = "Location")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<?> createPatternLanguage(@RequestBody PatternLanguage patternLanguage) {
        String patternLanguageNameAsCamelCase = CaseUtils.toCamelCase(patternLanguage.getName(), false);
        String uri = String.format("https://patternpedia.org/patternLanguages/%s", patternLanguageNameAsCamelCase);
        patternLanguage.setUri(uri);

        PatternLanguage createdPatternLanguage = this.patternLanguageService.createPatternLanguage(patternLanguage);
        return ResponseEntity.ok(PatternLanguageModel.toModel(createdPatternLanguage));

    }

    @Operation(operationId = "updatePatternLanguage", responses = {@ApiResponse(responseCode = "200")}, description = "Update pattern language")
    @PutMapping(value = "/{patternLanguageId}")
    ResponseEntity<Void> putPatternLanguage(@PathVariable UUID patternLanguageId, @RequestBody PatternLanguage patternLanguage) {
        this.patternLanguageService.updatePatternLanguage(patternLanguage);
        return ResponseEntity.noContent().build();
    }

    @Operation(operationId = "deletePatternLanguage", responses = {@ApiResponse(responseCode = "204")}, description = "Delete pattern language")
    @DeleteMapping(value = "/{patternLanguageId}")
    ResponseEntity<Void> deletePatternLanguage(@PathVariable UUID patternLanguageId) {
        this.patternLanguageService.deletePatternLanguage(patternLanguageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/pattern-schemas")
    List<PatternLanguageSchemaModel> getAllPatternLanguagesWithSchema() {
        return this.patternLanguageService.getPatternLanguages()
            .stream()
            .map(PatternLanguageSchemaModel::from)
            .collect(Collectors.toList());
    }

    @Operation(operationId = "getPatternSchema", responses = {
        @ApiResponse(responseCode = "200")}, description = "Get pattern schema by pattern language id")
    @GetMapping(value = "/{patternLanguageId}/pattern-schema")
    ResponseEntity<?> getPatternSchema(@PathVariable UUID patternLanguageId) {
        PatternSchema schema = this.patternLanguageService.getPatternSchemaByPatternLanguageId(patternLanguageId);
        return ResponseEntity.ok(schema);
    }

    @Operation(operationId = "updatePatternSchema", responses = {
        @ApiResponse(responseCode = "204")}, description = "Update pattern schema by pattern language id")
    @PutMapping(value = "/{patternLanguageId}/pattern-schema")
    ResponseEntity<Void> updatePatternSchema(@PathVariable UUID patternLanguageId, @RequestBody PatternSchema patternSchema) {
        this.patternLanguageService.updatePatternSchemaOfPatternLanguage(patternLanguageId, patternSchema);
        return ResponseEntity.noContent().build();
    }

    @Operation(operationId = "getPatternLanguageGraph", responses = {
        @ApiResponse(responseCode = "200")}, description = "Retrieve pattern language graph")
    @GetMapping(value = "/{patternLanguageId}/graph")
    ResponseEntity<?> getPatternLanguageGraph(@PathVariable UUID patternLanguageId) {
        Object graph = this.patternLanguageService.getGraphOfPatternLanguage(patternLanguageId);

        GraphModel model = new GraphModel();
        if (null == graph) {
            model.setGraph(this.objectMapper.createArrayNode());
        } else {
            model.setGraph(graph);
        }
        return ResponseEntity.ok(model);
    }

    @Operation(operationId = "postPatternLanguageGraph", responses = {
        @ApiResponse(responseCode = "200")}, description = "Update pattern language graph")
    @PostMapping(value = "/{patternLanguageId}/graph")
    ResponseEntity<?> postPatternLanguageGraph(@PathVariable UUID patternLanguageId, @RequestBody Object graph) {
        this.patternLanguageService.updateGraphOfPatternLanguage(patternLanguageId, graph);
        return ResponseEntity.ok().build();
    }

    @Operation(operationId = "putPatternLanguageGraph", responses = {
        @ApiResponse(responseCode = "204")}, description = "Update pattern language graph")
    @PutMapping(value = "/{patternLanguageId}/graph")
    ResponseEntity<Void> putPatternLanguageGraph(@PathVariable UUID patternLanguageId, @RequestBody Object graph) {
        this.patternLanguageService.updateGraphOfPatternLanguage(patternLanguageId, graph);
        return ResponseEntity.noContent().build();
    }

    @Operation(operationId = "deletePatternLanguageGraph", responses = {
        @ApiResponse(responseCode = "204")}, description = "Delete pattern language graph")
    @DeleteMapping(value = "/{patternLanguageId}/graph")
    ResponseEntity<Void> deletePatternLanguageGraph(@PathVariable UUID patternLanguageId) {
        this.patternLanguageService.deleteGraphOfPatternLanguage(patternLanguageId);
        return ResponseEntity.noContent().build();
    }
}
