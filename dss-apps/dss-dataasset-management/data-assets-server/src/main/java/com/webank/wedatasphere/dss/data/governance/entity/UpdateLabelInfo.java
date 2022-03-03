package com.webank.wedatasphere.dss.data.governance.entity;

import lombok.Data;
import org.apache.atlas.model.glossary.AtlasGlossaryTerm;

@Data
public class UpdateLabelInfo {
    private String name;

    private String guid;

    public static UpdateLabelInfo from(AtlasGlossaryTerm term) {
        UpdateLabelInfo info = new UpdateLabelInfo();
        info.setName(term.getName());
        info.setGuid(term.getGuid());
        return info;
    }
}
