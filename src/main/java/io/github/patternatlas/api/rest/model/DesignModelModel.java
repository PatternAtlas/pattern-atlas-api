package io.github.patternatlas.api.rest.model;

import java.io.Serializable;
import java.net.URL;
import java.util.UUID;

import io.github.patternatlas.api.entities.designmodel.DesignModel;
import lombok.Value;

/**
 * DTO for {@link io.github.patternatlas.api.entities.designmodel.DesignModel}
 */
@Value
public class DesignModelModel implements Serializable {
    UUID id;

    String uri;

    String name;

    URL logo;

    public static DesignModelModel from(DesignModel designModel) {
        return new DesignModelModel(designModel.getId(), designModel.getUri(), designModel.getName(), designModel.getLogo());
    }
}
