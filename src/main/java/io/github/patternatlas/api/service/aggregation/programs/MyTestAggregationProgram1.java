package io.github.patternatlas.api.service.aggregation.programs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismInspector;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.patternatlas.api.entities.solutionaggregation.ConcreteSolutionDescriptor;
import io.github.patternatlas.api.entities.solutionaggregation.SolutionModel;
import io.github.patternatlas.api.rest.dto.AggregationContext;
import io.github.patternatlas.api.service.aggregation.DetectorGraph;
import io.github.patternatlas.api.service.aggregation.DetectorGraphEdge;
import io.github.patternatlas.api.service.aggregation.EdgeComparator;
import io.github.patternatlas.api.service.aggregation.NodeComparator;
import io.github.patternatlas.api.service.aggregation.OperationTarget;
import io.github.patternatlas.api.service.aggregation.PseydoEdge;
import io.github.patternatlas.api.service.aggregation.PseydoNode;
import lombok.Getter;
import lombok.extern.apachecommons.CommonsLog;

@Getter
@CommonsLog
@Component
public class MyTestAggregationProgram1 implements AggregationProgram {


    private DetectorGraph detectorGraph;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MyTestAggregationProgram1() {
        this.setupDetectorGraph();
    }

    public String getId() {
        return this.getClass().getName();
    }

    private void setupDetectorGraph() {
        var graph = new DetectorGraph();
        Set<String> requirements1 = new HashSet<>(List.of("A"));
        Set<String> capabilities1 = new HashSet<>(List.of("B"));
        var ot1 = OperationTarget.builder()
            .type("TestType1")
            .requirements(requirements1)
            .capabilities(capabilities1)
            .id(UUID.fromString("de520326-6356-4a62-b57e-359bfd986835"))
            .build();
        graph.getOperationTargets().add(ot1);

        Set<String> requirements2 = new HashSet<>(List.of("B"));
        Set<String> capabilities2 = new HashSet<>(List.of("C"));
        var ot2 = OperationTarget.builder()
            .type("TestType2")
            .requirements(requirements2)
            .capabilities(capabilities2)
            .id(UUID.fromString("1ca8024a-f19a-4994-b195-7d96105238cb"))
            .build();
        graph.getOperationTargets().add(ot2);

        graph.getEdges().add(DetectorGraphEdge.builder()
            .ot1(ot1)
            .ot2(ot2)
            .directed(true)
            .edgeSemantic("TestEdgeSemantic")
            .build());
        this.detectorGraph = graph;
    }

    private static Graph<PseydoNode, PseydoEdge> buildEmptyGraph() {
        return GraphTypeBuilder
            .<PseydoNode, PseydoEdge>directed().allowingMultipleEdges(false)
            .allowingSelfLoops(false).edgeClass(PseydoEdge.class).weighted(true).buildGraph();
    }

    @Override
    public List<AggregationContext> findAggregationContexts(SolutionModel solutionModel) {
        // Note: we assume that the solution model is a connected graph, thus, no single node is disconnected
        var aggregationContexts = new ArrayList<AggregationContext>();

        var solutionModelRepresentation = buildEmptyGraph();
        for (var edge : solutionModel.getEdges()) {
            var n1 = PseydoNode.from(edge.getCsInstance1());
            var n2 = PseydoNode.from(edge.getCsInstance2());
            if (solutionModelRepresentation.vertexSet().stream().noneMatch(n -> n.equals(n1))) {
                solutionModelRepresentation.addVertex(n1);
            }
            if (solutionModelRepresentation.vertexSet().stream().noneMatch(n -> n.equals(n2))) {
                solutionModelRepresentation.addVertex(n2);
            }
            solutionModelRepresentation.addEdge(n1, n2, PseydoEdge.from(edge));
        }

        var detectorGraphRepresentation = buildEmptyGraph();
        for (var edge : this.detectorGraph.getEdges()) {
            var n1 = PseydoNode.from(edge.getOt1());
            var n2 = PseydoNode.from(edge.getOt2());
            if (detectorGraphRepresentation.vertexSet().stream().noneMatch(n -> n.equals(n1))) {
                detectorGraphRepresentation.addVertex(n1);
            }
            if (detectorGraphRepresentation.vertexSet().stream().noneMatch(n -> n.equals(n2))) {
                detectorGraphRepresentation.addVertex(n2);
            }
            detectorGraphRepresentation.addEdge(n1, n2, PseydoEdge.from(edge));
        }

        VF2SubgraphIsomorphismInspector<PseydoNode, PseydoEdge> inspector =
            new VF2SubgraphIsomorphismInspector<>(solutionModelRepresentation, detectorGraphRepresentation, new NodeComparator(),
                new EdgeComparator());
        Iterator<GraphMapping<PseydoNode, PseydoEdge>> iter = inspector.getMappings();

        while (iter.hasNext()) {
            GraphMapping<PseydoNode, PseydoEdge> mapping = iter.next();

            var aggregationContext = new AggregationContext();
            aggregationContext.setAggregationProgramName(this.getId());
            detectorGraphRepresentation.vertexSet().forEach((PseydoNode n) -> {
                var targetNode = mapping.getVertexCorrespondence(n, false);
                aggregationContext.getConcreteSolutionInstanceIds().add(targetNode.getId());
            });
            detectorGraphRepresentation.edgeSet().forEach((PseydoEdge e) -> {
                var targetEdge = mapping.getEdgeCorrespondence(e, false);
                aggregationContext.getSolutionModelEdgeIds().add(targetEdge.getSolutionModelEdgeId());
            });
            aggregationContexts.add(aggregationContext);
        }

        return aggregationContexts;
    }

    @Override
    public boolean canAggregate(AggregationContext aggregationContext) {
        return true;
    }

    @Override
    public ConcreteSolutionDescriptor aggregate(AggregationContext aggregationContext) {
        return null;
    }

    @Override
    public SolutionModel updateSolutionModel(SolutionModel solutionModel, AggregationContext aggregationContext,
                                             ConcreteSolutionDescriptor concreteSolutionDescriptor) {
        return null;
    }
}
