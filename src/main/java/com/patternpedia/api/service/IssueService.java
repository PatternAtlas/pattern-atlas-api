package com.patternpedia.api.service;

import java.util.List;

import com.patternpedia.api.entities.issue.IssueRating;
import com.patternpedia.api.entities.issue.author.IssueAuthor;
import com.patternpedia.api.entities.issue.comment.IssueComment;
import com.patternpedia.api.entities.issue.Issue;
import com.patternpedia.api.entities.issue.comment.IssueCommentRating;
import com.patternpedia.api.entities.issue.evidence.IssueEvidence;
import com.patternpedia.api.entities.issue.evidence.IssueEvidenceRating;
import com.patternpedia.api.rest.model.issue.IssueModelRequest;
import com.patternpedia.api.rest.model.shared.AuthorModelRequest;
import com.patternpedia.api.rest.model.shared.CommentModel;
import com.patternpedia.api.rest.model.shared.EvidenceModel;
import com.patternpedia.api.rest.model.shared.RatingModelRequest;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface IssueService {

    Issue saveIssue(Issue issue);
    /** CRUD  */
    Issue createIssue(IssueModelRequest issueModelRequest, UUID userId);

    List<Issue> getAllIssues();

    Issue getIssueById(UUID issueId);

    Issue getIssueByURI(String uri);

    Issue updateIssue(UUID issueId, UUID userId, IssueModelRequest issueModelRequest);

    Issue updateIssueRating(UUID issueId, UUID userId, RatingModelRequest ratingModelRequest);

    void deleteIssue(UUID issueId);

    boolean authorPermissions(UUID issueId, UUID userId);

    /** Author */

    Issue saveIssueAuthor(UUID issueId, AuthorModelRequest authorModelRequest);

    Issue deleteIssueAuthor(UUID issueId, UUID userId);

    /** Comment */
    Issue createComment(UUID issueId, UUID userId, CommentModel commentModel);

    IssueComment getCommentById(UUID issueCommentId);

    Issue updateComment(UUID issueId, UUID commentId, UUID userId, CommentModel commentModel);

    Issue updateIssueCommentRating(UUID issueId, UUID commentId, UUID userId, RatingModelRequest ratingModelRequest);

    Issue deleteComment(UUID issueId, UUID commentId, UUID userId);

    /** Evidence */
    Issue createEvidence(UUID issueId, UUID userId, EvidenceModel evidenceModel);

    IssueEvidence getEvidenceById(UUID issueEvidenceId);

    Issue updateEvidence(UUID issueId, UUID evidenceId, UUID userId, EvidenceModel evidenceModel);

    Issue updateIssueEvidenceRating(UUID issueId, UUID evidenceID, UUID userId, RatingModelRequest ratingModelRequest);

    Issue deleteEvidence(UUID issueId, UUID evidenceId, UUID userId);


}
