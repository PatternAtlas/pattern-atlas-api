package io.github.patternatlas.api.entities.solutionaggregation;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "solution_model_concrete_solution_instance")
public class SolutionModelConcreteSolutionInstance {

    @Id
    @GeneratedValue(generator = "pg-uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "concrete_solution_descriptor_id")
    private ConcreteSolutionDescriptor concreteSolutionDescriptor;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "solution_model_id")
    private SolutionModel solutionModel;

    public SolutionModelConcreteSolutionInstance(SolutionModel solutionModel, ConcreteSolutionDescriptor concreteSolutionDescriptor) {
        this.solutionModel = solutionModel;
        this.concreteSolutionDescriptor = concreteSolutionDescriptor;
    }
}
