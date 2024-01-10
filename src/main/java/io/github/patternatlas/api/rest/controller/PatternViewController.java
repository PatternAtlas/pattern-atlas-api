package io.github.patternatlas.api.rest.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.text.CaseUtils;
import org.springframework.hateoas.Link;
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

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.patternatlas.api.entities.DirectedEdge;
import io.github.patternatlas.api.entities.PatternView;
import io.github.patternatlas.api.entities.UndirectedEdge;
import io.github.patternatlas.api.rest.dto.AddDirectedEdgeToViewRequest;
import io.github.patternatlas.api.rest.dto.AddUndirectedEdgeToViewRequest;
import io.github.patternatlas.api.rest.dto.DirectedEdgeModel;
import io.github.patternatlas.api.rest.dto.GraphModel;
import io.github.patternatlas.api.rest.dto.UndirectedEdgeModel;
import io.github.patternatlas.api.rest.dto.UpdateDirectedEdgeRequest;
import io.github.patternatlas.api.rest.dto.UpdateUndirectedEdgeRequest;
import io.github.patternatlas.api.service.PatternViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@Tag(name = "Pattern Views Management")
@RequestMapping(value = "/pattern-views", produces = "application/json")
public class PatternViewController {

    private final PatternViewService patternViewService;

    private final ObjectCodec objectMapper;

    public PatternViewController(PatternViewService patternViewService, ObjectMapper objectMapper) {
        this.patternViewService = patternViewService;
        this.objectMapper = objectMapper;
    }

