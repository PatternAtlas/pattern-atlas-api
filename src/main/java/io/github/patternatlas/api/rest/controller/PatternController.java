package io.github.patternatlas.api.rest.controller;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.patternatlas.api.entities.Pattern;
import io.github.patternatlas.api.entities.PatternLanguage;
import io.github.patternatlas.api.entities.solutionmodel.ConcreteSolutionDescriptor;
import io.github.patternatlas.api.rest.model.PatternContentModel;
import io.github.patternatlas.api.rest.model.PatternModel;
import io.github.patternatlas.api.rest.model.PatternRenderedContentModel;
import io.github.patternatlas.api.service.PatternLanguageService;
import io.github.patternatlas.api.service.PatternRenderService;
import io.github.patternatlas.api.service.PatternService;
import io.github.patternatlas.api.service.PatternViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping(value = "/", produces = "application/json")
public class PatternController {

    private final PatternService patternService;

    private final PatternLanguageService patternLanguageService;

    private final PatternViewService patternViewService;


    private final ObjectMapper objectMapper;

    private final PatternRenderService patternRenderService;

    public PatternController(PatternService patternService,
                             PatternLanguageService patternLanguageService,
                             PatternViewService patternViewService,
                             PatternRenderService patternRenderService,
                             ObjectMapper objectMapper) {
        this.patternService = patternService;
        this.patternLanguageService = patternLanguageService;
        this.patternViewService = patternViewService;
        this.objectMapper = objectMapper;
        this.patternRenderService = patternRenderService;
    }

    @GetMapping(value = "/patterns/{patternId}/concrete-solution-descriptors")
    List<ConcreteSolutionDescriptor> getConcreteSolutionsOfPattern(@PathVariable java.util.UUID patternId) {
        Pattern pattern = this.patternService.getPatternById(patternId);
        return pattern.getConcreteSolutionDescriptors();
    }

