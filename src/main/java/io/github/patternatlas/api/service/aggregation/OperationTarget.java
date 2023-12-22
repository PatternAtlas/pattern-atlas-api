package io.github.patternatlas.api.service.aggregation;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OperationTarget {
    private String type;
    private Object requirements;
    private Object capabilities;
}
