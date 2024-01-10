package io.github.patternatlas.api.service.aggregation.programs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismInspector;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.springframework.stereotype.Component;
import org.stringtemplate.v4.ST;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.patternatlas.api.entities.solutionaggregation.ConcreteSolutionDescriptor;
import io.github.patternatlas.api.entities.solutionaggregation.SolutionModel;
import io.github.patternatlas.api.entities.solutionaggregation.SolutionModelConcreteSolutionInstance;
import io.github.patternatlas.api.repositories.SolutionModelConcreteSolutionInstanceRepository;
import io.github.patternatlas.api.rest.dto.AddEdgeToSolutionModelRequest;
import io.github.patternatlas.api.rest.dto.AggregationContext;
import io.github.patternatlas.api.rest.dto.CreateConcreteSolutionDescriptorRequest;
import io.github.patternatlas.api.service.ConcreteSolutionDescriptorService;
import io.github.patternatlas.api.service.SolutionModelService;
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
public class PipesAndFilterAggregator implements AggregationProgram {

    private SolutionModelService solutionModelService;

    private SolutionModelConcreteSolutionInstanceRepository solutionModelConcreteSolutionInstanceRepository;

    private ConcreteSolutionDescriptorService concreteSolutionDescriptorService;

    private final DetectorGraph detectorGraph;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PipesAndFilterAggregator(SolutionModelService solutionModelService, ConcreteSolutionDescriptorService concreteSolutionDescriptorService,
                                    SolutionModelConcreteSolutionInstanceRepository solutionModelConcreteSolutionInstanceRepository) {
        this.solutionModelService = solutionModelService;
        this.concreteSolutionDescriptorService = concreteSolutionDescriptorService;
        this.solutionModelConcreteSolutionInstanceRepository = solutionModelConcreteSolutionInstanceRepository;
        this.detectorGraph = this.setupDetectorGraph();
    }

    public String getId() {
        return this.getClass().getName();
    }

    private DetectorGraph setupDetectorGraph() {
        var graph = new DetectorGraph();
        Set<String> requirements1 = new HashSet<>(List.of("OutputChannel"));
        Set<String> capabilities1 = new HashSet<>(List.of("MessageProducer"));
        var ot1 = OperationTarget.builder()
            .type("PipesAndFilterAggregate")
            .requirements(requirements1)
            .capabilities(capabilities1)
            .id(UUID.fromString("de520326-6356-4a62-b57e-359bfd986836"))
            .build();
        graph.getOperationTargets().add(ot1);

        Set<String> requirements2 = new HashSet<>(List.of("MessageProducer", "MessageConsumer"));
        Set<String> capabilities2 = new HashSet<>(List.of("InputChannel", "OutputChannel"));
        var ot2 = OperationTarget.builder()
            .type("ActiveMQ XML Route")
            .requirements(requirements2)
            .capabilities(capabilities2)
            .id(UUID.fromString("1ca8024a-f19a-4994-b195-7d96105238cc"))
            .build();
        graph.getOperationTargets().add(ot2);

        Set<String> requirements3 = Set.of("InputChannel");
        Set<String> capabilities3 = Set.of("MessageConsumer");
        var ot3 = OperationTarget.builder()
            .type("Java String Template")
            .requirements(requirements3)
            .capabilities(capabilities3)
            .id(UUID.fromString("4684a64d-cc7f-48cd-b77b-f6457a9cbff6"))
            .build();
        graph.getOperationTargets().add(ot3);

        graph.getEdges().add(DetectorGraphEdge.builder()
            .ot1(ot1)
            .ot2(ot2)
            .directed(true)
            .edgeSemantic("sendTo")
            .build());
        graph.getEdges().add(DetectorGraphEdge.builder()
            .ot1(ot3)
            .ot2(ot2)
            .directed(true)
            .edgeSemantic("receiveFrom")
            .build());
        return graph;
    }

    private static Graph<PseydoNode, PseydoEdge> buildEmptyGraph() {
        return GraphTypeBuilder
            .<PseydoNode, PseydoEdge>directed().allowingMultipleEdges(false)
            .allowingSelfLoops(false).edgeClass(PseydoEdge.class).weighted(true).buildGraph();
    }

