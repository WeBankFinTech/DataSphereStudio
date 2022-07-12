package com.webank.wedatasphere.dss.standard.app.development.utils;

import org.apache.linkis.protocol.util.ImmutablePair;

/**
 * @author enjoyyin
 * @date 2022-04-14
 * @since 1.1.0
 */
public class QueryJumpUrlConstant {

    /**
     * 工作流节点 ID，对应到 DSS 工作流的 nodes 里面对应 node 的 key。
     */
    public static final ImmutablePair<String, String> NODE_ID = new ImmutablePair<>("nodeId", "${nodeId}");

    /**
     * 工作流节点 ID，对应到 DSS 工作流的 nodes 里面对应 node 的 name。
     */
    public static final ImmutablePair<String, String> NODE_NAME = new ImmutablePair<>("nodeName", "${nodeName}");

    /**
     * DSS 工程名。
     */
    public static final ImmutablePair<String, String> PROJECT_NAME = new ImmutablePair<>("projectName", "${projectName}");

}
