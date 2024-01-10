package io.github.patternatlas.api.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.patternatlas.api.entities.solutionaggregation.ConcreteSolutionDescriptor;

public interface ConcreteSolutionDescriptorRepository extends JpaRepository<ConcreteSolutionDescriptor, UUID> {
    List<ConcreteSolutionDescriptor> findAllByPatternId(UUID patternId);
}
