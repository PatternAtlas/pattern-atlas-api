package io.github.patternatlas.api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import io.github.patternatlas.api.entities.solutionaggregation.SolutionModel;
import io.github.patternatlas.api.entities.solutionaggregation.SolutionModelConcreteSolutionInstance;
import io.github.patternatlas.api.entities.solutionaggregation.SolutionModelEdge;
import io.github.patternatlas.api.rest.dto.AddEdgeToSolutionModelRequest;
import io.github.patternatlas.api.rest.dto.UpdateSolutionModelEdgeRequest;

public interface SolutionModelService {

    @Transactional
    List<SolutionModel> getAllSolutionModels();

    SolutionModel getSolutionModelById(UUID id);

    SolutionModel createSolutionModel(SolutionModel solutionModel);

    SolutionModel updateSolutionModel(UUID id, SolutionModel solutionModel);

    void deleteSolutionModel(UUID id);

    SolutionModel getSolutionModel(UUID id);

    SolutionModelConcreteSolutionInstance addConcreteSolutionInstance(UUID solutionModelId,
                                                                      UUID concreteSolutionDescriptorId);

    SolutionModelEdge createSolutionModelEdge(UUID solutionModelId, AddEdgeToSolutionModelRequest solutionModelEdgeRequest);

    SolutionModelEdge updateSolutionModelEdge(UUID solutionModelId, UUID edgeId, UpdateSolutionModelEdgeRequest solutionModelEdgeRequest);

    void deleteSolutionModelEdge(UUID solutionModelId, UUID edgeId);

    void deleteConcreteSolutionInstance(UUID solutionModelId, UUID concreteSolutionInstanceId);
}
