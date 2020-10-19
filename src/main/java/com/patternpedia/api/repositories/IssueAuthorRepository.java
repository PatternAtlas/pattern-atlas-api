package com.patternpedia.api.repositories;

import com.patternpedia.api.entities.issue.author.IssueAuthor;
import com.patternpedia.api.entities.shared.CompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueAuthorRepository extends JpaRepository<IssueAuthor, CompositeKey> {

    boolean existsByIdAndRole(CompositeKey compositeKey, String role);

}
