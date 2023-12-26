package io.github.patternatlas.api.rest.dto.shared;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class RatingModelMultiRequest extends RatingModelRequest {

    private RatingType ratingType;
}
