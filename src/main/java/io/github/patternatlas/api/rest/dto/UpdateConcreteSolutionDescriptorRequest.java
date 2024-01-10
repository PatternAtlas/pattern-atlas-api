package io.github.patternatlas.api.rest.dto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UpdateConcreteSolutionDescriptorRequest {
    private UUID id;

    private String name;

    private String templateUri;

    private String template;

    private Map<String, Object> properties;

    private Map<String, Object> selectionCriteria;

    private Set<String> requirements;

    private Set<String> capabilities;

    private UUID patternId;

    private String type;
}
