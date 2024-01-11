package io.github.patternatlas.api.aggregation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.github.patternatlas.api.entities.Pattern;
import io.github.patternatlas.api.entities.solutionaggregation.ConcreteSolutionDescriptor;
import io.github.patternatlas.api.entities.solutionaggregation.SolutionModel;
import io.github.patternatlas.api.entities.solutionaggregation.SolutionModelConcreteSolutionInstance;
import io.github.patternatlas.api.rest.dto.AddEdgeToSolutionModelRequest;
import io.github.patternatlas.api.rest.dto.CreateConcreteSolutionDescriptorRequest;
import io.github.patternatlas.api.service.ConcreteSolutionDescriptorService;
import io.github.patternatlas.api.service.PatternService;
import io.github.patternatlas.api.service.SolutionModelService;
import io.github.patternatlas.api.service.aggregation.AggregationContextFinder;
import io.github.patternatlas.api.service.aggregation.AggregationEngine;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("existing-db")
@AutoConfigureMockMvc
public class EIPCloudAggregationTest {

    @Autowired
    private SolutionModelService solutionModelService;

    @Autowired
    private ConcreteSolutionDescriptorService concreteSolutionDescriptorService;

    @Autowired
    private PatternService patternService;

    @Autowired
    private AggregationEngine aggregationEngine;

    @Autowired
    private AggregationContextFinder aggregationContextFinder;

    @NotNull
    private CreateConcreteSolutionDescriptorRequest getMessageOrientedMiddlewareRequest(Pattern messageOrientedMiddlewarePattern) {
        var request = new CreateConcreteSolutionDescriptorRequest();
        request.setName("Message-oriented Middleware");
        request.setPatternId(messageOrientedMiddlewarePattern.getId());
        request.setCapabilities(new HashSet<>(Collections.singletonList("ActiveMQRuntime")));
        request.setProperties(ContentHelper.getMessageOrientedMiddlewareProperties());
        request.setType("Spring XML");
        request.setTemplateMimeType("text/xml");
        request.setTemplate(ContentHelper.getMoMTemplate());
        return request;
    }

    @NotNull
    private CreateConcreteSolutionDescriptorRequest getMessageFilterRequest(Pattern messageFilterPattern) {
        var request = new CreateConcreteSolutionDescriptorRequest();
        request.setName("Message Filter");
        request.setPatternId(messageFilterPattern.getId());
        request.setCapabilities(new HashSet<>(Collections.singletonList("MessageFilter")));
        var reqs = new ArrayList<String>();
        reqs.add("InputChannel");
        reqs.add("OutputChannel");
        request.setRequirements(new HashSet<>(reqs));
        request.setProperties(ContentHelper.getMessageFilterProperties());
        request.setType("ActiveMQ XML Route");
        request.setTemplateMimeType("text/xml");
        request.setTemplate(ContentHelper.getMessageFilterTemplate());
        return request;
    }

    @NotNull
    private CreateConcreteSolutionDescriptorRequest getP2PChannelRequest(Pattern pointToPointChannelPattern) {
        var request = new CreateConcreteSolutionDescriptorRequest();
        request.setName("Point to Point Channel");
        request.setPatternId(pointToPointChannelPattern.getId());
        var caps = new ArrayList<String>();
        caps.add("InputChannel");
        caps.add("OutputChannel");
        request.setCapabilities(new HashSet<>(caps));
        var reqs = new ArrayList<String>();
        reqs.add("MessageProducer");
        reqs.add("MessageConsumer");
        request.setRequirements(new HashSet<>(reqs));
        request.setProperties(ContentHelper.getP2PChannelProperties());
        request.setType("ActiveMQ XML Route");
        request.setTemplateMimeType("text/xml");
        request.setTemplate(ContentHelper.getPointToPointChannelTemplate());
        return request;
    }

