package io.github.patternatlas.api.aggregation;

import static org.junit.Assert.assertEquals;

import org.apache.catalina.Store;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.patternatlas.api.entities.Pattern;
import io.github.patternatlas.api.entities.solutionmodel.SolutionModel;
import io.github.patternatlas.api.integration.IntegrationTest;
import io.github.patternatlas.api.repositories.PatternRepository;
import io.github.patternatlas.api.rest.model.AddEdgeToSolutionModelRequest;
import io.github.patternatlas.api.rest.model.CreateConcreteSolutionDescriptorRequest;
import io.github.patternatlas.api.service.ConcreteSolutionDescriptorService;
import io.github.patternatlas.api.service.SolutionModelService;
import io.github.patternatlas.api.service.aggregation.AggregationEngine;

@SpringBootTest
public class AggregationEngineTest extends IntegrationTest {

    @Autowired
    private SolutionModelService solutionModelService;

    @Autowired
    ConcreteSolutionDescriptorService concreteSolutionDescriptorService;

    @Autowired
    AggregationEngine aggregationEngine;

    @Autowired
    private PatternRepository patternRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void givenSolutionModelAndDetectorGraphFindOneMatch() throws Exception {
        // Create Pattern 1
        Pattern pattern1 = new Pattern();
        pattern1.setName("Test Pattern 1");
        pattern1 = patternRepository.save(pattern1);

        var solutionModel = new SolutionModel();
        solutionModel.setName("Test Solution Model");
        solutionModel = solutionModelService.createSolutionModel(solutionModel);

        // Create Concrete Solution Descriptor 1
        var csd1Request = new CreateConcreteSolutionDescriptorRequest();
        csd1Request.setName("Concrete Solution Descriptor 1");
        csd1Request.setType("TestType1");
        var requirements1 = "[\"B\"]";
        csd1Request.setRequirements(objectMapper.readTree(requirements1));
        var capabilities1 = "[\"A\"]";
        csd1Request.setCapabilities(objectMapper.readTree(capabilities1));
        var csd1 = concreteSolutionDescriptorService.createConcreteSolutionDescriptor(csd1Request);
        var csd1Instance = solutionModelService.addConcreteSolutionInstance(solutionModel.getId(), csd1.getId());

        // Create Concrete Solution Descriptor 2
        var csd2Request = new CreateConcreteSolutionDescriptorRequest();
        csd2Request.setName("Concrete Solution Descriptor 2");
        csd2Request.setType("TestType2");
        var capabilities2 = "[\"B\"]";
        csd2Request.setCapabilities(objectMapper.readTree(capabilities2));
        var csd2 = concreteSolutionDescriptorService.createConcreteSolutionDescriptor(csd2Request);
        var csd2Instance = solutionModelService.addConcreteSolutionInstance(solutionModel.getId(), csd2.getId());

        // Create Edge between Concrete Solution Descriptors
        var edgeRequest = new AddEdgeToSolutionModelRequest();
        edgeRequest.setConcreteSolutionInstanceId1(csd1Instance.getId());
        edgeRequest.setConcreteSolutionInstanceId2(csd2Instance.getId());
        edgeRequest.setDirected(true);
        edgeRequest.setEdgeSemantic("TestEdgeSemantic");
        solutionModelService.createSolutionModelEdge(solutionModel.getId(), edgeRequest);

        var aggregationContexts = aggregationEngine.findAllAggregationContexts(solutionModel.getId());
        assertEquals(1, aggregationContexts.size());
    }
}
