package io.github.patternatlas.api.rest.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.patternatlas.api.rest.dto.AggregationContext;
import io.github.patternatlas.api.rest.dto.SolutionModelDto;
import io.github.patternatlas.api.service.SolutionModelService;
import io.github.patternatlas.api.service.aggregation.AggregationContextFinder;
import io.github.patternatlas.api.service.aggregation.AggregationEngine;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

@RestController
@CommonsLog
@CrossOrigin(allowedHeaders = "*", origins = "*")
@AllArgsConstructor
@Tag(name = "Concrete Solution Aggregation")
@RequestMapping(value = "/aggregation-engine", produces = "application/json")
public class AggregationEngineController {

    private AggregationEngine aggregationEngine;
    private AggregationContextFinder aggregationContextFinder;
    private SolutionModelService solutionModelService;

    @Transactional
    @PostMapping("/solution-models/{solutionModelId}/aggregation-contexts")
    public List<AggregationContext> findAggregationContexts(@PathVariable UUID solutionModelId) {
        return this.aggregationContextFinder.findAggregationContexts(solutionModelId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @PostMapping("/solution-models/{solutionModelId}/aggregate")
    public SolutionModelDto aggregate(@PathVariable UUID solutionModelId, @RequestBody AggregationContext aggregationContext) {
        var solutionModel = this.aggregationEngine.aggregate(solutionModelId, aggregationContext);
        solutionModel = this.solutionModelService.getSolutionModel(solutionModel.getId());
        return new SolutionModelDto(
            solutionModel.getId(),
            solutionModel.getName(),
            solutionModel.getConcreteSolutionInstances().stream()
                .map(csi -> new SolutionModelDto.SolutionModelConcreteSolutionInstanceDto(
                    csi.getId(),
                    csi.getConcreteSolutionDescriptor().getId(),
                    csi.getProperties()
                ))
                .toList(),
            solutionModel.getEdges().stream()
                .map(e -> new SolutionModelDto.SolutionModelEdgeDto(
                    e.getId(),
                    e.getDirected(),
                    e.getEdgeSemantic(),
                    e.getCsInstance1().getId(),
                    e.getCsInstance2().getId(),
                    e.getDescription()
                ))
                .toList(),
            solutionModel.getDesignModel() == null ? null : solutionModel.getDesignModel().getId(),
            solutionModel.getDesignModel() == null ? null : solutionModel.getDesignModel().getUri(),
            solutionModel.getDesignModel() == null ? null : solutionModel.getDesignModel().getName(),
            solutionModel.getPreviousSolutionModelId());
    }
}
