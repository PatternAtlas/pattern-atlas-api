package io.github.patternatlas.api.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.patternatlas.api.entities.solutionaggregation.SolutionModelConcreteSolutionInstance;

public interface SolutionModelConcreteSolutionInstanceRepository extends JpaRepository<SolutionModelConcreteSolutionInstance, UUID> {
}
