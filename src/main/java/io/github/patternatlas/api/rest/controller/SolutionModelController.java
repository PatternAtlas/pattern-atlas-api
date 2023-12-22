package io.github.patternatlas.api.rest.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.patternatlas.api.entities.solutionmodel.SolutionModel;
import io.github.patternatlas.api.entities.solutionmodel.SolutionModelConcreteSolutionInstance;
import io.github.patternatlas.api.entities.solutionmodel.SolutionModelEdge;
import io.github.patternatlas.api.rest.model.AddEdgeToSolutionModelRequest;
import io.github.patternatlas.api.rest.model.UUIDDTO;
import io.github.patternatlas.api.rest.model.UpdateSolutionModelEdgeRequest;
import io.github.patternatlas.api.service.SolutionModelService;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

@RestController
@CommonsLog
@CrossOrigin(allowedHeaders = "*", origins = "*")
@AllArgsConstructor
@RequestMapping(value = "/solution-models", produces = "application/json")
public class SolutionModelController {

    private final SolutionModelService solutionModelService;

    @GetMapping
    public List<SolutionModel> getSolutionModels() {
        return this.solutionModelService.getAllSolutionModels();
    }

    @GetMapping("/{id}")
    public SolutionModel getSolutionModel(@PathVariable UUID id) {
        return this.solutionModelService.getSolutionModel(id);
    }

    @PostMapping
    public SolutionModel createSolutionModel(@RequestBody String name) {
        SolutionModel solutionModel = new SolutionModel();
        solutionModel.setName(name);
        return this.solutionModelService.createSolutionModel(solutionModel);
    }

    @PutMapping("/{id}")
    public SolutionModel updateSolutionModel(@PathVariable UUID id, @RequestBody SolutionModel solutionModel) {
        return this.solutionModelService.updateSolutionModel(id, solutionModel);
    }

    @DeleteMapping("/{id}")
    public void deleteSolutionModel(@PathVariable UUID id) {
        this.solutionModelService.deleteSolutionModel(id);
    }

    @GetMapping("/{id}/concrete-solution-instances")
    public List<SolutionModelConcreteSolutionInstance> getConcreteSolutionDescriptors(@PathVariable UUID id) {
        SolutionModel solutionModel = this.solutionModelService.getSolutionModel(id);
        return solutionModel.getConcreteSolutionInstances();
    }

    @PostMapping("/{id}/concrete-solution-instances")
    public SolutionModelConcreteSolutionInstance addConcreteSolutionDescriptor(@PathVariable UUID id,
                                                                                  @RequestBody UUIDDTO concreteSolutionDescriptorIdDto) {
        return this.solutionModelService.addConcreteSolutionInstance(id, concreteSolutionDescriptorIdDto.getId());
    }

    @DeleteMapping("/{solutionModelId}/concrete-solution-instances/{concreteSolutionInstanceId}")
    public void deleteConcreteSolutionInstance(@PathVariable UUID solutionModelId, @PathVariable UUID concreteSolutionInstanceId) {
        this.solutionModelService.deleteConcreteSolutionInstance(solutionModelId, concreteSolutionInstanceId);
    }

    @PostMapping("/{solutionModelId}/edges")
    public SolutionModelEdge createSolutionModelEdge(@PathVariable UUID solutionModelId, @RequestBody AddEdgeToSolutionModelRequest solutionModelEdgeRequest) {
        return this.solutionModelService.createSolutionModelEdge(solutionModelId, solutionModelEdgeRequest);
    }

    @PutMapping("/{solutionModelId}/edges/{edgeId}")
    public SolutionModelEdge updateSolutionModelEdge(@PathVariable UUID solutionModelId, @PathVariable UUID edgeId, @RequestBody UpdateSolutionModelEdgeRequest solutionModelEdgeRequest) {
        return this.solutionModelService.updateSolutionModelEdge(solutionModelId, edgeId, solutionModelEdgeRequest);
    }

    @DeleteMapping("/{solutionModelId}/edges/{edgeId}")
    public void deleteSolutionModelEdge(@PathVariable UUID solutionModelId, @PathVariable UUID edgeId) {
        this.solutionModelService.deleteSolutionModelEdge(solutionModelId, edgeId);
    }
}
