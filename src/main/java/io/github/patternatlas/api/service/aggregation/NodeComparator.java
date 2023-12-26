package io.github.patternatlas.api.service.aggregation;

import java.util.Comparator;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class NodeComparator implements Comparator<PseydoNode> {
    @Override
    public int compare(PseydoNode solutionModelNode, PseydoNode ot) {
        log.debug("Comparing " + solutionModelNode.getName() + " and " + ot.getName());
        if (solutionModelNode.getType().equals(ot.getType()) &&
            solutionModelNode.getCapabilities().containsAll(ot.getCapabilities()) &&
            solutionModelNode.getRequirements().containsAll(ot.getRequirements())) {
            log.debug("Matched");
            return 0;
        }
        log.debug("Not Matched");
        return -1;
    }
}
