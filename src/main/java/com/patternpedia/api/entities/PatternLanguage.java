package com.patternpedia.api.entities;

import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import com.patternpedia.api.entities.candidate.Candidate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PatternLanguage extends PatternGraph {

    private URL logo;

    @Column(nullable = true)
    private String creativeCommonsReference;

    @JsonIgnore
    @OneToMany(mappedBy = "patternLanguage")
    private List<Candidate> candidates;

    @JsonIgnore
    @OneToMany(mappedBy = "patternLanguage")
    private List<Pattern> patterns;

    @OneToOne(mappedBy = "patternLanguage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PatternSchema patternSchema;

    @JsonIgnore
    @OneToMany(mappedBy = "patternLanguage", fetch = FetchType.LAZY)
    private List<DirectedEdge> directedEdges;

    @JsonIgnore
    @OneToMany(mappedBy = "patternLanguage", fetch = FetchType.LAZY)
    private List<UndirectedEdge> undirectedEdges;


}
