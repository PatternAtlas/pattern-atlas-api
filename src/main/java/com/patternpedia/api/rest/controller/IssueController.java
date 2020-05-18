package com.patternpedia.api.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patternpedia.api.entities.issue.IssueComment;
import com.patternpedia.api.entities.issue.Issue;
import com.patternpedia.api.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/issues", produces = "application/hal+json")
public class IssueController {

    Logger logger = LoggerFactory.getLogger(IssueController.class);

    private IssueService issueService;
    private PatternLanguageService patternLanguageService;
    private ObjectMapper objectMapper;

    public IssueController(
            IssueService issueService,
            PatternLanguageService patternLanguageService,
            ObjectMapper objectMapper
    ) {
        this.issueService = issueService;
        this.patternLanguageService = patternLanguageService;
        this.objectMapper = objectMapper;
    }

    /**
     * GET Methods
     */
    @GetMapping(value = "")
    List<Issue> all() {
        return this.issueService.getAllIssues();
    }

    @GetMapping(value = "/{issueId}")
    @PreAuthorize(value = "#oauth2.hasScope('read')")
    Issue getIssueById(@PathVariable UUID issueId) {
        return this.issueService.getIssueById(issueId);
    }

    @GetMapping(value = "/?uri={issueUri}")
    Issue getIssueByUri(@PathVariable String issueUri) {
        return this.issueService.getIssueByURI(issueUri);
    }

    /**
     * CREATE Methods
     */
    @PostMapping(value = "")
//    @PreAuthorize(value = "#oauth2.hasScope('write')")
    @ResponseStatus(HttpStatus.CREATED)
    Issue newIssue(@RequestBody Issue issue) {
        return this.issueService.createIssue(issue);
    }

    @PostMapping(value = "/{issueId}/comments/{userId}")
//    @PreAuthorize(value = "#oauth2.hasScope('write')")
    @ResponseStatus(HttpStatus.CREATED)
    Issue newIssueComment(@PathVariable UUID issueId, @PathVariable UUID userId, @RequestBody IssueComment issueComment) {
        return this.issueService.createComment(issueId, userId, issueComment);
    }

    /**
     * UPDATE Methods
     */
    @PutMapping(value = "/{issueId}")
    Issue putIssue(@PathVariable UUID issueId, @RequestBody Issue issue) {
        logger.info(issue.toString());
        return this.issueService.updateIssue(issue);
    }

    @PutMapping(value = "/{issueId}/users/{userId}/rating/{rating}")
    Issue putIssueRating(@PathVariable UUID issueId, @PathVariable UUID userId, @PathVariable String rating) {
        return this.issueService.userRating(issueId, userId, rating);
    }

    @PutMapping(value = "/comments/{issueCommentId}/users/{userId}/rating/{rating}")
    Issue putIssueCommentRating(@PathVariable UUID issueCommentId, @PathVariable UUID userId, @PathVariable String rating) {
        return this.issueService.commentUserRating(issueCommentId, userId, rating);
    }

    /**
     * DELETE Methods
     */
    @DeleteMapping(value = "/{issueId}")
//    @PreAuthorize(value = "#oauth2.hasScope('de')")
    ResponseEntity<?> deleteIssue(@PathVariable UUID issueId) {
        this.issueService.deleteIssue(issueId);
        return ResponseEntity.noContent().build();
    }
}
