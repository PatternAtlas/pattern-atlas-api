package io.github.patternatlas.api.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismInspector;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.junit.Test;

import io.github.patternatlas.api.entities.solutionaggregation.ConcreteSolutionDescriptor;
import io.github.patternatlas.api.entities.solutionaggregation.SolutionModel;
import io.github.patternatlas.api.entities.solutionaggregation.SolutionModelConcreteSolutionInstance;
import io.github.patternatlas.api.entities.solutionaggregation.SolutionModelEdge;
import io.github.patternatlas.api.rest.dto.AggregationContext;
import io.github.patternatlas.api.service.aggregation.EdgeComparator;
import io.github.patternatlas.api.service.aggregation.NodeComparator;
import io.github.patternatlas.api.service.aggregation.PseydoEdge;
import io.github.patternatlas.api.service.aggregation.PseydoNode;

public class GraphTest {

    private static Graph<PseydoNode, PseydoEdge> buildEmptyGraph() {
        return GraphTypeBuilder
            .<PseydoNode, PseydoEdge>directed().allowingMultipleEdges(false)
            .allowingSelfLoops(false).edgeClass(PseydoEdge.class).weighted(true).buildGraph();
    }

    @Test
    public void givenDetectorGraphAndSolutionModelThenFindMapping() {
        ConcreteSolutionDescriptor cs1 = new ConcreteSolutionDescriptor();
        cs1.setId(java.util.UUID.randomUUID());
        cs1.setType("type1");
        cs1.setRequirements(new HashSet<>(List.of("A")));
        cs1.setCapabilities(new HashSet<>(List.of("B")));
        ConcreteSolutionDescriptor cs2 = new ConcreteSolutionDescriptor();
        cs2.setId(java.util.UUID.randomUUID());
        cs2.setType("type2");
        cs2.setRequirements(new HashSet<>(List.of("B")));
        cs2.setCapabilities(new HashSet<>(List.of("C")));

        ConcreteSolutionDescriptor cs3 = new ConcreteSolutionDescriptor();
        cs3.setId(java.util.UUID.randomUUID());
        cs3.setType("type3");
        cs3.setRequirements(new HashSet<>(List.of("C")));
        cs3.setCapabilities(new HashSet<>(List.of("D")));

        var solutionModel = new SolutionModel();
        solutionModel.setName("Test Solution Model");
        solutionModel.setConcreteSolutionInstances(new ArrayList<>());
        solutionModel.setEdges(new ArrayList<>());
        var csi1 = new SolutionModelConcreteSolutionInstance(solutionModel, cs1);
        csi1.setId(UUID.randomUUID());
        var csi2 = new SolutionModelConcreteSolutionInstance(solutionModel, cs2);
        csi2.setId(UUID.randomUUID());
        var csi3 = new SolutionModelConcreteSolutionInstance(solutionModel, cs3);
        csi3.setId(UUID.randomUUID());
        solutionModel.getConcreteSolutionInstances().add(csi1);
        solutionModel.getConcreteSolutionInstances().add(csi2);
        solutionModel.getConcreteSolutionInstances().add(csi3);
        var edge = new SolutionModelEdge();
        edge.setCsInstance1(csi1);
        edge.setCsInstance2(csi2);
        edge.setDirected(true);
        edge.setEdgeSemantic("TestEdgeSemantic");
        edge.setId(UUID.randomUUID());
        solutionModel.getEdges().add(edge);

        var edge2 = new SolutionModelEdge();
        edge2.setCsInstance1(csi2);
        edge2.setCsInstance2(csi3);
        edge2.setDirected(true);
        edge2.setEdgeSemantic("TestEdgeSemantic");
        solutionModel.getEdges().add(edge2);

        var solutionModelGraph = buildEmptyGraph();
        var n1 = PseydoNode.from(csi1);
        n1.setName("n1");
        var n2 = PseydoNode.from(csi2);
        n2.setName("n2");
        var n3 = PseydoNode.from(csi3);
        n3.setName("n3");
        solutionModelGraph.addVertex(n1);
        solutionModelGraph.addVertex(n2);
        solutionModelGraph.addVertex(n3);
        solutionModelGraph.addEdge(n1, n2, PseydoEdge.from(edge));
        solutionModelGraph.addEdge(n2, n3, PseydoEdge.from(edge2));

        var detectorGraph = buildEmptyGraph();
        var ot1 = PseydoNode.from(csi1);
        ot1.setName("ot1");
        var ot2 = PseydoNode.from(csi2);
        ot2.setName("ot2");
        detectorGraph.addVertex(ot1);
        detectorGraph.addVertex(ot2);
        detectorGraph.addEdge(ot1, ot2, PseydoEdge.from(edge));

        VF2SubgraphIsomorphismInspector<PseydoNode, PseydoEdge> inspector =
            new VF2SubgraphIsomorphismInspector<>(solutionModelGraph, detectorGraph, new NodeComparator(), new EdgeComparator());
        Iterator<GraphMapping<PseydoNode, PseydoEdge>> iter = inspector.getMappings();

        var graphMappings = new ArrayList<GraphMapping<PseydoNode, PseydoEdge>>();
        while (iter.hasNext()) {
            GraphMapping<PseydoNode, PseydoEdge> mapping = iter.next();
            graphMappings.add(mapping);
        }
        assertTrue(inspector.isomorphismExists());
        var aggregationContexts = new ArrayList<AggregationContext>();

        for (var mapping : graphMappings) {
            var aggregationContext = new AggregationContext();
            detectorGraph.vertexSet().forEach((PseydoNode n) -> {
                var targetNode = mapping.getVertexCorrespondence(n, false);
                aggregationContext.getConcreteSolutionInstanceIds().add(targetNode.getId());
            });
            detectorGraph.edgeSet().forEach((PseydoEdge e) -> {
                var targetEdge = mapping.getEdgeCorrespondence(e, false);
                aggregationContext.getSolutionModelEdgeIds().add(targetEdge.getSolutionModelEdgeId());
            });
            aggregationContexts.add(aggregationContext);
        }
        assertEquals(1, aggregationContexts.size());
        assertEquals(2, aggregationContexts.get(0).getConcreteSolutionInstanceIds().size());
        assertEquals(1, aggregationContexts.get(0).getSolutionModelEdgeIds().size());
    }

