package io.github.patternatlas.api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import io.github.patternatlas.api.entities.Pattern;
import io.github.patternatlas.api.entities.solutionaggregation.ConcreteSolutionDescriptor;
import io.github.patternatlas.api.repositories.ConcreteSolutionDescriptorRepository;
import io.github.patternatlas.api.repositories.PatternRepository;
import io.github.patternatlas.api.rest.dto.CreateConcreteSolutionDescriptorRequest;
import io.github.patternatlas.api.rest.dto.UpdateConcreteSolutionDescriptorRequest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ConcreteSolutionDescriptorServiceImpl implements ConcreteSolutionDescriptorService {

    ConcreteSolutionDescriptorRepository concreteSolutionDescriptorRepository;
    PatternRepository patternRepository;

    @Override
    public List<ConcreteSolutionDescriptor> getAllConcreteSolutionDescriptors() {
        return this.concreteSolutionDescriptorRepository.findAll();
    }

    @Override
    public List<ConcreteSolutionDescriptor> getAllConcreteSolutionDescriptorsByPatternId(UUID patternId) {
        return null;
    }

    @Override
    public ConcreteSolutionDescriptor getConcreteSolutionDescriptor(java.util.UUID id) {
        return this.concreteSolutionDescriptorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Concrete Solution Descriptor not found!"));
    }

    @Override
    public ConcreteSolutionDescriptor createConcreteSolutionDescriptor(CreateConcreteSolutionDescriptorRequest request) {

        ConcreteSolutionDescriptor concreteSolutionDescriptor = new ConcreteSolutionDescriptor();
        concreteSolutionDescriptor.setPatternId(request.getPatternId());
        concreteSolutionDescriptor.setName(request.getName());
        concreteSolutionDescriptor.setTemplateUri(request.getTemplateUri());
        concreteSolutionDescriptor.setTemplate(request.getTemplate());
        concreteSolutionDescriptor.setTemplateMimeType(request.getTemplateMimeType());
        concreteSolutionDescriptor.setType(request.getType());
        concreteSolutionDescriptor.setProperties(request.getProperties());
        concreteSolutionDescriptor.setSelectionCriteria(request.getSelectionCriteria());
        concreteSolutionDescriptor.setRequirements(request.getRequirements());
        concreteSolutionDescriptor.setCapabilities(request.getCapabilities());

        return this.concreteSolutionDescriptorRepository.save(concreteSolutionDescriptor);
    }

    @Override
    public ConcreteSolutionDescriptor updateConcreteSolutionDescriptor(UUID id, UpdateConcreteSolutionDescriptorRequest request) {
        request.setId(id);
        ConcreteSolutionDescriptor concreteSolutionDescriptor = this.concreteSolutionDescriptorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Concrete Solution Descriptor not found!"));

        concreteSolutionDescriptor.setPatternId(request.getPatternId());
        concreteSolutionDescriptor.setName(request.getName());
        concreteSolutionDescriptor.setTemplateUri(request.getTemplateUri());
        concreteSolutionDescriptor.setTemplate(request.getTemplate());
        concreteSolutionDescriptor.setType(request.getType());
        concreteSolutionDescriptor.setProperties(request.getProperties());
        concreteSolutionDescriptor.setSelectionCriteria(request.getSelectionCriteria());
        concreteSolutionDescriptor.setRequirements(request.getRequirements());
        concreteSolutionDescriptor.setCapabilities(request.getCapabilities());

        return this.concreteSolutionDescriptorRepository.save(concreteSolutionDescriptor);
    }

    @Override
    public void deleteConcreteSolutionDescriptor(java.util.UUID id) {
        this.concreteSolutionDescriptorRepository.deleteById(id);
    }

}
