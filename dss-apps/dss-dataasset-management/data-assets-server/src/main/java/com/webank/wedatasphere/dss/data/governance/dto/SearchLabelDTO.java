package com.webank.wedatasphere.dss.data.governance.dto;


import lombok.Data;
import org.apache.atlas.model.instance.AtlasEntityHeader;

@Data
public class SearchLabelDTO {
    private String name;
    private String guid;

    public static SearchLabelDTO from(AtlasEntityHeader atlasEntityHeader){
        SearchLabelDTO dto = new SearchLabelDTO();
        dto.setGuid(atlasEntityHeader.getGuid());
        dto.setName(atlasEntityHeader.getAttribute("name").toString());
        return dto;
    }
}