    @Test
    public void givenDetectorGraphAndSolutionModelThenFindTwoMappings() {
        ConcreteSolutionDescriptor cs1 = new ConcreteSolutionDescriptor();
        cs1.setId(java.util.UUID.randomUUID());
        cs1.setType("type1");
        cs1.setRequirements(new HashSet<>(List.of("A")));
        cs1.setCapabilities(new HashSet<>(List.of("B")));
        ConcreteSolutionDescriptor cs2 = new ConcreteSolutionDescriptor();
        cs2.setId(java.util.UUID.randomUUID());
        cs2.setType("type2");
        cs2.setRequirements(new HashSet<>(List.of("B")));
        cs2.setCapabilities(new HashSet<>(List.of("C")));

        var solutionModel = new SolutionModel();
        solutionModel.setName("Test Solution Model");
        solutionModel.setConcreteSolutionInstances(new ArrayList<>());
        solutionModel.setEdges(new ArrayList<>());
        var csi1 = new SolutionModelConcreteSolutionInstance(solutionModel, cs1);
        csi1.setId(UUID.randomUUID());
        var csi2 = new SolutionModelConcreteSolutionInstance(solutionModel, cs2);
        csi2.setId(UUID.randomUUID());
        var csi3 = new SolutionModelConcreteSolutionInstance(solutionModel, cs1);
        csi3.setId(UUID.randomUUID());
        var csi4 = new SolutionModelConcreteSolutionInstance(solutionModel, cs2);
        csi4.setId(UUID.randomUUID());

        solutionModel.getConcreteSolutionInstances().add(csi1);
        solutionModel.getConcreteSolutionInstances().add(csi2);
        solutionModel.getConcreteSolutionInstances().add(csi3);
        solutionModel.getConcreteSolutionInstances().add(csi4);

        var edge1 = new SolutionModelEdge();
        edge1.setCsInstance1(csi1);
        edge1.setCsInstance2(csi2);
        edge1.setDirected(true);
        edge1.setEdgeSemantic("TestEdgeSemantic");
        edge1.setId(UUID.randomUUID());
        solutionModel.getEdges().add(edge1);

        var edge2 = new SolutionModelEdge();
        edge2.setCsInstance1(csi3);
        edge2.setCsInstance2(csi4);
        edge2.setDirected(true);
        edge2.setEdgeSemantic("TestEdgeSemantic");
        edge2.setId(UUID.randomUUID());
        solutionModel.getEdges().add(edge2);

        var solutionModelGraph = buildEmptyGraph();
        var n1 = PseydoNode.from(csi1);
        n1.setName("n1");
        var n2 = PseydoNode.from(csi2);
        n2.setName("n2");
        var n3 = PseydoNode.from(csi3);
        n3.setName("n3");
        var n4 = PseydoNode.from(csi4);
        n4.setName("n4");
        solutionModelGraph.addVertex(n1);
        solutionModelGraph.addVertex(n2);
        solutionModelGraph.addVertex(n3);
        solutionModelGraph.addVertex(n4);
        solutionModelGraph.addEdge(n1, n2, PseydoEdge.from(edge1));
        solutionModelGraph.addEdge(n3, n4, PseydoEdge.from(edge2));

        var detectorGraph = buildEmptyGraph();
        var ot1 = PseydoNode.from(csi1);
        ot1.setName("ot1");
        var ot2 = PseydoNode.from(csi2);
        ot2.setName("ot2");
        detectorGraph.addVertex(ot1);
        detectorGraph.addVertex(ot2);
        detectorGraph.addEdge(ot1, ot2, PseydoEdge.from(edge1));

        VF2SubgraphIsomorphismInspector<PseydoNode, PseydoEdge> inspector =
            new VF2SubgraphIsomorphismInspector<>(solutionModelGraph, detectorGraph, new NodeComparator(), new EdgeComparator());
        Iterator<GraphMapping<PseydoNode, PseydoEdge>> iter = inspector.getMappings();

        var graphMappings = new ArrayList<GraphMapping<PseydoNode, PseydoEdge>>();
        while (iter.hasNext()) {
            GraphMapping<PseydoNode, PseydoEdge> mapping = iter.next();
            graphMappings.add(mapping);
        }
        assertTrue(inspector.isomorphismExists());

        var aggregationContexts = new ArrayList<AggregationContext>();

        for (var mapping : graphMappings) {
            var aggregationContext = new AggregationContext();
            detectorGraph.vertexSet().forEach((PseydoNode n) -> {
                var targetNode = mapping.getVertexCorrespondence(n, false);
                aggregationContext.getConcreteSolutionInstanceIds().add(targetNode.getId());
            });
            detectorGraph.edgeSet().forEach((PseydoEdge e) -> {
                var targetEdge = mapping.getEdgeCorrespondence(e, false);
                aggregationContext.getSolutionModelEdgeIds().add(targetEdge.getSolutionModelEdgeId());
            });
            aggregationContexts.add(aggregationContext);
        }
        assertEquals(2, aggregationContexts.size());
        assertEquals(2, aggregationContexts.get(0).getConcreteSolutionInstanceIds().size());
        assertEquals(2, aggregationContexts.get(1).getConcreteSolutionInstanceIds().size());
        assertEquals(1, aggregationContexts.get(0).getSolutionModelEdgeIds().size());
        assertEquals(1, aggregationContexts.get(1).getSolutionModelEdgeIds().size());
    }

}
