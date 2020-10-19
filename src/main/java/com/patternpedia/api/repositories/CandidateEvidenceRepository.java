package com.patternpedia.api.repositories;

import com.patternpedia.api.entities.candidate.evidence.CandidateEvidence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CandidateEvidenceRepository extends JpaRepository<CandidateEvidence, UUID> {
}
