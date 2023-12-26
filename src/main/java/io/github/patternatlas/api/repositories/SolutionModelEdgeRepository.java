package io.github.patternatlas.api.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.patternatlas.api.entities.solutionaggregation.SolutionModelEdge;

public interface SolutionModelEdgeRepository extends JpaRepository<SolutionModelEdge, UUID> {
}
