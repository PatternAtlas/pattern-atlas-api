package io.github.patternatlas.api.rest.model;

import java.net.URL;
import java.util.UUID;

import io.github.patternatlas.api.entities.designmodel.DesignModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DesignModelModel {
    private UUID id;

    private String uri;

    private String name;

    private URL logo;

    public static DesignModelModel toModel(DesignModel designModel) {
        DesignModelModel model = new DesignModelModel();
        model.setId(designModel.getId());
        model.setUri(designModel.getUri());
        model.setName(designModel.getName());
        model.setLogo(designModel.getLogo());
        return model;
    }
}
