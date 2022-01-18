package com.webank.wedatasphere.dss.datamodel.table.event;

import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

@ToString
@Getter
public class UnBindLabelByTableEvent extends ApplicationEvent {

    private String user;

    private DssDatamodelTable table;

    public UnBindLabelByTableEvent(Object source,String user,DssDatamodelTable table) {
        super(source);
        this.user = user;
        this.table = table;
    }
}