    @Operation(operationId = "getPatterViewGraph", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Retrieve pattern view graph")
    @GetMapping(value = "/{patternViewId}/graph")
    GraphModel getPatterViewGraph(@PathVariable UUID patternViewId) {
        Object graph = this.patternViewService.getGraphOfPatternView(patternViewId);

        GraphModel model = new GraphModel();
        if (null == graph) {
            model.setGraph(this.objectMapper.createArrayNode());
        } else {
            model.setGraph(graph);
        }
        return model;
    }

    @Operation(operationId = "getAllPatternViews", responses = {@ApiResponse(responseCode = "200")}, description = "Retrieve all pattern views")
    @GetMapping
    public List<PatternView> getAllPatternViews() {
        return this.patternViewService.getAllPatternViews();
    }

    @Operation(operationId = "createPatternView", responses = {@ApiResponse(responseCode = "200")}, description = "Create a pattern view")
    @PostMapping
    @CrossOrigin(exposedHeaders = "Location")
    @ResponseStatus(HttpStatus.OK)
    public PatternView createPatternView(@RequestBody PatternView patternView) {
        String patternViewNameAsCamelCase = CaseUtils.toCamelCase(patternView.getName(), false);
        String uri = String.format("https://patternpedia.org/pattern-views/%s", patternViewNameAsCamelCase);
        patternView.setUri(uri);
        return this.patternViewService.createPatternView(patternView);

    }

    @Operation(operationId = "getPatternViewById", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Retrieve pattern view by id")
    @GetMapping(value = "/{patternViewId}")
    public PatternView getPatternViewById(@PathVariable UUID patternViewId) {
        return this.patternViewService.getPatternViewById(patternViewId);
    }

    @Operation(operationId = "getPatternViewByURI", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Retrieve pattern view by URI")
    @GetMapping(value = "/findByUri")
    public PatternView findPatternViewByUri(@RequestParam String encodedUri) throws UnsupportedEncodingException {
        String uri = URLDecoder.decode(encodedUri, StandardCharsets.UTF_8.toString());
        return this.patternViewService.getPatternViewByUri(uri);
    }

    @Operation(operationId = "updatePatternView", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Update pattern view by id")
    @PutMapping(value = "/{patternViewId}")
    public ResponseEntity<?> putPatternView(@PathVariable UUID patternViewId, @RequestBody PatternView patternView) {
        patternView = this.patternViewService.updatePatternView(patternView);
        return ResponseEntity.ok(patternView);
    }

    @Operation(operationId = "deletePatternViewById", responses = {@ApiResponse(responseCode = "204"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Delete pattern view by id")
    @DeleteMapping(value = "/{patternViewId}")
    public ResponseEntity<?> deletePatternView(@PathVariable UUID patternViewId) {
        this.patternViewService.deletePatternView(patternViewId);
        return ResponseEntity.noContent().build();
    }

    @Operation(operationId = "createPatternViewGraph", responses = {@ApiResponse(responseCode = "201"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Create pattern view graph")
    @PostMapping(value = "/{patternViewId}/graph")
    ResponseEntity<?> postPatternViewGraph(@PathVariable UUID patternViewId, @RequestBody Object graph) {
        this.patternViewService.updateGraphOfPatternView(patternViewId, graph);
        return ResponseEntity.ok().build();
    }

    @Operation(operationId = "updatePatternViewGraph", responses = {@ApiResponse(responseCode = "204"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Update pattern view graph")
    @PutMapping(value = "/{patternViewId}/graph")
    ResponseEntity<?> putPatternViewGraph(@PathVariable UUID patternViewId, @RequestBody Object graph) {
        this.patternViewService.updateGraphOfPatternView(patternViewId, graph);
        return ResponseEntity.noContent().build();
    }

    @Operation(operationId = "deletePatternViewGraph", responses = {@ApiResponse(responseCode = "204"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Delete pattern view graph")
    @DeleteMapping(value = "/{patternViewId}/graph")
    ResponseEntity<?> deletePatternViewGraph(@PathVariable UUID patternViewId) {
        this.patternViewService.deleteGraphOfPatternView(patternViewId);
        return ResponseEntity.noContent().build();
    }

    // Edges of Views

    @Operation(operationId = "addDirectedEdgeToView", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Adds directed edge to view")
    @PostMapping(value = "/{patternViewId}/directed-edges")
    @CrossOrigin(exposedHeaders = "Location")
    @ResponseStatus(HttpStatus.OK)
    public DirectedEdgeModel addDirectedEdgeToView(@PathVariable UUID patternViewId, @RequestBody AddDirectedEdgeToViewRequest request) {
        if (request.isNewEdge()) {
            DirectedEdge directedEdge = this.patternViewService.createDirectedEdgeAndAddToPatternView(patternViewId, request);
            return DirectedEdgeModel.from(directedEdge);
        } else {
            this.patternViewService.addDirectedEdgeToPatternView(patternViewId, request.getDirectedEdgeId());
            DirectedEdge directedEdge = this.patternViewService.getDirectedEdgeOfPatternViewById(patternViewId, request.getDirectedEdgeId());
            return DirectedEdgeModel.from(directedEdge);
        }
    }

    @Operation(operationId = "getDirectedEdgesOfView", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Retrieve directed edges of view")
    @GetMapping(value = "/{patternViewId}/directed-edges")
    public List<DirectedEdgeModel> getDirectedEdgesOfView(@PathVariable UUID patternViewId) {
        return this.patternViewService.getDirectedEdgesByPatternViewId(patternViewId)
            .stream()
            .map(DirectedEdgeModel::from)
            .collect(Collectors.toList());
    }

    @Operation(operationId = "getDirectedEdgeOfPatternViewById", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Retrieve directed edge of pattern view by id")
    @GetMapping(value = "/{patternViewId}/directed-edges/{directedEdgeId}")
    public DirectedEdgeModel getDirectedEdgeOfPatternViewById(@PathVariable UUID patternViewId, @PathVariable UUID directedEdgeId) {
        DirectedEdge directedEdge = this.patternViewService.getDirectedEdgeOfPatternViewById(patternViewId, directedEdgeId);
        return DirectedEdgeModel.from(directedEdge);
    }

    @Operation(operationId = "updateDirectedEdgeOfPatternViewById", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Update directed edge of pattern view by id")
    @PutMapping(value = "/{patternViewId}/directed-edges/{directedEdgeId}")
    public DirectedEdgeModel putDirectedEdgeToPatternView(@PathVariable UUID patternViewId, @PathVariable UUID directedEdgeId,
                                                          @RequestBody UpdateDirectedEdgeRequest request) {
        request.setDirectedEdgeId(directedEdgeId);
        DirectedEdge directedEdge = this.patternViewService.updateDirectedEdgeOfPatternView(patternViewId, request);
        return DirectedEdgeModel.from(directedEdge);
    }

    @Operation(operationId = "removeDirectedEdgeFromPatternView", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Remove directed edge of pattern view by id")
    @DeleteMapping(value = "/{patternViewId}/directed-edges/{directedEdgeId}")
    public ResponseEntity<?> removeDirectedEdgeFromPatternView(@PathVariable UUID patternViewId, @PathVariable UUID directedEdgeId) {
        this.patternViewService.removeDirectedEdgeFromPatternView(patternViewId, directedEdgeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(operationId = "addUndirectedEdgeToView", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Add undirected edge to pattern view")
    @PostMapping(value = "/{patternViewId}/undirected-edges")
    @CrossOrigin(exposedHeaders = "Location")
    @ResponseStatus(HttpStatus.OK)
    public UndirectedEdgeModel addUndirectedEdgeToView(@PathVariable UUID patternViewId, @RequestBody AddUndirectedEdgeToViewRequest request) {
        if (request.isNewEdge()) {
            UndirectedEdge undirectedEdge = this.patternViewService.createUndirectedEdgeAndAddToPatternView(patternViewId, request);
            return UndirectedEdgeModel.from(undirectedEdge);
        } else {
            this.patternViewService.addUndirectedEdgeToPatternView(patternViewId, request.getUndirectedEdgeId());
            UndirectedEdge undirectedEdge = this.patternViewService.getUndirectedEdgeOfPatternViewById(patternViewId, request.getUndirectedEdgeId());
            return UndirectedEdgeModel.from(undirectedEdge);
        }
    }

    @Operation(operationId = "getUndirectedEdgesOfView", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Retrieve undirected edges of pattern view")
    @GetMapping(value = "/{patternViewId}/undirected-edges")
    public List<UndirectedEdgeModel> getUndirectedEdgesOfView(@PathVariable UUID patternViewId) {
        return this.patternViewService.getUndirectedEdgesByPatternViewId(patternViewId)
            .stream()
            .map(UndirectedEdgeModel::from)
            .collect(Collectors.toList());
    }

    @Operation(operationId = "getUndirectedEdgeOfPatternViewById", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Retrieve undirected edge of pattern view by id")
    @GetMapping(value = "/{patternViewId}/undirected-edges/{undirectedEdgeId}")
    public UndirectedEdgeModel getUndirectedEdgeOfPatternViewById(@PathVariable UUID patternViewId, @PathVariable UUID undirectedEdgeId) {
        UndirectedEdge undirectedEdge = this.patternViewService.getUndirectedEdgeOfPatternViewById(patternViewId, undirectedEdgeId);
        return UndirectedEdgeModel.from(undirectedEdge);
    }

    @Operation(operationId = "updateUndirectedEdgeOfPatternView", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Update undirected edge of pattern view by id")
    @PutMapping(value = "/{patternViewId}/undirected-edges/{undirectedEdgeId}")
    public UndirectedEdgeModel putUndirectedEdgeToPatternView(@PathVariable UUID patternViewId, @PathVariable UUID undirectedEdgeId,
                                                              @RequestBody UpdateUndirectedEdgeRequest request) {
        request.setUndirectedEdgeId(undirectedEdgeId);
        UndirectedEdge undirectedEdge = this.patternViewService.updateUndirectedEdgeOfPatternView(patternViewId, request);
        return UndirectedEdgeModel.from(undirectedEdge);
    }

    @Operation(operationId = "removeUndirectedEdgeFromPatternView", responses = {@ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", content = @Content)}, description = "Remove undirected edge of pattern view by id")
    @DeleteMapping(value = "/{patternViewId}/undirected-edges/{undirectedEdgeId}")
    public ResponseEntity<?> removeUndirectedEdgeFromPatternView(@PathVariable UUID patternViewId, @PathVariable UUID undirectedEdgeId) {
        this.patternViewService.removeUndirectedEdgeFromPatternView(patternViewId, undirectedEdgeId);
        return ResponseEntity.noContent().build();
    }
}
