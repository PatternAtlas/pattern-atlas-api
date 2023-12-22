package io.github.patternatlas.api.service.aggregation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetectorGraphEdge {
    private OperationTarget ot1;
    private OperationTarget ot2;
    private boolean directed;
    private String edgeSemantic;
}