    @NotNull
    private CreateConcreteSolutionDescriptorRequest getME1Request(Pattern messageEndpointPattern) {
        var request = new CreateConcreteSolutionDescriptorRequest();
        request.setName("Sync Message Producer Endpoint");
        request.setPatternId(messageEndpointPattern.getId());
        request.setCapabilities(new HashSet<>(Collections.singletonList("MessageProducer")));
        request.setRequirements(new HashSet<>(Collections.singletonList("OutputChannel")));
        request.setProperties(ContentHelper.getQueueProducerProperties());
        request.setType("Java String Template");
        request.setTemplateMimeType("text/plain");
        request.setTemplate(ContentHelper.getQueueProducerTemplate());
        return request;
    }

    @NotNull
    private CreateConcreteSolutionDescriptorRequest getME2Request(Pattern messageEndpointPattern)
        throws JsonProcessingException {
        var request = new CreateConcreteSolutionDescriptorRequest();
        request.setName("Sync Message Consumer Endpoint");
        request.setPatternId(messageEndpointPattern.getId());
        request.setCapabilities(new HashSet<>(Collections.singletonList("MessageConsumer")));
        request.setRequirements(new HashSet<>(Collections.singletonList("InputChannel")));
        request.setProperties(ContentHelper.getQueueConsumerProperties());
        request.setType("Java String Template");
        request.setTemplateMimeType("text/plain");
        request.setTemplate(ContentHelper.getQueueConsumerTemplate());
        return request;
    }

    @NotNull
    private AddEdgeToSolutionModelRequest getAddEdgeRequestForP2PChannel2AndMessageFilter(
        SolutionModelConcreteSolutionInstance csdP2PChannel2Instance, SolutionModelConcreteSolutionInstance csdMEConsumerInstance) {
        var request = new AddEdgeToSolutionModelRequest();
        request.setConcreteSolutionInstanceId2(csdP2PChannel2Instance.getId());
        request.setConcreteSolutionInstanceId1(csdMEConsumerInstance.getId());
        request.setEdgeSemantic("receiveFrom");
        request.setDirected(true);
        return request;
    }

    @NotNull
    private AddEdgeToSolutionModelRequest getAddEdgeRequestForMessageFilterAndP2PChannel2(
        SolutionModelConcreteSolutionInstance csdMessageFilterInstance, SolutionModelConcreteSolutionInstance csdP2PChannel2Instance) {
        var request = new AddEdgeToSolutionModelRequest();
        request.setConcreteSolutionInstanceId1(csdMessageFilterInstance.getId());
        request.setConcreteSolutionInstanceId2(csdP2PChannel2Instance.getId());
        request.setEdgeSemantic("sendTo");
        request.setDirected(true);
        return request;
    }

    @NotNull
    private AddEdgeToSolutionModelRequest getAddEdgeRequestForP2PChannel1AndMessageFilter(
        SolutionModelConcreteSolutionInstance csdP2PChannel1Instance, SolutionModelConcreteSolutionInstance csdMessageFilterInstance) {
        var request = new AddEdgeToSolutionModelRequest();
        request.setConcreteSolutionInstanceId2(csdP2PChannel1Instance.getId());
        request.setConcreteSolutionInstanceId1(csdMessageFilterInstance.getId());
        request.setEdgeSemantic("receiveFrom");
        request.setDirected(true);
        return request;
    }

    @NotNull
    private static AddEdgeToSolutionModelRequest getAddEdgeRequestForMEProducerAndP2PChannel1(
        SolutionModelConcreteSolutionInstance csdMEProducerInstance,
        SolutionModelConcreteSolutionInstance csdP2PChannel1Instance) {
        var request = new AddEdgeToSolutionModelRequest();
        request.setConcreteSolutionInstanceId1(csdMEProducerInstance.getId());
        request.setConcreteSolutionInstanceId2(csdP2PChannel1Instance.getId());
        request.setEdgeSemantic("sendTo");
        request.setDirected(true);
        return request;
    }

