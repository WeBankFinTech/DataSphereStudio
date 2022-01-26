package com.webank.wedatasphere.dss.flow.execution.entrance.enums;

import java.util.Arrays;

/**
 * Description 执行策略
 *
 * @Author elishazhang
 * @Date 2021/11/25
 */

public enum ExecuteStrategyEnum {


    IS_EXECUTE("isExecute", "execute", "执行"),
    IS_RE_EXECUTE("isReExecute", "reExecute", "失败重跑"),
    IS_SELECTED_EXECUTE("isSelectedExecute", "selectedExecute", "选中执行");

    private String name;
    private String value;
    private String desc;

    ExecuteStrategyEnum(String name, String value, String desc) {
        this.name = name;
        this.value = value;
        this.desc = desc;
    }

    public static ExecuteStrategyEnum getEnum(String value) {
        return Arrays.stream(ExecuteStrategyEnum.values()).filter(e -> e.getValue().equals(value)).findFirst().orElseThrow(NullPointerException::new);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
