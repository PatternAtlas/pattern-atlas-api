package io.github.patternatlas.api.entities.solutionaggregation;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.patternatlas.api.entities.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "concrete_solution_descriptor")
public class ConcreteSolutionDescriptor {

    private static ObjectMapper mapper = new ObjectMapper();

    @Id
    @GeneratedValue(generator = "pg-uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "pattern_id")
    private Pattern pattern;

    private String name;

    private String templateUri;

    @Lob
    private String template;

    private String type;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Object properties;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Object selectionCriteria;

    @Setter
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Set<String> requirements;

    @Setter
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Set<String> capabilities;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "concreteSolutionDescriptor")
    private List<SolutionModelConcreteSolutionInstance> solutionModelConcreteSolutionInstances;

    public void setProperties(Object description) {
        this.properties = mapper.valueToTree(description);
    }

    public void setSelectionCriteria(Object description) {
        this.selectionCriteria = mapper.valueToTree(description);
    }

}
