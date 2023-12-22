package io.github.patternatlas.api.service.aggregation;

import java.util.List;

import io.github.patternatlas.api.entities.solutionmodel.ConcreteSolutionDescriptor;
import io.github.patternatlas.api.entities.solutionmodel.SolutionModel;

public interface AggregationProgram {

    String getId();

    DetectorGraph getDetectorGraph();
    List<SolutionModel> findAggregationContexts(SolutionModel solutionModel);
    boolean canAggregate(SolutionModel aggregationContext);

    ConcreteSolutionDescriptor aggregate(SolutionModel aggregationContext);

    SolutionModel updateSolutionModel(SolutionModel solutionModel, SolutionModel aggregationContext, ConcreteSolutionDescriptor concreteSolutionDescriptor);
}
