package io.github.patternatlas.api.service.aggregation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.patternatlas.api.entities.solutionmodel.SolutionModel;

@Service
public class AggregationEngine {

    List<AggregationProgram> aggregationPrograms;

    public List<SolutionModel> findAllAggregationContexts(UUID solutionModelId) {
        return null;
    }
}