    @Test
    public void persistsCsd() {
        var request = new CreateConcreteSolutionDescriptorRequest();
        request.setName("Test CSD");
        this.concreteSolutionDescriptorService.createConcreteSolutionDescriptor(request);
    }

    @Test
    public void givenScenario_whenAggregate_thenSuccess() throws Exception {

        // Setup ConcreteSolutionDescriptors
        ConcreteSolutionDescriptor csdME1 = null;
        ConcreteSolutionDescriptor csdME2 = null;
        ConcreteSolutionDescriptor csdP2PChannel = null;
        ConcreteSolutionDescriptor csdMessageFilter = null;
        ConcreteSolutionDescriptor csdMessageOrientedMiddleware = null;
        if (false) {
            // Create ConcreteSolutionDescriptors
            // create five concrete solution descriptors,
            // one for a sending MessageEndpoint,
            // one for a receiving MessageEndpoint,
            // one for a Point-to-Point Channel,
            // one for a MessageFilter,
            // one for a Message-oriented Middleware
            var messageEndpointPattern = this.patternService.getPatternById(UUID.fromString("3953e924-30f6-4561-8e02-e761b417c1d4"));
            var createME1request = getME1Request(messageEndpointPattern);
            csdME1 = this.concreteSolutionDescriptorService.createConcreteSolutionDescriptor(createME1request);
            var createME2request = getME2Request(messageEndpointPattern);
            csdME2 = this.concreteSolutionDescriptorService.createConcreteSolutionDescriptor(createME2request);

            var pointToPointChannelPattern = this.patternService.getPatternById(UUID.fromString("3b3a9b48-63aa-4523-be3a-6a39fdf4bda7"));
            var createP2PChannelRequest = getP2PChannelRequest(pointToPointChannelPattern);
            csdP2PChannel = this.concreteSolutionDescriptorService.createConcreteSolutionDescriptor(createP2PChannelRequest);

            var messageFilterPattern = this.patternService.getPatternById(UUID.fromString("9c49472d-47fc-417e-b75a-512d57e76870"));
            var createMessageFilterRequest = getMessageFilterRequest(messageFilterPattern);
            csdMessageFilter = this.concreteSolutionDescriptorService.createConcreteSolutionDescriptor(createMessageFilterRequest);

            var messageOrientedMiddlewarePattern = this.patternService.getPatternById(UUID.fromString("c12b3086-5fa4-4140-915b-330fbffdfc43"));
            var createMessageOrientedMiddlewareRequest = getMessageOrientedMiddlewareRequest(messageOrientedMiddlewarePattern);
            csdMessageOrientedMiddleware =
                this.concreteSolutionDescriptorService.createConcreteSolutionDescriptor(createMessageOrientedMiddlewareRequest);
        } else {
            // Load ConcreteSolutionDescriptors
            csdME1 = this.concreteSolutionDescriptorService.getConcreteSolutionDescriptor(UUID.fromString("74e7b6e5-2b69-46db-9bea-24cbd403e69e"));
            csdME2 = this.concreteSolutionDescriptorService.getConcreteSolutionDescriptor(UUID.fromString("9f8b4f17-3df8-4f0e-9338-34dab7bd9f25"));
            csdP2PChannel =
                this.concreteSolutionDescriptorService.getConcreteSolutionDescriptor(UUID.fromString("b2f718b9-463f-4038-aade-745f1482de74"));
            csdMessageFilter =
                this.concreteSolutionDescriptorService.getConcreteSolutionDescriptor(UUID.fromString("670056ae-93a2-408b-bdf9-3e25333df810"));
            csdMessageOrientedMiddleware =
                this.concreteSolutionDescriptorService.getConcreteSolutionDescriptor(UUID.fromString("4b237cf0-ef58-444e-8f4b-649365e11acb"));
        }

        // Setup SolutionModel
        SolutionModel solutionModel = null;
        if (false) {
            // create initial solution model
            // create a solution model with a MessageEndpoint, connected via a directed edge of type sendTo to a Point-to-Point Channel,
            // which is connected via a directed edge of type receiveFrom to a MessageFilter,
            // which is connected via a directed edge of type sendTo to a Point-to-Point Channel,
            // which is connected via a directed edge of type receiveFrom to another MessageEndpoint
            solutionModel = new SolutionModel();
            solutionModel.setName("EIP Cloud Aggregation Test");
            solutionModel = this.solutionModelService.createSolutionModel(solutionModel);

            // add concrete solution instances
            var csdMEProducerInstance = this.solutionModelService.addConcreteSolutionInstance(solutionModel.getId(), csdME1.getId());
            var csdP2PChannel1Instance = this.solutionModelService.addConcreteSolutionInstance(solutionModel.getId(), csdP2PChannel.getId());
            var csdMessageFilterInstance = this.solutionModelService.addConcreteSolutionInstance(solutionModel.getId(), csdMessageFilter.getId());
            var csdP2PChannel2Instance = this.solutionModelService.addConcreteSolutionInstance(solutionModel.getId(), csdP2PChannel.getId());
            var csdMEConsumerInstance = this.solutionModelService.addConcreteSolutionInstance(solutionModel.getId(), csdME2.getId());

            // add edges
            var addMEProducerP2PChannelEdgeRequest = getAddEdgeRequestForMEProducerAndP2PChannel1(csdMEProducerInstance, csdP2PChannel1Instance);
            this.solutionModelService.createSolutionModelEdge(solutionModel.getId(), addMEProducerP2PChannelEdgeRequest);

            var addP2PChannel1MessageFilterEdgeRequest =
                getAddEdgeRequestForP2PChannel1AndMessageFilter(csdP2PChannel1Instance, csdMessageFilterInstance);
            this.solutionModelService.createSolutionModelEdge(solutionModel.getId(), addP2PChannel1MessageFilterEdgeRequest);

            var addMessageFilterP2PChannel2EdgeRequest =
                getAddEdgeRequestForMessageFilterAndP2PChannel2(csdMessageFilterInstance, csdP2PChannel2Instance);
            this.solutionModelService.createSolutionModelEdge(solutionModel.getId(), addMessageFilterP2PChannel2EdgeRequest);

            var addP2PChannel2MEConsumerEdgeRequest = getAddEdgeRequestForP2PChannel2AndMessageFilter(csdP2PChannel2Instance, csdMEConsumerInstance);
            this.solutionModelService.createSolutionModelEdge(solutionModel.getId(), addP2PChannel2MEConsumerEdgeRequest);
        } else {
            // load existing solution model
            solutionModel = this.solutionModelService.getSolutionModel(UUID.fromString("aab31277-bc27-48c1-a7fa-2667f81ef448"));
        }
        assertEquals(5, solutionModel.getConcreteSolutionInstances().size());
        assertEquals(4, solutionModel.getEdges().size());

        var aggregationContexts = this.aggregationContextFinder.findAggregationContexts(solutionModel.getId());
        assertEquals(1, aggregationContexts.size());

        var newSolutionModel = this.aggregationEngine.aggregate(solutionModel.getId(), aggregationContexts.get(0));
        newSolutionModel = this.solutionModelService.getSolutionModel(newSolutionModel.getId());
        assertEquals(3, newSolutionModel.getConcreteSolutionInstances().size());
        assertEquals(2, newSolutionModel.getEdges().size());

        var newAggregationContexts = this.aggregationContextFinder.findAggregationContexts(newSolutionModel.getId());
        assertEquals(1, newAggregationContexts.size());

        var finalSolutionModel = this.aggregationEngine.aggregate(newSolutionModel.getId(), newAggregationContexts.get(0));
        finalSolutionModel = this.solutionModelService.getSolutionModel(finalSolutionModel.getId());
        assertEquals(1, finalSolutionModel.getConcreteSolutionInstances().size());
        assertEquals(0, finalSolutionModel.getEdges().size());

        // Next steps:
        // 3. Add Controller to get AggregationContexts and to trigger aggregations
    }

}
