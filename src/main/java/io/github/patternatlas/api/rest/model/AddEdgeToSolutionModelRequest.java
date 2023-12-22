package io.github.patternatlas.api.rest.model;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AddEdgeToSolutionModelRequest {
    private UUID concreteSolutionInstanceId1;
    private UUID concreteSolutionInstanceId2;
    private Object description;
    private boolean directed;
    private String edgeSemantic;
}
