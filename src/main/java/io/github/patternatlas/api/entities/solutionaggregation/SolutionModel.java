package io.github.patternatlas.api.entities.solutionaggregation;

import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import io.github.patternatlas.api.entities.designmodel.DesignModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "solution_model")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class SolutionModel {

    @Id
    @GeneratedValue(generator = "pg-uuid")
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "solutionModel")
    private List<SolutionModelConcreteSolutionInstance> concreteSolutionInstances;

    @OneToMany(mappedBy = "solutionModel")
    private List<SolutionModelEdge> edges;

    @ManyToOne
    @JoinColumn(name = "design_model_id")
    private DesignModel designModel;

}
