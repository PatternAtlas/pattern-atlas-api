package io.github.patternatlas.api.rest.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.patternatlas.api.entities.solutionaggregation.SolutionModel;
import io.github.patternatlas.api.entities.solutionaggregation.SolutionModelConcreteSolutionInstance;
import io.github.patternatlas.api.entities.solutionaggregation.SolutionModelEdge;
import io.github.patternatlas.api.rest.dto.AddEdgeToSolutionModelRequest;
import io.github.patternatlas.api.rest.dto.SolutionModelDto;
import io.github.patternatlas.api.rest.dto.UUIDDTO;
import io.github.patternatlas.api.rest.dto.UpdateSolutionModelEdgeRequest;
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

    @Transactional
    @GetMapping
    public List<SolutionModelDto> getSolutionModels() {
        return this.solutionModelService.getAllSolutionModels().stream()
            .map(sm ->
                new SolutionModelDto(
                    sm.getId(),
                    sm.getName(),
                    sm.getConcreteSolutionInstances().stream()
                        .map(csi -> new SolutionModelDto.SolutionModelConcreteSolutionInstanceDto(
                            csi.getId(),
                            csi.getConcreteSolutionDescriptor().getId(),
                            csi.getProperties()
                        ))
                        .toList(),
                    sm.getEdges().stream()
                        .map(e -> new SolutionModelDto.SolutionModelEdgeDto(
                            e.getId(),
                            e.getDirected(),
                            e.getEdgeSemantic(),
                            e.getCsInstance1().getId(),
                            e.getCsInstance2().getId(),
                            e.getDescription()
                        ))
                        .toList(),
                    sm.getDesignModel() == null ? null : sm.getDesignModel().getId(),
                    sm.getDesignModel() == null ? null : sm.getDesignModel().getUri(),
                    sm.getDesignModel() == null ? null : sm.getDesignModel().getName(),
                    sm.getPreviousSolutionModelId()
                )
            )
            .toList();
    }

    @GetMapping("/{id}")
    public SolutionModelDto getSolutionModel(@PathVariable UUID id) {
        var solutionModel = this.solutionModelService.getSolutionModel(id);
        return new SolutionModelDto(
            solutionModel.getId(),
            solutionModel.getName(),
            solutionModel.getConcreteSolutionInstances().stream()
                .map(csi -> new SolutionModelDto.SolutionModelConcreteSolutionInstanceDto(
                    csi.getId(),
                    csi.getConcreteSolutionDescriptor().getId(),
                    csi.getProperties()
                ))
                .toList(),
            solutionModel.getEdges().stream()
                .map(e -> new SolutionModelDto.SolutionModelEdgeDto(
                    e.getId(),
                    e.getDirected(),
                    e.getEdgeSemantic(),
                    e.getCsInstance1().getId(),
                    e.getCsInstance2().getId(),
                    e.getDescription()
                ))
                .toList(),
            solutionModel.getDesignModel().getId(),
            solutionModel.getDesignModel().getUri(),
            solutionModel.getDesignModel().getName(),
            solutionModel.getPreviousSolutionModelId());
    }

    @PostMapping
    public SolutionModelDto createSolutionModel(@RequestBody String name) {
        SolutionModel solutionModel = new SolutionModel();
        solutionModel.setName(name);
        solutionModel = this.solutionModelService.createSolutionModel(solutionModel);
        return new SolutionModelDto(
            solutionModel.getId(),
            solutionModel.getName(),
            solutionModel.getConcreteSolutionInstances().stream()
                .map(csi -> new SolutionModelDto.SolutionModelConcreteSolutionInstanceDto(
                    csi.getId(),
                    csi.getConcreteSolutionDescriptor().getId(),
                    csi.getProperties()
                ))
                .toList(),
            solutionModel.getEdges().stream()
                .map(e -> new SolutionModelDto.SolutionModelEdgeDto(
                    e.getId(),
                    e.getDirected(),
                    e.getEdgeSemantic(),
                    e.getCsInstance1().getId(),
                    e.getCsInstance2().getId(),
                    e.getDescription()
                ))
                .toList(),
            solutionModel.getDesignModel().getId(),
            solutionModel.getDesignModel().getUri(),
            solutionModel.getDesignModel().getName(),
            solutionModel.getPreviousSolutionModelId());
    }

    @PutMapping("/{id}")
    public SolutionModelDto updateSolutionModel(@PathVariable UUID id, @RequestBody SolutionModel solutionModel) {
        solutionModel = this.solutionModelService.updateSolutionModel(id, solutionModel);
        return new SolutionModelDto(
            solutionModel.getId(),
            solutionModel.getName(),
            solutionModel.getConcreteSolutionInstances().stream()
                .map(csi -> new SolutionModelDto.SolutionModelConcreteSolutionInstanceDto(
                    csi.getId(),
                    csi.getConcreteSolutionDescriptor().getId(),
                    csi.getProperties()
                ))
                .toList(),
            solutionModel.getEdges().stream()
                .map(e -> new SolutionModelDto.SolutionModelEdgeDto(
                    e.getId(),
                    e.getDirected(),
                    e.getEdgeSemantic(),
                    e.getCsInstance1().getId(),
                    e.getCsInstance2().getId(),
                    e.getDescription()
                ))
                .toList(),
            solutionModel.getDesignModel().getId(),
            solutionModel.getDesignModel().getUri(),
            solutionModel.getDesignModel().getName(),
            solutionModel.getPreviousSolutionModelId());
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
    public SolutionModelEdge createSolutionModelEdge(@PathVariable UUID solutionModelId,
                                                     @RequestBody AddEdgeToSolutionModelRequest solutionModelEdgeRequest) {
        return this.solutionModelService.createSolutionModelEdge(solutionModelId, solutionModelEdgeRequest);
    }

    @PutMapping("/{solutionModelId}/edges/{edgeId}")
    public SolutionModelEdge updateSolutionModelEdge(@PathVariable UUID solutionModelId, @PathVariable UUID edgeId,
                                                     @RequestBody UpdateSolutionModelEdgeRequest solutionModelEdgeRequest) {
        return this.solutionModelService.updateSolutionModelEdge(solutionModelId, edgeId, solutionModelEdgeRequest);
    }

    @DeleteMapping("/{solutionModelId}/edges/{edgeId}")
    public void deleteSolutionModelEdge(@PathVariable UUID solutionModelId, @PathVariable UUID edgeId) {
        this.solutionModelService.deleteSolutionModelEdge(solutionModelId, edgeId);
    }
}
