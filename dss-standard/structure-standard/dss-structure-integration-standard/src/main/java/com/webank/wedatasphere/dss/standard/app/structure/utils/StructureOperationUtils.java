package com.webank.wedatasphere.dss.standard.app.structure.utils;

import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.standard.app.structure.StructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.DSSProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardWarnException;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.standard.common.utils.RequestRefUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.linkis.common.exception.WarnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author enjoyyin
 * @date 2022-03-14
 * @since 0.5.0
 */
public class StructureOperationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(StructureOperationUtils.class);

    public static <S extends StructureService, K extends StructureRequestRef, V extends ResponseRef> V tryStructureOperation(Supplier<S> getStructureService,
                                                                                                    Function<S, StructureOperation> getStructureOperation,
                                                                                                    Consumer<StructureRequestRef> structureRequestRefConsumer,
                                                                                                    BiFunction<StructureOperation, K, V> responseRefConsumer,
                                                                                                    String errorMsg) {
        S structureService = getStructureService.get();
        if(structureService == null) {
            LOGGER.warn("{} is ignored. Caused by: The AppConn has no StructureService.", errorMsg);
            return null;
        }
        StructureOperation operation = getStructureOperation.apply(structureService);
        if(operation == null) {
            LOGGER.warn("{} is ignored. Caused by: The corresponding operation is not exists.", errorMsg);
            return null;
        }
        K requestRef = RequestRefUtils.getRequestRef(operation);
        if(structureRequestRefConsumer != null) {
            structureRequestRefConsumer.accept(requestRef);
        }
        V responseRef;
        try {
            responseRef = responseRefConsumer.apply(operation, requestRef);
        } catch (WarnException e) {
            String error;
            if(StringUtils.isBlank(e.getDesc())) {
                error = String.format("%s failed, no detail error returned by this AppConn, please ask admin for help.", errorMsg);
            } else {
                error = String.format("%s failed. Caused by: %s.", errorMsg, e.getDesc());
            }
            throw new ExternalOperationFailedException(50009, error, e);
        } catch (RuntimeException e) {
            String error = String.format("%s failed. Caused by: %s.", errorMsg, ExceptionUtils.getRootCauseMessage(e));
            throw new ExternalOperationFailedException(50009, error, e);
        }
        if(responseRef.isFailed()) {
            LOGGER.error("{} failed. Caused by: {}.", errorMsg, responseRef.getErrorMsg());
            DSSExceptionUtils.dealWarnException(61123,
                    String.format("%s failed. Caused by: %s.", errorMsg, responseRef.getErrorMsg()),
                    AppStandardWarnException.class);
        }
        return responseRef;
    }

    public static <K extends StructureRequestRef, V extends ResponseRef> V tryProjectOperation(Supplier<ProjectService> getProjectService,
                                                                                                 Function<ProjectService, StructureOperation> getProjectOperation,
                                                                                                 Consumer<DSSProjectContentRequestRef> dssProjectContentRequestRefConsumer,
                                                                                                 Consumer<RefProjectContentRequestRef> refProjectContentRequestRefConsumer,
                                                                                                 BiFunction<StructureOperation, K, V> responseRefConsumer,
                                                                                                 String errorMsg) {
        return tryStructureOperation(getProjectService, getProjectOperation, structureRequestRef -> {
            if(dssProjectContentRequestRefConsumer != null && structureRequestRef instanceof DSSProjectContentRequestRef) {
                dssProjectContentRequestRefConsumer.accept((DSSProjectContentRequestRef) structureRequestRef);
            }
            if(refProjectContentRequestRefConsumer != null && structureRequestRef instanceof RefProjectContentRequestRef) {
                refProjectContentRequestRefConsumer.accept((RefProjectContentRequestRef) structureRequestRef);
            }
        }, responseRefConsumer, errorMsg);
    }

}
