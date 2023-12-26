package io.github.patternatlas.api.service.aggregation;

import java.util.Comparator;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class EdgeComparator implements Comparator<PseydoEdge>{
    @Override
    public int compare(PseydoEdge solutionModelEdge, PseydoEdge detectorGraphEdge) {
        if (solutionModelEdge.getType().equals(detectorGraphEdge.getType()) &&
            solutionModelEdge.isDirected() == detectorGraphEdge.isDirected()) {
            log.debug("Matched");
            return 0;
        }
        log.debug("Not Matched");
        return -1;
    }
}
