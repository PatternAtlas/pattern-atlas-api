package io.github.patternatlas.api.rest.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;

/**
 * DTO for {@link io.github.patternatlas.api.entities.solutionaggregation.SolutionModel}
 */
@Getter
@Setter
@Value
public class SolutionModelDto implements Serializable {
    UUID id;

    String name;

    List<SolutionModelConcreteSolutionInstanceDto> concreteSolutionInstances;

    List<SolutionModelEdgeDto> edges;

    UUID designModelId;

    String designModelUri;

    String designModelName;

    UUID previousSolutionModelId;

    /**
     * DTO for {@link io.github.patternatlas.api.entities.solutionaggregation.SolutionModelConcreteSolutionInstance}
     */
    @Value
    public static class SolutionModelConcreteSolutionInstanceDto implements Serializable {
        UUID id;

        UUID concreteSolutionDescriptorId;

        Object properties;
    }

    /**
     * DTO for {@link io.github.patternatlas.api.entities.solutionaggregation.SolutionModelEdge}
     */
    @Value
    public static class SolutionModelEdgeDto implements Serializable {
        UUID id;

        Boolean directed;

        String edgeSemantic;

        UUID csInstance1Id;

        UUID csInstance2Id;

        Object description;
    }
}
