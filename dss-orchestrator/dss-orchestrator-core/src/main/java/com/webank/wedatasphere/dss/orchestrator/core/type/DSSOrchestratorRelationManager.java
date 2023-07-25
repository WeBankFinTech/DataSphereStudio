package com.webank.wedatasphere.dss.orchestrator.core.type;

import com.webank.wedatasphere.dss.common.utils.ClassUtils;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationWarnException;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.conf.BDPConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author enjoyyin
 * @date 2022-03-20
 * @since 1.1.0
 */
public class DSSOrchestratorRelationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSOrchestratorRelationManager.class);

    private static final List<DSSOrchestratorRelation> dssOrchestratorRelations = ClassUtils.getInstances(DSSOrchestratorRelation.class, c -> c != OrchestratorJsonRelation.class);

    static {
        int index = 1;
        while(true) {
            String key = "wds.dss.orchestrator.new." + index + "th";
            if(BDPConfiguration.contains(key,false)) {
                String relation = BDPConfiguration.get(key,false);
                OrchestratorJsonRelation jsonRelation = new OrchestratorJsonRelation(relation);
                jsonRelation.init();
                LOGGER.info("loaded DSSOrchestratorRelation is {}.", relation);
                dssOrchestratorRelations.add(jsonRelation);
            } else {
                break;
            }
            index ++;
        }
        BiConsumer<Function<DSSOrchestratorRelation, String>, String> getRepeatRelations = (getField, fieldName) -> {
            String repeatRelations = dssOrchestratorRelations.stream().map(getField).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet().stream().filter(entry -> entry.getValue() > 1).map(Map.Entry::getKey)
                    .collect(Collectors.joining(", "));
            if(StringUtils.isNotEmpty(repeatRelations)) {
                throw new ExternalOperationWarnException(50035, "repeated DSSOrchestratorRelation in " + fieldName + ": " + repeatRelations);
            }
        };
        getRepeatRelations.accept(DSSOrchestratorRelation::getDSSOrchestratorName, "name");
        getRepeatRelations.accept(DSSOrchestratorRelation::getDSSOrchestratorMode, "mode");
        LOGGER.info("The DSSOrchestratorRelation list is {}.", dssOrchestratorRelations.stream().map(DSSOrchestratorRelation::getDSSOrchestratorName)
                .collect(Collectors.joining(", ")));
    }

    public static List<DSSOrchestratorRelation> getDSSOrchestratorRelations() {
        return ListUtils.unmodifiableList(dssOrchestratorRelations);
    }

    public static DSSOrchestratorRelation getDSSOrchestratorRelationByName(String dssOrchestratorName) {
        return dssOrchestratorRelations.stream().filter(dssOrchestratorRelation -> dssOrchestratorRelation.getDSSOrchestratorName().equals(dssOrchestratorName))
                .findAny().orElseThrow(() -> new ExternalOperationWarnException(50355, "Not exists DSSOrchestrator name " + dssOrchestratorName));
    }

    public static DSSOrchestratorRelation getDSSOrchestratorRelationByMode(String dssOrchestratorMode) {
        return dssOrchestratorRelations.stream().filter(dssOrchestratorRelation -> dssOrchestratorRelation.getDSSOrchestratorMode().equals(dssOrchestratorMode))
                .findAny().orElseThrow(() -> new ExternalOperationWarnException(50355, "Not exists DSSOrchestrator mode " + dssOrchestratorMode));
    }

}
