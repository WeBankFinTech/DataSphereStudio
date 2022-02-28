package com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.builder;

import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.entity.LinkisTaskExecutionContext;
import com.webank.wedatasphere.linkis.computation.client.LinkisJobClient;
import com.webank.wedatasphere.linkis.computation.client.interactive.InteractiveJobBuilder;
import com.webank.wedatasphere.linkis.computation.client.interactive.SubmittableInteractiveJob;
import com.webank.wedatasphere.linkis.computation.client.utils.LabelKeyUtils;

/**
 * linkis interactive job builder
 */
public class LinkisInteractiveJobBuilder {

    private LinkisTaskExecutionContext linkisTaskExecutionContext;
    private SubmittableInteractiveJob job;
    private InteractiveJobBuilder builder;

    public static LinkisInteractiveJobBuilder getLinkisInteractiveJobBuilder() {
        return new LinkisInteractiveJobBuilder();
    }

    public LinkisInteractiveJobBuilder setLinkisTaskExecuionContext(LinkisTaskExecutionContext linkisTaskExecutionContext) {
        this.linkisTaskExecutionContext = linkisTaskExecutionContext;
        return this;
    }

    public SubmittableInteractiveJob build() {
        LinkisJobClient.config().setDefaultServerUrl(linkisTaskExecutionContext.getGatewayUrl());
        builder = LinkisJobClient.interactive().builder();
        builder.setCode(linkisTaskExecutionContext.getExecuteCode());
        builder.setRunTypeStr(linkisTaskExecutionContext.getRunType());
        builder.addExecuteUser(linkisTaskExecutionContext.getExecuteUser());
        addLabels();
        addStartupParams();
        addVariable();
        addSource();
        job = builder.build();
        return job;
    }

    /**
     * 添加label信息
     */
    private void addLabels() {
        builder.addLabel(LabelKeyUtils.ENGINE_TYPE_LABEL_KEY(), linkisTaskExecutionContext.getLabelsMap().get(LabelKeyUtils.ENGINE_TYPE_LABEL_KEY()));
        builder.addLabel(LabelKeyUtils.USER_CREATOR_LABEL_KEY(), linkisTaskExecutionContext.getLabelsMap().get(LabelKeyUtils.USER_CREATOR_LABEL_KEY()));
    }

    /**
     * 添加启动参数
     */
    private void addStartupParams() {
        if (!linkisTaskExecutionContext.getStartupParams().isEmpty()) {
            linkisTaskExecutionContext.getStartupParams().forEach((key, value) -> {
                builder.addStartupParam(key, value);
            });
        }
    }

    /**
     * 添加参数信息
     */
    private void addVariable() {
        if (!linkisTaskExecutionContext.getVariableMap().isEmpty()) {
            linkisTaskExecutionContext.getVariableMap().forEach((key, value) -> {
                builder.addVariable(key, value);
            });
        }
    }

    /**
     * 添加source信息
     */
    private void addSource() {
        if (!linkisTaskExecutionContext.getSourceMap().isEmpty()) {
            linkisTaskExecutionContext.getSourceMap().forEach((key, value) -> {
                builder.addSource(key, value);
            });
        }
    }

    public LinkisTaskExecutionContext getLinkisTaskExecuionContext() {
        return linkisTaskExecutionContext;
    }

    public SubmittableInteractiveJob getJob() {
        return job;
    }

    public InteractiveJobBuilder getBuilder() {
        return builder;
    }
}
