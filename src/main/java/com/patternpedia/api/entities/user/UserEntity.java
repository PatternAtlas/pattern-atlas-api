package com.patternpedia.api.entities.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.patternpedia.api.entities.candidate.CandidateRating;
import com.patternpedia.api.entities.candidate.comment.CandidateComment;
import com.patternpedia.api.entities.candidate.author.CandidateAuthor;
import com.patternpedia.api.entities.candidate.evidence.CandidateEvidence;
import com.patternpedia.api.entities.issue.comment.IssueComment;
import com.patternpedia.api.entities.issue.author.IssueAuthor;
import com.patternpedia.api.entities.issue.IssueRating;
import com.patternpedia.api.entities.issue.evidence.IssueEvidence;
import com.patternpedia.api.entities.user.role.Role;
import com.patternpedia.api.rest.model.user.UserModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
public class UserEntity implements Serializable{

    /** User fields*/
    @Id
    @GeneratedValue(generator = "pg-uuid")
    private UUID id;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne()
    private Role role;

    @NaturalId(mutable = true)
    @Column(nullable = false, unique = true)
    private String email;

    @NaturalId(mutable = true)
    @Column(nullable = false, unique = true)
    private String name;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /** Issue fields*/
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<IssueAuthor> issues = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<IssueRating> issueRatings = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IssueComment> issueComments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IssueEvidence> issueEvidence = new ArrayList<>();

    /** Candidate fields*/
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CandidateAuthor> candidates = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CandidateRating> candidateRatings = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandidateComment> candidateComments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandidateEvidence> candidateEvidence = new ArrayList<>();

    /** Pattern fields*/
//    @JsonIgnore
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<CandidateRating> candidateRatings = new HashSet<>();
//
//    @JsonIgnore
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<CandidateComment> candidateComments = new ArrayList<>();

    public UserEntity(UserModel userModel, String password) {
        this.id = userModel.getId();
        this.name = userModel.getName();
        this.email = userModel.getEmail();
        this.password = password;
    }

    public UserEntity(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void updateUserEntity(UserModel userModel) {
        this.setName(userModel.getName());
        this.setEmail(userModel.getEmail());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity)) return false;
        UserEntity that = (UserEntity) o;
        return id.equals(that.id) &&
                email.equals(that.email) &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, name);
    }

    @Override
    public String toString() {
        return "User: " + this.name;
    }
}
