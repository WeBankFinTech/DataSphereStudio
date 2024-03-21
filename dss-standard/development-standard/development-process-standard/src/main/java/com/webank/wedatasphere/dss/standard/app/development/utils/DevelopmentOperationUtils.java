package com.webank.wedatasphere.dss.standard.app.development.utils;

import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.standard.app.development.operation.DevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.*;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationWarnException;
import com.webank.wedatasphere.dss.standard.common.utils.RequestRefUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.linkis.common.exception.WarnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.*;

/**
 * operation工具
 * @author enjoyyin
 * @date 2022-03-12
 * @since 0.5.0
 */
public class DevelopmentOperationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevelopmentOperationUtils.class);

    /**
     * 执行operation。
     * 先通过getDevelopmentService和getDevelopmentOperation获取operation，在根据operation中的requestRef不同实现以及不同的
     * RequestRefConsumer先对requestRef做处理，最后执行operation得到 ResponseRef结果后返回
     *
     * @param getDevelopmentService DevelopmentService获取器
     * @param getDevelopmentOperation operation获取器
     * @param jobContentRequestRefConsumer  对RequestRef的jobContent处理器。如果处理器不为空，且requestRef是DSSJobContentRequestRef的话，会本执行此处理器
     * @param refJobContentRequestRefConsumer 对RequestRef的refJobContent处理器。如果处理不为空，且requestRef是RefJobContentRequestRef的话，会执行此处理器
     * @param contextRequestRefConsumer 对RequestRef的context处理器。如果处理不为空，且requestRef是DSSContextRequestReff的话，会执行此处理器
     * @param projectRefRequestRefConsumer 对RequestRef的project处理器。如果处理不为空，且requestRef是ProjectRefRequestRef的话，会执行此处理器
     * @param requestRefOperationFunction operation的执行器，由外部传入
     * @param responseRefConsumer
     * @param errorMsg
     * @return
     * @param <K>
     * @param <V>
     */
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
        if(operation == null) {
            LOGGER.error("{} failed. Caused by: The corresponding development operation is not exists.", errorMsg);
            throw new ExternalOperationWarnException(60092, errorMsg + " failed. Caused by: The AppConn has no corresponding development operation.");
        }
        K requestRef = RequestRefUtils.getRequestRef(operation);
        if(jobContentRequestRefConsumer != null && requestRef instanceof DSSJobContentRequestRef) {
            LOGGER.info("requestRef is DSSJobContentRequestRef");
            jobContentRequestRefConsumer.accept((DSSJobContentRequestRef) requestRef);
        }
        if(refJobContentRequestRefConsumer != null && requestRef instanceof RefJobContentRequestRef) {
            LOGGER.info("requestRef is RefJobContentRequestRef");
            refJobContentRequestRefConsumer.accept((RefJobContentRequestRef) requestRef);
        }
        if(contextRequestRefConsumer != null && requestRef instanceof DSSContextRequestRef) {
            LOGGER.info("requestRef is DSSContextRequestRef");
            contextRequestRefConsumer.accept((DSSContextRequestRef) requestRef);
        }
        if(projectRefRequestRefConsumer != null && requestRef instanceof ProjectRefRequestRef) {
            LOGGER.info("requestRef is ProjectRefRequestRef");
            projectRefRequestRefConsumer.accept((ProjectRefRequestRef) requestRef);
        }
        V responseRef;
        try {
            responseRef = requestRefOperationFunction.apply(operation, requestRef);
        } catch (WarnException e) {
            String error;
            if(StringUtils.isBlank(e.getDesc())) {
                error = String.format("%s failed, no detail error returned by this AppConn, please ask admin for help.", errorMsg);
            } else {
                error = String.format("%s failed. Caused by: %s.", errorMsg, e.getDesc());
            }
            throw new ExternalOperationFailedException(50010, error, e);
        } catch (RuntimeException e) {
            String error = String.format("%s failed. Caused by: %s.", errorMsg, ExceptionUtils.getRootCauseMessage(e));
            throw new ExternalOperationFailedException(50010, error, e);
        }
        if(responseRef == null) {
            LOGGER.error("{} failed. Caused by: empty responseRef returned by AppConn.", errorMsg);
            throw new ExternalOperationFailedException(61123, errorMsg + " failed. Caused by: empty responseRef returned by AppConn.");
        } else if(responseRef.isFailed()) {
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
