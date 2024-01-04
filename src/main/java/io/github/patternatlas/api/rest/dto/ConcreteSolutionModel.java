package io.github.patternatlas.api.rest.dto;

import java.io.Serializable;
import java.util.UUID;

import io.github.patternatlas.api.entities.designmodel.ConcreteSolution;
import lombok.Value;

/**
 * DTO for {@link io.github.patternatlas.api.entities.designmodel.ConcreteSolution}
 */
@Value
public class ConcreteSolutionModel implements Serializable {
    UUID id;

    String patternUri;

    String name;

    String templateUri;

    String aggregatorType;

    public static ConcreteSolutionModel from(ConcreteSolution concreteSolution) {
        return new ConcreteSolutionModel(
            concreteSolution.getId(),
            concreteSolution.getPatternUri(),
            concreteSolution.getName(),
            concreteSolution.getTemplateUri(),
            concreteSolution.getAggregatorType()
        );
    }
}
