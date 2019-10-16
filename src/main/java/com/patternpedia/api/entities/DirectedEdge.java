package com.patternpedia.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(exclude = "patternLanguage")
public class DirectedEdge extends PatternRelationDescriptor {

    @ManyToOne(optional = false)
    private Pattern source;

    @ManyToOne(optional = false)
    private Pattern target;

    @JsonIgnore
    @ManyToOne
    private PatternLanguage patternLanguage;

}