    @Override
    public List<AggregationContext> findAggregationContexts(SolutionModel solutionModel) {
        this.setupDetectorGraph();
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
            aggregationContext.setSolutionModelId(solutionModel.getId());
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
        var solutionModel = this.solutionModelService.getSolutionModel(aggregationContext.getSolutionModelId());
        // 1. get template of MessageProducer concrete solution
        // 2. get template of ActiveMQ XML Route concrete solution
        // 3. get template of MessageConsumer concrete solution
        SolutionModelConcreteSolutionInstance messageProducerCSI = null;
        SolutionModelConcreteSolutionInstance p2pChannelCSI = null;
        SolutionModelConcreteSolutionInstance messageConsumerCSI = null;
        for (var edgeId : aggregationContext.getSolutionModelEdgeIds()) {
            var solutionModelEdge = solutionModel.getEdges().stream().filter(e -> e.getId().equals(edgeId)).findFirst().get();
            if (solutionModelEdge.getEdgeSemantic().equals("sendTo")) {
                messageProducerCSI = solutionModelEdge.getCsInstance1();
                p2pChannelCSI = solutionModelEdge.getCsInstance2();
            } else if (solutionModelEdge.getEdgeSemantic().equals("receiveFrom")) {
                messageConsumerCSI = solutionModelEdge.getCsInstance1();
            }
        }
        if (messageProducerCSI == null || p2pChannelCSI == null || messageConsumerCSI == null) {
            throw new IllegalStateException("Aggregation context is not valid!");
        }

        var messageProducerConfigHook = (String) messageProducerCSI.getConcreteSolutionDescriptor().getProperties().get("configHook");
        var messageProducerFileName = (String) messageProducerCSI.getConcreteSolutionDescriptor().getProperties().get("filterFile");
        var p2pChannelConsumerConfigHook = (String) p2pChannelCSI.getConcreteSolutionDescriptor().getProperties().get("consumerConfigHook");
        var p2pChannelProducerConfigHook = (String) p2pChannelCSI.getConcreteSolutionDescriptor().getProperties().get("producerConfigHook");
        var messageConsumerConfigHook = (String) messageConsumerCSI.getConcreteSolutionDescriptor().getProperties().get("configHook");

        if (null == messageProducerConfigHook || null == p2pChannelConsumerConfigHook || null == p2pChannelProducerConfigHook ||
            null == messageConsumerConfigHook || null == messageProducerFileName) {
            throw new IllegalStateException("Required config properties not found!");
        }

        // MessageProducerTemplate has to be loaded from the zip provided by the aggregate
        var messageProducerTemplateFileContent =
            loadFromZip(messageProducerCSI.getConcreteSolutionDescriptor().getTemplate(), messageProducerFileName);
        var messageProducerTemplate = new ST(messageProducerTemplateFileContent, '$', '$');
        messageProducerTemplate.add(messageProducerConfigHook, messageProducerCSI.getId());
        var messageProducerRenderedTemplate = messageProducerTemplate.render();

        var p2pChannelTemplate = new ST(p2pChannelCSI.getConcreteSolutionDescriptor().getTemplate(), '$', '$');
        p2pChannelTemplate.add(p2pChannelProducerConfigHook, messageProducerCSI.getId());
        p2pChannelTemplate.add(p2pChannelConsumerConfigHook, messageConsumerCSI.getId());
        var p2pChannelRenderedTemplate = p2pChannelTemplate.render();

        var messageConsumerTemplate = new ST(messageConsumerCSI.getConcreteSolutionDescriptor().getTemplate(), '$', '$');
        messageConsumerTemplate.add(messageConsumerConfigHook, messageConsumerCSI.getId());
        var messageConsumerRenderedTemplate = messageConsumerTemplate.render();

        // 1. zip rendered templates as files
        // 2. create new concrete solution descriptor and put zip base64 encoded as template and set mime type to application/zip

        var oldP2PChannelFileName = (String) messageProducerCSI.getConcreteSolutionDescriptor().getProperties().get("p2pRouteFile");
        var oldP2PChannelRoute =
            loadFromZip(messageProducerCSI.getConcreteSolutionDescriptor().getTemplate(), oldP2PChannelFileName);

        var queueProducerTemplate = loadFromZip(messageProducerCSI.getConcreteSolutionDescriptor().getTemplate(), "QueueProducer.java");
        var newCsdTemplate = getZipAsBase64String(messageProducerRenderedTemplate, p2pChannelRenderedTemplate, oldP2PChannelRoute, messageConsumerRenderedTemplate, queueProducerTemplate);

        var newCsdRequest = new CreateConcreteSolutionDescriptorRequest();
        newCsdRequest.setTemplate(newCsdTemplate);
        newCsdRequest.setTemplateMimeType("application/zip");
        newCsdRequest.setRequirements(Set.of("ActiveMQRuntime", "JavaRuntime"));
        newCsdRequest.setName("Final Aggregate");
        newCsdRequest.setType("PipesAndFilterAggregate");
        // save new CSD
        var newCsd = this.concreteSolutionDescriptorService.createConcreteSolutionDescriptor(newCsdRequest);
        return newCsd;
    }

