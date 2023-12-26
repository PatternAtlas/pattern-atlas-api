package io.github.patternatlas.api.rest.dto.candidate;

import io.github.patternatlas.api.rest.dto.shared.AuthorModel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class CandidateModelRequest extends CandidateModel {

    private UUID issueId;
    private Integer updateRating;
    private List<AuthorModel> updateAuthors;
}
