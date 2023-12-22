package io.github.patternatlas.api.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.patternatlas.api.entities.solutionmodel.SolutionModelEdge;

public interface SolutionModelEdgeRepository extends JpaRepository<SolutionModelEdge, UUID> {
}
