package com.webank.wedatasphere.dss.datamodel.indicator.dto;

import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicator;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicatorContent;


public class IndicatorVersionDTO {

    private DssDatamodelIndicator essential;

    private DssDatamodelIndicatorContent content;

    public IndicatorVersionDTO() {
    }

    public IndicatorVersionDTO(DssDatamodelIndicator essential, DssDatamodelIndicatorContent content) {
        this.essential = essential;
        this.content = content;
    }

    public DssDatamodelIndicator getEssential() {
        return essential;
    }

    public void setEssential(DssDatamodelIndicator essential) {
        this.essential = essential;
    }

    public DssDatamodelIndicatorContent getContent() {
        return content;
    }

    public void setContent(DssDatamodelIndicatorContent content) {
        this.content = content;
    }

    public static IndicatorVersionDTO of(DssDatamodelIndicator essential, DssDatamodelIndicatorContent content){
        IndicatorVersionDTO dto = new IndicatorVersionDTO();
        dto.setContent(content);
        dto.setEssential(essential);
        return dto;
    }
}
