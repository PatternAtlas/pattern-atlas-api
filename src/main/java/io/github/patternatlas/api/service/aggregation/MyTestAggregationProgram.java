package io.github.patternatlas.api.service.aggregation;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.patternatlas.api.entities.solutionmodel.ConcreteSolutionDescriptor;
import io.github.patternatlas.api.entities.solutionmodel.SolutionModel;
import lombok.Getter;
import lombok.extern.apachecommons.CommonsLog;

@Getter
@CommonsLog
@Component
public class MyTestAggregationProgram implements AggregationProgram {


    private DetectorGraph detectorGraph;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MyTestAggregationProgram() {
        try {
            this.setupDetectorGraph();
        } catch (JsonProcessingException e) {
            log.debug("Error setting up detector graph");
        }
    }

    public String getId() {
        return this.getClass().getName();
    }

    private void setupDetectorGraph() throws JsonProcessingException {
        var graph = new DetectorGraph();
        String capabilities1 = "[\"A\"]";
        String requirements1 = "[\"B\"]";
        var ot1 = OperationTarget.builder()
            .type("TestType1")
            .capabilities(objectMapper.readTree(capabilities1))
            .requirements(objectMapper.readTree(requirements1))
            .build();
        graph.operationTargets.add(ot1);

        String capabilities2 = "[\"B\"]";
        var ot2 = OperationTarget.builder()
            .type("TestType2")
            .capabilities(objectMapper.readTree(capabilities2))
            .build();
        graph.operationTargets.add(ot2);

        graph.edges.add(DetectorGraphEdge.builder()
            .ot1(ot1)
            .ot2(ot2)
            .directed(true)
                .edgeSemantic("TestEdgeSemantic")
            .build());
        this.detectorGraph = graph;
    }

    @Override
    public List<SolutionModel> findAggregationContexts(SolutionModel solutionModel) {
        return null;
    }

    @Override
    public boolean canAggregate(SolutionModel aggregationContext) {
        return false;
    }

    @Override
    public ConcreteSolutionDescriptor aggregate(SolutionModel aggregationContext) {
        return null;
    }

    @Override
    public SolutionModel updateSolutionModel(SolutionModel solutionModel, SolutionModel aggregationContext,
                                             ConcreteSolutionDescriptor concreteSolutionDescriptor) {
        return null;
    }
}
