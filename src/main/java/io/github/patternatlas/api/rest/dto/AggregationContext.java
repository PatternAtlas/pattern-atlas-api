package io.github.patternatlas.api.rest.dto;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.Data;

@Data
public class AggregationContext {
    String aggregationProgramName;

    Set<UUID> concreteSolutionInstanceIds = new HashSet<>();

    Set<UUID> solutionModelEdgeIds = new HashSet<>();
}