    @Operation(operationId = "getPatternByUri", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404")}, description = "Retrieve pattern by pattern uri")
    @GetMapping(value = "/patterns/search/findByUri")
    PatternModel getPatternByUri(@RequestParam String encodedUri) throws UnsupportedEncodingException {
        String uri = URLDecoder.decode(encodedUri, StandardCharsets.UTF_8.toString());
        Pattern pattern = this.patternService.getPatternByUri(uri);
        return PatternModel.from(pattern);
    }

    @Operation(operationId = "getPatternById", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404")}, description = "Retrieve pattern by pattern UUID")
    @GetMapping(value = "/patterns/{id}")
    PatternModel getPatternById(@PathVariable java.util.UUID id) {
        Pattern pattern = this.patternService.getPatternById(id);
        return PatternModel.from(pattern);
    }

    @Operation(operationId = "getPatternsOfPatternLanguage", responses = {
        @ApiResponse(responseCode = "200")}, description = "Retrieve patterns by pattern language id")
    @GetMapping(value = "/pattern-languages/{patternLanguageId}/patterns")
    List<PatternModel> getPatternsOfPatternLanguage(@PathVariable java.util.UUID patternLanguageId) {
        return this.patternLanguageService.getPatternsOfPatternLanguage(patternLanguageId).stream()
            .map(PatternModel::from).toList();
    }

    @Operation(operationId = "getPatternsOfPatternView", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404")}, description = "Retrieve patterns by pattern view id")
    @GetMapping(value = "/pattern-views/{patternViewId}/patterns")
    List<PatternModel> getPatternsOfPatternView(@PathVariable java.util.UUID patternViewId) {
        return this.patternViewService.getPatternsOfPatternView(patternViewId).stream()
            .map(PatternModel::from).toList();
    }

    @Operation(operationId = "addPatternToPatternView", responses = {@ApiResponse(responseCode = "200")}, description = "add pattern to pattern view")
    @PostMapping(value = "/pattern-views/{patternViewId}/patterns")
    @CrossOrigin(exposedHeaders = "Location")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> addPatternToPatternView(@PathVariable java.util.UUID patternViewId, @RequestBody Pattern pattern) {
        Object renderedContent = patternRenderService.renderContent(pattern, null);
        if (renderedContent != null) {
            pattern.setRenderedContent(renderedContent);
        } else {
            pattern.setRenderedContent(pattern.getContent());
        }
        this.patternViewService.addPatternToPatternView(patternViewId, pattern.getId());
        return ResponseEntity.ok().build();
    }

    @Operation(operationId = "getPatternOfPatternViewById", responses = {
        @ApiResponse(responseCode = "200")}, description = "Retrieve single pattern by pattern view id")
    @CrossOrigin(exposedHeaders = "Location")
    @GetMapping(value = "/pattern-views/{patternViewId}/patterns/{patternId}")
    PatternModel getPatternOfPatternViewById(@PathVariable java.util.UUID patternViewId, @PathVariable java.util.UUID patternId) {
        Pattern pattern = this.patternViewService.getPatternOfPatternViewById(patternViewId, patternId);
        return PatternModel.from(pattern);
    }

    @Operation(operationId = "removePatternFromView", responses = {
        @ApiResponse(responseCode = "204")}, description = "Delete pattern from pattern view")
    @DeleteMapping(value = "/pattern-views/{patternViewId}/patterns/{patternId}")
    ResponseEntity<?> removePatternFromView(@PathVariable java.util.UUID patternViewId, @PathVariable java.util.UUID patternId) {
        this.patternViewService.removePatternFromPatternView(patternViewId, patternId);
        return ResponseEntity.noContent().build();
    }

    @Operation(operationId = "addPatternToPatternLanguage", responses = {
        @ApiResponse(responseCode = "200")}, description = "Add pattern to pattern language")
    @PostMapping(value = "/pattern-languages/{patternLanguageId}/patterns")
    @CrossOrigin(exposedHeaders = "Location")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<?> addPatternToPatternLanguage(@PathVariable java.util.UUID patternLanguageId, @Valid @RequestBody Pattern pattern) {
        PatternLanguage patternLanguage = this.patternLanguageService.getPatternLanguageById(patternLanguageId);
        if (null == pattern.getUri()) {
            pattern.setUri(patternLanguage.getUri() + '/' + CaseUtils.toCamelCase(pattern.getName(), false));
        }
        Object renderedContent = patternRenderService.renderContent(pattern, null);
        if (renderedContent != null) {
            pattern.setRenderedContent(renderedContent);
        } else {
            pattern.setRenderedContent(pattern.getContent());
        }
        pattern = this.patternLanguageService.createPatternAndAddToPatternLanguage(patternLanguageId, pattern);

        return ResponseEntity.ok(PatternModel.from(pattern));
    }

    @Operation(operationId = "getPatternOfPatternLanguageById", responses = {
        @ApiResponse(responseCode = "200")}, description = "Retrieve single pattern by pattern language id")
    @GetMapping(value = "/pattern-languages/{patternLanguageId}/patterns/{patternId}")
    PatternModel getPatternOfPatternLanguageById(@PathVariable java.util.UUID patternLanguageId, @PathVariable java.util.UUID patternId) {
        Pattern pattern = this.patternLanguageService.getPatternOfPatternLanguageById(patternLanguageId, patternId);
        return PatternModel.from(pattern);
    }

    @Operation(operationId = "updatePatternByPatternLanguageId", responses = {
        @ApiResponse(responseCode = "200")}, description = "Update pattern by pattern language id")
    @PutMapping(value = "/pattern-languages/{patternLanguageId}/patterns/{patternId}")
    PatternModel updatePatternViaPut(@PathVariable java.util.UUID patternLanguageId, @PathVariable java.util.UUID patternId, @Valid @RequestBody Pattern pattern) {
        this.patternLanguageService.getPatternLanguageById(patternLanguageId);
        Pattern persistedVersion = this.patternService.getPatternById(patternId);
        // Remark: At the moment we do not support changing name, uri of a pattern

        Object renderedContent = patternRenderService.renderContent(pattern, persistedVersion);
        if (renderedContent != null) {
            persistedVersion.setRenderedContent(renderedContent);
        } else {
            persistedVersion.setRenderedContent(pattern.getContent());
        }
        persistedVersion.setIconUrl(pattern.getIconUrl());
        persistedVersion.setPaperRef(pattern.getPaperRef());
        persistedVersion.setContent(pattern.getContent());
        persistedVersion.setName(pattern.getName());

        pattern = this.patternService.updatePattern(persistedVersion);
        return PatternModel.from(pattern);
    }

    @Operation(operationId = "deletePatternOfPatternLanguage", responses = {
        @ApiResponse(responseCode = "204")}, description = "Delete pattern of pattern language")
    @DeleteMapping(value = "/pattern-languages/{patternLanguageId}/patterns/{patternId}")
    ResponseEntity<?> deletePatternOfPatternLanguage(@PathVariable java.util.UUID patternLanguageId, @PathVariable java.util.UUID patternId) {
        this.patternLanguageService.deletePatternOfPatternLanguage(patternLanguageId, patternId);
        return ResponseEntity.noContent().build();
    }

    @Operation(operationId = "getPatternContent", responses = {@ApiResponse(responseCode = "200")}, description = "Get content of pattern")
    @GetMapping(value = "/pattern-languages/{patternLanguageId}/patterns/{patternId}/content")
    ResponseEntity<?> getPatternContentOfPattern(@PathVariable java.util.UUID patternLanguageId, @PathVariable java.util.UUID patternId) {
        Pattern pattern = this.patternLanguageService.getPatternOfPatternLanguageById(patternLanguageId, patternId);
        PatternContentModel model = new PatternContentModel();

        if (null == pattern.getContent()) {
            model.setContent(this.objectMapper.createObjectNode());
        } else {
            model.setContent(pattern.getContent());
        }

        return ResponseEntity.ok(model);
    }

    @GetMapping(value = "/pattern-languages/{patternLanguageId}/patterns/{patternId}/rendered-content")
    ResponseEntity<?> getPatternRenderedContentOfPattern(@PathVariable java.util.UUID patternLanguageId, @PathVariable java.util.UUID patternId) {

        Pattern pattern = this.patternLanguageService.getPatternOfPatternLanguageById(patternLanguageId, patternId);
        PatternRenderedContentModel model = new PatternRenderedContentModel();

        if (null == pattern.getRenderedContent()) {
            model.setRenderedContent(this.objectMapper.createObjectNode());
        } else {
            model.setRenderedContent(pattern.getRenderedContent());
        }

        return ResponseEntity.ok(model);
    }

    @GetMapping(value = "/pattern-languages/{patternLanguageId}/patterns/{patternId}/concrete-solution-descriptors")
    List<ConcreteSolutionDescriptor> getConcreteSolutionsOfPatternOfPatternLanguage(@PathVariable java.util.UUID patternLanguageId,
                                                                                    @PathVariable java.util.UUID patternId) {
        Pattern pattern = this.patternService.getPatternById(patternId);
        if (!pattern.getPatternLanguage().getId().equals(patternLanguageId)) {
            throw new IllegalArgumentException("Pattern does not belong to pattern language");
        }
        return pattern.getConcreteSolutionDescriptors();
    }

}
