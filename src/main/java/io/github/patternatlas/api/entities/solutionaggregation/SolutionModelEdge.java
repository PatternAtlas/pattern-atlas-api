package io.github.patternatlas.api.entities.solutionaggregation;

import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "solution_model_edge")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class SolutionModelEdge {

    @Id
    @GeneratedValue(generator = "pg-uuid")
    private UUID id;

    private static ObjectMapper mapper = new ObjectMapper();

    private Boolean directed;

    private String edgeSemantic;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "solution_model_id")
    private SolutionModel solutionModel;

    @ManyToOne
    @JoinColumn(name = "cs_1_id")
    private SolutionModelConcreteSolutionInstance csInstance1;

    @ManyToOne
    @JoinColumn(name = "cs_2_id")
    private SolutionModelConcreteSolutionInstance csInstance2;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Object description;

    public void setDescription(Object description) {
        this.description = mapper.valueToTree(description);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolutionModelEdge that = (SolutionModelEdge) o;
        return Objects.equals(id, that.id);
    }
}
