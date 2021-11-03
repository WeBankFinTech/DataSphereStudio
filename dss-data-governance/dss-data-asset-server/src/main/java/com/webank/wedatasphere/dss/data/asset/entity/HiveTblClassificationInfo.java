package com.webank.wedatasphere.dss.data.asset.entity;

import lombok.Data;

import java.util.List;

/**
 * @author suyc
 * @Classname HiveTblClassificationInfo
 * @Description TODO
 * @Date 2021/10/11 14:21
 * @Created by suyc
 */
@Data
public class HiveTblClassificationInfo {
    private List<String> oldClassifications;
    private List<String> newClassifications;
}
