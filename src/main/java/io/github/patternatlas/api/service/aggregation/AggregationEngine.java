package io.github.patternatlas.api.service.aggregation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.github.patternatlas.api.entities.solutionaggregation.ConcreteSolutionDescriptor;
import io.github.patternatlas.api.entities.solutionaggregation.SolutionModel;
import io.github.patternatlas.api.repositories.SolutionModelConcreteSolutionInstanceRepository;
import io.github.patternatlas.api.rest.dto.AddEdgeToSolutionModelRequest;
import io.github.patternatlas.api.rest.dto.AggregationContext;
import io.github.patternatlas.api.service.SolutionModelService;
import io.github.patternatlas.api.service.aggregation.programs.AggregationProgram;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AggregationEngine {

    private SolutionModelService solutionModelService;

    private List<AggregationProgram> aggregationPrograms;

    private SolutionModelConcreteSolutionInstanceRepository solutionModelConcreteSolutionInstanceRepository;

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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SolutionModel aggregate(UUID oldSolutionModelId, AggregationContext aggregationContext) {
            var aggregationProgram = this.aggregationPrograms.stream().filter(p -> p.getId().equals(aggregationContext.getAggregationProgramName()))
                .findFirst().orElseThrow( () -> new RuntimeException("Aggregation program not found!"));
            if (!aggregationProgram.canAggregate(aggregationContext)) {
                throw new IllegalStateException("Aggregation program cannot aggregate given aggregation context!");
            }
            var newCSD = aggregationProgram.aggregate(aggregationContext);
            var newSolutionModel = aggregationProgram.updateSolutionModel(this.solutionModelService.getSolutionModel(oldSolutionModelId), aggregationContext, newCSD);
            return this.solutionModelService.getSolutionModel(newSolutionModel.getId());
    }
}
