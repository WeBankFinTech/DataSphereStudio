package com.webank.wedatasphere.dss.data.governance.entity;


import lombok.Data;
import org.apache.atlas.model.glossary.AtlasGlossaryTerm;

@Data
public class CreateLabelInfo {
    private String name;

    private String guid;

    public static CreateLabelInfo from(AtlasGlossaryTerm term) {
        CreateLabelInfo info = new CreateLabelInfo();
        info.setName(term.getName());
        info.setGuid(term.getGuid());
        return info;
    }
}
