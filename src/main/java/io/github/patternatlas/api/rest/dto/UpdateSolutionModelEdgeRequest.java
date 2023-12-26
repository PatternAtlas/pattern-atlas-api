package io.github.patternatlas.api.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UpdateSolutionModelEdgeRequest {
    private Object description;
    private String edgeSemanticType;
    private boolean directed;
}
