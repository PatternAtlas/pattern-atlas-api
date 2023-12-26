package io.github.patternatlas.api.service;

import java.util.UUID;

import io.github.patternatlas.api.entities.PatternSchema;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PatternSchemaService {

    PatternSchema createPatternSchema(PatternSchema patternSchema);

    PatternSchema updatePatternSchema(PatternSchema patternSchema);

    PatternSchema getPatternSchemaById(UUID id);

    void deletePatternSchema(PatternSchema schema);
}
