package io.github.patternatlas.api.service.aggregation;

import java.util.List;

import io.github.patternatlas.api.entities.solutionaggregation.ConcreteSolutionDescriptor;
import io.github.patternatlas.api.entities.solutionaggregation.SolutionModel;
import io.github.patternatlas.api.rest.dto.AggregationContext;

public interface AggregationProgram {

    String getId();

    DetectorGraph getDetectorGraph();
    List<AggregationContext> findAggregationContexts(SolutionModel solutionModel);
    boolean canAggregate(AggregationContext aggregationContext);

    ConcreteSolutionDescriptor aggregate(AggregationContext aggregationContext);

    SolutionModel updateSolutionModel(SolutionModel solutionModel, SolutionModel aggregationContext, ConcreteSolutionDescriptor concreteSolutionDescriptor);
}
