package io.github.patternatlas.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.patternatlas.api.entities.solutionaggregation.ConcreteSolutionDescriptor;

public interface ConcreteSolutionDescriptorRepository extends JpaRepository<ConcreteSolutionDescriptor, java.util.UUID> {
}
