package com.webank.wedatasphere.warehouse.cqe;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class DwModifierCreateCommand {
    private Long themeDomainId;

    private String themeArea;

    private Long layerId;

    private String layerArea;

    private String typeName;

    private String description;

    private List<DwModifierCreateListItem> list;

    @Setter
    @Getter
    @ToString
    public static class DwModifierCreateListItem {
        private String name;
        private String identifier;
        private String formula;
    }
}
