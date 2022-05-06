package com.webank.wedatasphere.dss.data.asset.entity;

import lombok.Data;

import java.util.List;

@Data
public class HiveTblClassificationInfo {
    private List<String> oldClassifications;
    private List<String> newClassifications;
}
