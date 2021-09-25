package com.webank.wedatasphere.warehouse.cqe;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class DwModifierUpdateCommand {
    private Long id;

    private Long themeDomainId;

    private String themeArea;

    private Long layerId;

    private String layerArea;

    private String typeName;

    private String description;

    private List<DwModifierUpdateListItem> list;

    @Setter
    @Getter
    @ToString
    public static class DwModifierUpdateListItem {
        private String name;
        private String identifier;
        private String formula;
    }
}
