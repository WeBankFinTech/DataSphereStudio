package com.webank.wedatasphere.dss.standard.app.development.utils;

/**
 * @author enjoyyin
 * @date 2022-03-15
 * @since 0.5.0
 */
public class DSSJobContentConstant {

    /**
     * DSSJob 所归属的 DSS编排（如：DSS工作流）的编排名，其对应的 value 值为 {@code String}。
     */
    public static final String ORCHESTRATION_NAME = "orchestrationName";

    /**
     * DSSJob 所归属的 DSS编排（如：DSS工作流）的编排名，其对应的 value 值为 {@code Long}。
     */
    public static final String ORCHESTRATION_ID = "orchestrationId";

    /**
     * DSS编排的版本号，例如： DSS 工作流所在的版本。
     * <strong>请注意：该 key 只有在创建 refJob 时才存在，更新等操作不存在该 key。</strong>
     * 强烈建议根据 orcVersion 提供的 value 值，为 refJob 命名。
     * 例如：<br/>
     *    DSSJobContentRequestRef 的 name 为 test，orcVersion 为 v2，建议第三方 AppConn 在创建一个 refJob 时，
     *    新的 refJob 命名为 test_v2。
     * <br/>
     * 其对应的 value 值为 {@code String}，如：v1、v2、v3。
     */
    public static final String ORC_VERSION_KEY = "orcVersion";

    /**
     * DSSJob 的描述，其对应的 value 值为 {@code String}。
     */
    public static final String NODE_DESC_KEY = "desc";

    /**
     * DSSJob 所依赖的上游工作流节点，其对应的 value 值为 {@code List<DSSNode>}。
     */
    public static final String UP_STREAM_KEY = "upStreams";

    public static final String UP_STREAM_EMPTY_VALUE = "empty";

}
