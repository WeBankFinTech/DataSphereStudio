package com.webank.wedatasphere.dss.standard.app.development.utils;

import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.standard.app.development.operation.DevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.*;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationWarnException;
import com.webank.wedatasphere.dss.standard.common.utils.RequestRefUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.*;

/**
 * @author enjoyyin
 * @date 2022-03-12
 * @since 0.5.0
 */
public class DevelopmentOperationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevelopmentOperationUtils.class);

    public static <K extends DevelopmentRequestRef, V extends ResponseRef> V tryDevelopmentOperation(Supplier<DevelopmentService> getDevelopmentService,
                                                                                Function<DevelopmentService, DevelopmentOperation> getDevelopmentOperation,
                                                                                Consumer<DSSJobContentRequestRef> jobContentRequestRefConsumer,
                                                                                Consumer<RefJobContentRequestRef> refJobContentRequestRefConsumer,
                                                                                Consumer<DSSContextRequestRef> contextRequestRefConsumer,
                                                                                Consumer<ProjectRefRequestRef> projectRefRequestRefConsumer,
                                                                                BiFunction<DevelopmentOperation, K, V> requestRefOperationFunction,
                                                                                BiConsumer<K, V> responseRefConsumer,
                                                                                String errorMsg) {
        DevelopmentService developmentService = getDevelopmentService.get();
        if(developmentService == null) {
            LOGGER.error("{} failed. Caused by: The AppConn has no DevelopmentService.", errorMsg);
            throw new ExternalOperationWarnException(60092, errorMsg + " failed. Caused by: The AppConn has no DevelopmentService.");
        }
        DevelopmentOperation operation = getDevelopmentOperation.apply(developmentService);
        K requestRef = RequestRefUtils.getRequestRef(operation);
        if(jobContentRequestRefConsumer != null && requestRef instanceof DSSJobContentRequestRef) {
            jobContentRequestRefConsumer.accept((DSSJobContentRequestRef) requestRef);
        }
        if(refJobContentRequestRefConsumer != null && requestRef instanceof RefJobContentRequestRef) {
            refJobContentRequestRefConsumer.accept((RefJobContentRequestRef) requestRef);
        }
        if(contextRequestRefConsumer != null && requestRef instanceof DSSContextRequestRef) {
            contextRequestRefConsumer.accept((DSSContextRequestRef) requestRef);
        }
        if(projectRefRequestRefConsumer != null && requestRef instanceof ProjectRefRequestRef) {
            projectRefRequestRefConsumer.accept((ProjectRefRequestRef) requestRef);
        }
        V responseRef = requestRefOperationFunction.apply(operation, requestRef);
        if(responseRef.isFailed()) {
            LOGGER.error("{} failed. Caused by: {}.", errorMsg, responseRef.getErrorMsg());
            DSSExceptionUtils.dealWarnException(61123,
                    String.format("%s failed. Caused by: %s.", errorMsg, responseRef.getErrorMsg()),
                    ExternalOperationWarnException.class);
        }
        if(responseRefConsumer != null) {
            responseRefConsumer.accept(requestRef, responseRef);
        }
        return responseRef;
    }

    public static <K extends DevelopmentRequestRef, V extends ResponseRef> V tryDevelopmentOperation(Supplier<DevelopmentService> getDevelopmentService,
                                                                                                     Function<DevelopmentService, DevelopmentOperation> getDevelopmentOperation,
                                                                                                     Consumer<DSSJobContentRequestRef> jobContentRequestRefConsumer,
                                                                                                     Consumer<RefJobContentRequestRef> refJobContentRequestRefConsumer,
                                                                                                     Consumer<DSSContextRequestRef> contextRequestRefConsumer,
                                                                                                     Consumer<ProjectRefRequestRef> projectRefRequestRefConsumer,
                                                                                                     BiFunction<DevelopmentOperation, K, V> requestRefOperationFunction,
                                                                                                     String errorMsg) {
        return tryDevelopmentOperation(getDevelopmentService, getDevelopmentOperation, jobContentRequestRefConsumer, refJobContentRequestRefConsumer,
                contextRequestRefConsumer, projectRefRequestRefConsumer, requestRefOperationFunction, null, errorMsg);
    }

    public static <K extends DevelopmentRequestRef, V extends ResponseRef> V tryRefJobContentRequestRefOperation(Supplier<DevelopmentService> getDevelopmentService,
                                                                                                     Function<DevelopmentService, DevelopmentOperation> getDevelopmentOperation,
                                                                                                     Consumer<RefJobContentRequestRef> refJobContentRequestRefConsumer,
                                                                                                     Consumer<DSSContextRequestRef> contextRequestRefConsumer,
                                                                                                     Consumer<ProjectRefRequestRef> projectRefRequestRefConsumer,
                                                                                                     BiFunction<DevelopmentOperation, K, V> responseRefConsumer,
                                                                                                     String errorMsg) {
        return tryDevelopmentOperation(getDevelopmentService, getDevelopmentOperation, null,
                refJobContentRequestRefConsumer, contextRequestRefConsumer, projectRefRequestRefConsumer, responseRefConsumer, errorMsg);
    }

    public static <K extends DevelopmentRequestRef, V extends ResponseRef> V tryDSSJobContentRequestRefOperation(Supplier<DevelopmentService> getDevelopmentService,
                                                                                                                 Function<DevelopmentService, DevelopmentOperation> getDevelopmentOperation,
                                                                                                                 Consumer<DSSJobContentRequestRef> jobContentRequestRefConsumer,
                                                                                                                 Consumer<DSSContextRequestRef> contextRequestRefConsumer,
                                                                                                                 Consumer<ProjectRefRequestRef> projectRefRequestRefConsumer,
                                                                                                                 BiFunction<DevelopmentOperation, K, V> responseRefConsumer,
                                                                                                                 String errorMsg) {
        return tryDevelopmentOperation(getDevelopmentService, getDevelopmentOperation, jobContentRequestRefConsumer,
                null, contextRequestRefConsumer, projectRefRequestRefConsumer, responseRefConsumer, errorMsg);
    }

    public static <K extends DevelopmentRequestRef, V extends ResponseRef> V tryDevelopmentRequestRefOperation(Supplier<DevelopmentService> getDevelopmentService,
                                                                                                                 Function<DevelopmentService, DevelopmentOperation> getDevelopmentOperation,
                                                                                                                 Consumer<DSSContextRequestRef> contextRequestRefConsumer,
                                                                                                                 Consumer<ProjectRefRequestRef> projectRefRequestRefConsumer,
                                                                                                                 BiFunction<DevelopmentOperation, K, V> responseRefConsumer,
                                                                                                                 String errorMsg) {
        return tryDevelopmentOperation(getDevelopmentService, getDevelopmentOperation, null,
                null, contextRequestRefConsumer, projectRefRequestRefConsumer, responseRefConsumer, errorMsg);
    }
}
