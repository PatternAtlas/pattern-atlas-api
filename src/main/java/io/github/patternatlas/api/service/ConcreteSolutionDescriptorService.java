package io.github.patternatlas.api.service;

import java.util.List;

import io.github.patternatlas.api.entities.solutionaggregation.ConcreteSolutionDescriptor;
import io.github.patternatlas.api.rest.dto.CreateConcreteSolutionDescriptorRequest;
import io.github.patternatlas.api.rest.dto.UpdateConcreteSolutionDescriptorRequest;

public interface ConcreteSolutionDescriptorService {

    List<ConcreteSolutionDescriptor> getAllConcreteSolutionDescriptors();

    ConcreteSolutionDescriptor getConcreteSolutionDescriptor(java.util.UUID id);

    ConcreteSolutionDescriptor createConcreteSolutionDescriptor(CreateConcreteSolutionDescriptorRequest request);

    ConcreteSolutionDescriptor updateConcreteSolutionDescriptor(java.util.UUID id, UpdateConcreteSolutionDescriptorRequest request);

    void deleteConcreteSolutionDescriptor(java.util.UUID id);

}
