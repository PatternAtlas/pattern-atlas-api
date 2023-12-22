package io.github.patternatlas.api.service;

import java.util.List;
import java.util.UUID;

import io.github.patternatlas.api.entities.solutionmodel.SolutionModel;
import io.github.patternatlas.api.entities.solutionmodel.SolutionModelConcreteSolutionInstance;
import io.github.patternatlas.api.entities.solutionmodel.SolutionModelEdge;
import io.github.patternatlas.api.rest.model.AddEdgeToSolutionModelRequest;
import io.github.patternatlas.api.rest.model.UpdateSolutionModelEdgeRequest;

public interface SolutionModelService {

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
