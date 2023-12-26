package io.github.patternatlas.api.service;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import io.github.patternatlas.api.entities.solutionaggregation.SolutionModel;
import io.github.patternatlas.api.entities.solutionaggregation.SolutionModelConcreteSolutionInstance;
import io.github.patternatlas.api.entities.solutionaggregation.SolutionModelEdge;
import io.github.patternatlas.api.entities.solutionaggregation.ConcreteSolutionDescriptor;
import io.github.patternatlas.api.repositories.ConcreteSolutionDescriptorRepository;
import io.github.patternatlas.api.repositories.SolutionModelConcreteSolutionInstanceRepository;
import io.github.patternatlas.api.repositories.SolutionModelEdgeRepository;
import io.github.patternatlas.api.repositories.SolutionModelRepository;
import io.github.patternatlas.api.rest.dto.AddEdgeToSolutionModelRequest;
import io.github.patternatlas.api.rest.dto.UpdateSolutionModelEdgeRequest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class SolutionModelServiceImpl implements SolutionModelService {

    SolutionModelRepository solutionModelRepository;

    SolutionModelConcreteSolutionInstanceRepository solutionModelConcreteSolutionInstanceRepository;

    ConcreteSolutionDescriptorRepository concreteSolutionDescriptorRepository;

    SolutionModelEdgeRepository solutionModelEdgeRepository;

    @Override
    public List<SolutionModel> getAllSolutionModels() {
        return solutionModelRepository.findAll();
    }

    @Override
    public SolutionModel getSolutionModelById(UUID id) {
        return solutionModelRepository.getById(id);
    }

    @Override
    public SolutionModel createSolutionModel(SolutionModel solutionModel) {
        return solutionModelRepository.save(solutionModel);
    }

    @Override
    public SolutionModel updateSolutionModel(UUID id, SolutionModel solutionModel) {
        solutionModel.setId(id);
        return solutionModelRepository.save(solutionModel);
    }

    @Override
    public void deleteSolutionModel(UUID id) {
        solutionModelRepository.deleteById(id);
    }

    @Override
    public SolutionModel getSolutionModel(UUID id) {
        return solutionModelRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Solution Model not found!"));
    }

    @Override
    public SolutionModelConcreteSolutionInstance addConcreteSolutionInstance(UUID solutionModelId,
                                                                             UUID concreteSolutionDescriptorId) {
        SolutionModel solutionModel =
            this.solutionModelRepository.findById(solutionModelId).orElseThrow(() -> new EntityNotFoundException("Solution Model not found!"));

        ConcreteSolutionDescriptor concreteSolutionDescriptor =
            this.concreteSolutionDescriptorRepository.findById(concreteSolutionDescriptorId)
                .orElseThrow(() -> new EntityNotFoundException("Concrete Solution Descriptor not found!"));

        SolutionModelConcreteSolutionInstance solutionModelConcreteSolutionInstance = new SolutionModelConcreteSolutionInstance();
        solutionModelConcreteSolutionInstance.setConcreteSolutionDescriptor(concreteSolutionDescriptor);
        solutionModelConcreteSolutionInstance.setSolutionModel(solutionModel);

        return this.solutionModelConcreteSolutionInstanceRepository.save(solutionModelConcreteSolutionInstance);
    }

    @Override
    public SolutionModelEdge createSolutionModelEdge(UUID solutionModelId, AddEdgeToSolutionModelRequest solutionModelEdgeRequest) {
        SolutionModel solutionModel =
            this.solutionModelRepository.findById(solutionModelId).orElseThrow(() -> new EntityNotFoundException("Solution Model not found!"));

        SolutionModelConcreteSolutionInstance sourceConcreteSolutionInstance1 =
            this.solutionModelConcreteSolutionInstanceRepository.findById(solutionModelEdgeRequest.getConcreteSolutionInstanceId1())
                .orElseThrow(() -> new EntityNotFoundException("Source Concrete Solution Instance not found!"));

        SolutionModelConcreteSolutionInstance sourceConcreteSolutionInstance2 =
            this.solutionModelConcreteSolutionInstanceRepository.findById(solutionModelEdgeRequest.getConcreteSolutionInstanceId2())
                .orElseThrow(() -> new EntityNotFoundException("Source Concrete Solution Instance not found!"));

        SolutionModelEdge solutionModelEdge = new SolutionModelEdge();
        solutionModelEdge.setSolutionModel(solutionModel);
        solutionModelEdge.setCsInstance1(sourceConcreteSolutionInstance1);
        solutionModelEdge.setCsInstance2(sourceConcreteSolutionInstance2);
        solutionModelEdge.setDirected(solutionModelEdgeRequest.isDirected());
        solutionModelEdge.setEdgeSemantic(solutionModelEdgeRequest.getEdgeSemantic());
        solutionModelEdge.setDescription(solutionModelEdgeRequest.getDescription());

        return this.solutionModelEdgeRepository.save(solutionModelEdge);
    }

    @Override
    public SolutionModelEdge updateSolutionModelEdge(UUID solutionModelId, UUID edgeId, UpdateSolutionModelEdgeRequest solutionModelEdgeRequest) {
        return this.solutionModelEdgeRepository.findById(edgeId)
            .map(solutionModelEdge -> {
                if (!solutionModelEdge.getId().equals(solutionModelId)) {
                    throw new EntityNotFoundException("Solution Model Edge not found!");
                }
                solutionModelEdge.setDirected(solutionModelEdgeRequest.isDirected());
                solutionModelEdge.setEdgeSemantic(solutionModelEdgeRequest.getEdgeSemanticType());
                solutionModelEdge.setDescription(solutionModelEdgeRequest.getDescription());
                return this.solutionModelEdgeRepository.save(solutionModelEdge);
            }).orElseThrow(() -> new EntityNotFoundException("Solution Model Edge not found!"));
    }

    @Override
    public void deleteSolutionModelEdge(UUID solutionModelId, UUID edgeId) {
        SolutionModelEdge solutionModelEdge = this.solutionModelEdgeRepository.findById(edgeId)
            .orElseThrow(() -> new EntityNotFoundException("Solution Model Edge not found!"));
        if (!solutionModelEdge.getSolutionModel().getId().equals(solutionModelId)) {
            throw new EntityNotFoundException("Solution Model Edge not found!");
        }
        this.solutionModelEdgeRepository.deleteById(edgeId);
    }

    @Override
    public void deleteConcreteSolutionInstance(UUID solutionModelId, UUID concreteSolutionInstanceId) {
        SolutionModelConcreteSolutionInstance solutionModelConcreteSolutionInstance =
            this.solutionModelConcreteSolutionInstanceRepository.findById(concreteSolutionInstanceId)
                .orElseThrow(() -> new EntityNotFoundException("Concrete Solution Instance not found!"));
        if (!solutionModelConcreteSolutionInstance.getSolutionModel().getId().equals(solutionModelId)) {
            throw new EntityNotFoundException("Concrete Solution Instance not found!");
        }
        this.solutionModelConcreteSolutionInstanceRepository.deleteById(concreteSolutionInstanceId);
    }

}
