package io.github.patternatlas.api.service.aggregation;

import java.util.Set;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OperationTarget {
    private String type;

    private Set<String> requirements;

    private Set<String> capabilities;

    private UUID id;
}
