package io.github.patternatlas.api.aggregation;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.patternatlas.api.entities.Pattern;
import io.github.patternatlas.api.entities.PatternLanguage;
import io.github.patternatlas.api.entities.PatternSchema;
import io.github.patternatlas.api.entities.solutionaggregation.ConcreteSolutionDescriptor;
import io.github.patternatlas.api.entities.solutionaggregation.SolutionModel;
import io.github.patternatlas.api.entities.solutionaggregation.SolutionModelConcreteSolutionInstance;
import io.github.patternatlas.api.entities.solutionaggregation.SolutionModelEdge;
import io.github.patternatlas.api.integration.IntegrationTest;
import io.github.patternatlas.api.rest.dto.AddEdgeToSolutionModelRequest;
import io.github.patternatlas.api.rest.dto.CreateConcreteSolutionDescriptorRequest;
import io.github.patternatlas.api.service.ConcreteSolutionDescriptorService;
import io.github.patternatlas.api.service.PatternLanguageService;
import io.github.patternatlas.api.service.PatternService;
import io.github.patternatlas.api.service.SolutionModelService;
import io.github.patternatlas.api.service.aggregation.AggregationEngine;
import io.github.patternatlas.api.service.aggregation.AggregationProgram;

@SpringBootTest
public class AggregationEngineTest extends IntegrationTest {

    @Autowired
    ConcreteSolutionDescriptorService concreteSolutionDescriptorService;

    @Autowired
    SolutionModelService solutionModelService;

    @Autowired
    PatternLanguageService patternLanguageService;

    @Autowired
    PatternService patternService;

    @Autowired
    AggregationEngine aggregationEngine;

    @Autowired
    List<AggregationProgram> aggregationPrograms;

    @Test
    public void givenAggregationProgramAndSolutionModelThenFindTwoAggregationContext() {
        ConcreteSolutionDescriptor cs1 = new ConcreteSolutionDescriptor();
        cs1.setId(java.util.UUID.randomUUID());
        cs1.setType("TestType1");
        cs1.setRequirements(new HashSet<>(List.of("A")));
        cs1.setCapabilities(new HashSet<>(List.of("B")));
        ConcreteSolutionDescriptor cs2 = new ConcreteSolutionDescriptor();
        cs2.setId(java.util.UUID.randomUUID());
        cs2.setType("TestType2");
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

        var aggregationContexts = aggregationPrograms.get(0).findAggregationContexts(solutionModel);
        assertEquals(2, aggregationContexts.size());
        assertEquals(2, aggregationContexts.get(0).getConcreteSolutionInstanceIds().size());
        assertEquals(2, aggregationContexts.get(1).getConcreteSolutionInstanceIds().size());
        assertEquals(1, aggregationContexts.get(0).getSolutionModelEdgeIds().size());
        assertEquals(1, aggregationContexts.get(1).getSolutionModelEdgeIds().size());
    }

    @Test
    public void givenSolutionModelThenFindTwoAggregationContext() {
        var patternLanguage = new PatternLanguage();
        patternLanguage.setName("Test Pattern Language");
        patternLanguage.setUri("http://test.com");
        patternLanguage.setPatternSchema(new PatternSchema());
        patternLanguage = this.patternLanguageService.createPatternLanguage(patternLanguage);

        var pattern = new Pattern();
        pattern.setName("Test Pattern");
        pattern.setUri("http://test.com");
        pattern.setPatternLanguage(patternLanguage);
        pattern = this.patternService.createPattern(pattern);

        var request = new CreateConcreteSolutionDescriptorRequest();
        request.setType("TestType1");
        request.setRequirements(new HashSet<>(List.of("A")));
        request.setCapabilities(new HashSet<>(List.of("B")));
        request.setPatternId(pattern.getId());
        var cs1 = this.concreteSolutionDescriptorService.createConcreteSolutionDescriptor(request);

        var request2 = new CreateConcreteSolutionDescriptorRequest();
        request2.setType("TestType2");
        request2.setRequirements(new HashSet<>(List.of("B")));
        request2.setCapabilities(new HashSet<>(List.of("C")));
        request2.setPatternId(pattern.getId());
        var cs2 = this.concreteSolutionDescriptorService.createConcreteSolutionDescriptor(request2);

        var solutionModel = new SolutionModel();
        solutionModel.setName("Test Solution Model");
        solutionModel = this.solutionModelService.createSolutionModel(solutionModel);

        var csi1 = this.solutionModelService.addConcreteSolutionInstance(solutionModel.getId(), cs1.getId());
        var csi2 = this.solutionModelService.addConcreteSolutionInstance(solutionModel.getId(), cs2.getId());
        var csi3 = this.solutionModelService.addConcreteSolutionInstance(solutionModel.getId(), cs1.getId());
        var csi4 = this.solutionModelService.addConcreteSolutionInstance(solutionModel.getId(), cs2.getId());

        AddEdgeToSolutionModelRequest request3 = new AddEdgeToSolutionModelRequest();
        request3.setDirected(true);
        request3.setEdgeSemantic("TestEdgeSemantic");
        request3.setConcreteSolutionInstanceId1(csi1.getId());
        request3.setConcreteSolutionInstanceId2(csi2.getId());
        this.solutionModelService.createSolutionModelEdge(solutionModel.getId(), request3);

        AddEdgeToSolutionModelRequest request4 = new AddEdgeToSolutionModelRequest();
        request4.setDirected(true);
        request4.setEdgeSemantic("TestEdgeSemantic");
        request4.setConcreteSolutionInstanceId1(csi3.getId());
        request4.setConcreteSolutionInstanceId2(csi4.getId());
        this.solutionModelService.createSolutionModelEdge(solutionModel.getId(), request4);

        var aggregationContexts = aggregationEngine.findAllAggregationContexts(solutionModel.getId());
        assertEquals(2, aggregationContexts.size());
        assertEquals(2, aggregationContexts.get(0).getConcreteSolutionInstanceIds().size());
        assertEquals(2, aggregationContexts.get(1).getConcreteSolutionInstanceIds().size());
        assertEquals(1, aggregationContexts.get(0).getSolutionModelEdgeIds().size());
        assertEquals(1, aggregationContexts.get(1).getSolutionModelEdgeIds().size());
    }
}
