package io.github.patternatlas.api.service.aggregation;

import java.util.UUID;

import io.github.patternatlas.api.entities.solutionaggregation.SolutionModelEdge;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PseydoEdge {
    private UUID solutionModelEdgeId;
    private String type;
    private boolean directed;

    public static PseydoEdge from(SolutionModelEdge solutionModelEdge) {
        var pseydoEdge = new PseydoEdge();
        pseydoEdge.setSolutionModelEdgeId(solutionModelEdge.getId());
        pseydoEdge.setType(solutionModelEdge.getEdgeSemantic());
        pseydoEdge.setDirected(solutionModelEdge.getDirected());
        return pseydoEdge;
    }

    public static PseydoEdge from(DetectorGraphEdge detectorGraphEdge) {
        var pseydoEdge = new PseydoEdge();
        pseydoEdge.setType(detectorGraphEdge.getEdgeSemantic());
        pseydoEdge.setDirected(detectorGraphEdge.isDirected());
        return pseydoEdge;
    }
}
