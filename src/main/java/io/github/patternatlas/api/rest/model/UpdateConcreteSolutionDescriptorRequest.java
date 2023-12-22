package io.github.patternatlas.api.rest.model;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UpdateConcreteSolutionDescriptorRequest {
    private UUID id;
    private String name;
    private String templateUri;
    private Object properties;
    private Object selectionCriteria;
    private Object requirements;
    private Object capabilities;
    private UUID patternId;
    private String type;
}
