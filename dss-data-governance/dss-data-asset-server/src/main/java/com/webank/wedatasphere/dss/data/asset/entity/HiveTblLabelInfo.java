package com.webank.wedatasphere.dss.data.asset.entity;

import lombok.Data;

import java.util.Set;

/**
 * @author suyc
 * @Classname HiveTblLabelInfo
 * @Description TODO
 * @Date 2021/10/18 15:37
 * @Created by suyc
 */
@Data
public class HiveTblLabelInfo {
    private Set<String> labels;
}
