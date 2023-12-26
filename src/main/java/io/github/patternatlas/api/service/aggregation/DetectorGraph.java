package io.github.patternatlas.api.service.aggregation;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetectorGraph {
    List<OperationTarget> operationTargets = new ArrayList<>();
    List<DetectorGraphEdge> edges = new ArrayList<>();
}
