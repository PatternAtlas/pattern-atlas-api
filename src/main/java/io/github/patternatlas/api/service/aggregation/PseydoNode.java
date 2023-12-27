package io.github.patternatlas.api.service.aggregation;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.patternatlas.api.entities.solutionaggregation.SolutionModelConcreteSolutionInstance;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PseydoNode {
    private String name;

    private Set<String> capabilities;

    private Set<String> requirements;

    private String type;

    private UUID id;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static PseydoNode from(SolutionModelConcreteSolutionInstance solutionModelConcreteSolutionInstance) {
        var pseydoNode = new PseydoNode();
        var caps = solutionModelConcreteSolutionInstance.getConcreteSolutionDescriptor().getCapabilities() != null ?
            solutionModelConcreteSolutionInstance.getConcreteSolutionDescriptor().getCapabilities() : new HashSet<String>();
        var reqs = solutionModelConcreteSolutionInstance.getConcreteSolutionDescriptor().getRequirements() != null ?
            solutionModelConcreteSolutionInstance.getConcreteSolutionDescriptor().getRequirements() : new HashSet<String>();
        pseydoNode.setCapabilities(new HashSet<>(caps));
        pseydoNode.setRequirements(new HashSet<>(reqs));
        pseydoNode.setType(solutionModelConcreteSolutionInstance.getConcreteSolutionDescriptor().getType());
        pseydoNode.setId(solutionModelConcreteSolutionInstance.getId());
        return pseydoNode;
    }

    public static PseydoNode from(OperationTarget operationTarget) {
        var pseydoNode = new PseydoNode();
        pseydoNode.setCapabilities(operationTarget.getCapabilities());
        pseydoNode.setRequirements(operationTarget.getRequirements());
        pseydoNode.setType(operationTarget.getType());
        pseydoNode.setId(operationTarget.getId());
        return pseydoNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PseydoNode that = (PseydoNode) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
