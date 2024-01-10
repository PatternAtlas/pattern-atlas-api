package io.github.patternatlas.api.service;

import java.util.List;
import java.util.UUID;

import io.github.patternatlas.api.entities.solutionaggregation.ConcreteSolutionDescriptor;
import io.github.patternatlas.api.rest.dto.CreateConcreteSolutionDescriptorRequest;
import io.github.patternatlas.api.rest.dto.UpdateConcreteSolutionDescriptorRequest;

public interface ConcreteSolutionDescriptorService {

    List<ConcreteSolutionDescriptor> getAllConcreteSolutionDescriptors();

    List<ConcreteSolutionDescriptor> getAllConcreteSolutionDescriptorsByPatternId(UUID patternId);

    ConcreteSolutionDescriptor getConcreteSolutionDescriptor(UUID id);

    ConcreteSolutionDescriptor createConcreteSolutionDescriptor(CreateConcreteSolutionDescriptorRequest request);

    ConcreteSolutionDescriptor updateConcreteSolutionDescriptor(UUID id, UpdateConcreteSolutionDescriptorRequest request);

    void deleteConcreteSolutionDescriptor(UUID id);

}
