package io.github.patternatlas.api.service.aggregation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.patternatlas.api.entities.solutionaggregation.SolutionModel;
import io.github.patternatlas.api.rest.dto.AggregationContext;
import io.github.patternatlas.api.service.SolutionModelService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AggregationEngine {

    private SolutionModelService solutionModelService;
    private List<AggregationProgram> aggregationPrograms;

    public List<AggregationContext> findAllAggregationContexts(UUID solutionModelId) {
        var solutionModel = solutionModelService.getSolutionModel(solutionModelId);
        var allFoundAggregationContexts = new ArrayList<AggregationContext>();
        for (var aggregationProgram : aggregationPrograms) {
            var aggregationContexts = aggregationProgram.findAggregationContexts(solutionModel);
            if (aggregationContexts != null) {
                allFoundAggregationContexts.addAll(aggregationContexts);
            }
        }
        return allFoundAggregationContexts;
    }
}
