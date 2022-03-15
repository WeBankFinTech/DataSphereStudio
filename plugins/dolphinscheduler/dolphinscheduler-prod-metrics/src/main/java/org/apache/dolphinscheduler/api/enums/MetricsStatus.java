package org.apache.dolphinscheduler.api.enums;

import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * @author enjoyyin
 * @date 2022-02-23
 * @since 0.5.0
 */
public enum MetricsStatus {

    QUERY_PROCESS_INSTANCE_LIST_ORDER_BY_DURATION_ERROR(10113, "query process instance list order by duration error",
        "分页查询工作流实例列表（耗时降序）错误"),
    QUERY_PROCESS_INSTANCE_STATISTICS_ERROR(10113, "query process instance statistics error", "查询周期内实例完成情况错误");

    private final int code;
    private final String enMsg;
    private final String zhMsg;

    private MetricsStatus(int code, String enMsg, String zhMsg) {
        this.code = code;
        this.enMsg = enMsg;
        this.zhMsg = zhMsg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        if (Locale.SIMPLIFIED_CHINESE.getLanguage().equals(LocaleContextHolder.getLocale().getLanguage())) {
            return this.zhMsg;
        } else {
            return this.enMsg;
        }
    }

}