    private String loadFromZip(String base64Zip, String messageProducerFileName) {
        String fileContent = "";
        try {
            // Decode the base64 string into a byte array
            byte[] zipBytes = Base64.getDecoder().decode(base64Zip);

            // Create an input stream from the byte array
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(zipBytes);

            // Create a ZipInputStream to read the zip archive
            ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream);

            // Iterate through the zip entries
            ZipEntry zipEntry;

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().equals(messageProducerFileName)) {
                    // Read the content of file1.txt into a string
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zipInputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, len);
                    }
                    fileContent = byteArrayOutputStream.toString("UTF-8");
                }
                zipInputStream.closeEntry();
            }
            zipInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fileContent.isEmpty()) {
            throw new IllegalStateException("Could not find file in zip!");
        }
        return fileContent;
    }

    private static String getZipAsBase64String(String messageProducerRenderedTemplate,
                                               String p2pChannelRenderedTemplate,
                                               String oldP2PChannelRoute,
                                               String messageConsumerRenderedTemplate,
                                               String queueProducerTemplate) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

            addToZip(zipOutputStream, "QueueProducer.java", queueProducerTemplate);
            addToZip(zipOutputStream, "routes.xml", p2pChannelRenderedTemplate + "\n" + oldP2PChannelRoute + "\n" + messageProducerRenderedTemplate);
            addToZip(zipOutputStream, "QueueConsumer.java", messageConsumerRenderedTemplate);

            zipOutputStream.close();
            byte[] zipBytes = byteArrayOutputStream.toByteArray();

            // Convert the zipBytes to a base64 string
            return java.util.Base64.getEncoder().encodeToString(zipBytes);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void addToZip(ZipOutputStream zipOutputStream, String fileName, String content) throws IOException {
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOutputStream.putNextEntry(zipEntry);
        byte[] data = content.getBytes();
        zipOutputStream.write(data, 0, data.length);
        zipOutputStream.closeEntry();
    }

    @Override
    public SolutionModel updateSolutionModel(SolutionModel oldSolutionModel, AggregationContext aggregationContext,
                                             ConcreteSolutionDescriptor newConcreteSolution) {
        // 0. create new concrete solution model and work on that
        var newSolutionModel = new SolutionModel();
        newSolutionModel.setName(oldSolutionModel.getName() + " - " + newConcreteSolution.getId());
        newSolutionModel.setPreviousSolutionModelId(oldSolutionModel.getId());
        newSolutionModel = this.solutionModelService.createSolutionModel(newSolutionModel);

        // 1. just add concrete solutions not contained in aggregation context to new solution model
        var csiMapping = new HashMap<UUID, UUID>();
        for (var node : oldSolutionModel.getConcreteSolutionInstances()) {
            if (!aggregationContext.getConcreteSolutionInstanceIds().contains(node.getId())) {
                var instance = this.solutionModelService.addConcreteSolutionInstance(newSolutionModel.getId(),
                    node.getConcreteSolutionDescriptor().getId());
                instance.setProperties(node.getProperties());
                csiMapping.put(node.getId(), instance.getId());
                this.solutionModelConcreteSolutionInstanceRepository.save(instance);
            }
        }
        // 2. add new concrete solution to solution model
        var newCsInstance = this.solutionModelService.addConcreteSolutionInstance(newSolutionModel.getId(),
            newConcreteSolution.getId());
        newSolutionModel = this.solutionModelService.getSolutionModel(newSolutionModel.getId());
        // 3. just add edges not contained in aggregation context to new solution model
        for (var edge : oldSolutionModel.getEdges()) {
            if (aggregationContext.getSolutionModelEdgeIds().contains(edge.getId())) {
                // skip edges contained in aggregation context, i.e., don't add them to new solution model
                continue;
            }

            // wenn beide knoten der kante nicht in aggr context sind, dann füge kante hinzu und nutze mapping für csi
            if (!aggregationContext.getConcreteSolutionInstanceIds().contains(edge.getCsInstance1().getId()) &&
                !aggregationContext.getConcreteSolutionInstanceIds().contains(edge.getCsInstance2().getId())) {
                var request = new AddEdgeToSolutionModelRequest();
                request.setDirected(edge.getDirected());
                request.setEdgeSemantic(edge.getEdgeSemantic());
                request.setConcreteSolutionInstanceId1(csiMapping.get(edge.getCsInstance1().getId()));
                request.setConcreteSolutionInstanceId2(csiMapping.get(edge.getCsInstance2().getId()));

                this.solutionModelService.createSolutionModelEdge(newSolutionModel.getId(), request);
                continue;
            }

            // wenn erster knoten nicht in aggr context ist, dann füge kante von erstem knoten zu neuem csi hinzu
            if (!aggregationContext.getConcreteSolutionInstanceIds().contains(edge.getCsInstance1().getId())) {
                var request = new AddEdgeToSolutionModelRequest();
                request.setDirected(edge.getDirected());
                request.setEdgeSemantic(edge.getEdgeSemantic());
                request.setConcreteSolutionInstanceId1(csiMapping.get(edge.getCsInstance1().getId()));
                request.setConcreteSolutionInstanceId2(newCsInstance.getId());

                this.solutionModelService.createSolutionModelEdge(newSolutionModel.getId(), request);
                continue;
            }

            // wenn zweiter knoten nicht in aggr context ist, dann füge kanten von zweitem knoten zu neuem csi hinzu
            if (!aggregationContext.getConcreteSolutionInstanceIds().contains(edge.getCsInstance2().getId())) {
                var request = new AddEdgeToSolutionModelRequest();
                request.setDirected(edge.getDirected());
                request.setEdgeSemantic(edge.getEdgeSemantic());
                request.setConcreteSolutionInstanceId1(newCsInstance.getId());
                request.setConcreteSolutionInstanceId2(csiMapping.get(edge.getCsInstance2().getId()));

                this.solutionModelService.createSolutionModelEdge(newSolutionModel.getId(), request);
            }
        }
        return this.solutionModelService.getSolutionModel(newSolutionModel.getId());
    }
}
