package com.webank.wedatasphere.dss.datamodel.table.event;


import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class UnBindModelByTableEvent  extends ApplicationEvent {

    private String user;

    private DssDatamodelTable table;

    public UnBindModelByTableEvent(Object source,String user,DssDatamodelTable table) {
        super(source);
        this.user = user;
        this.table = table;
    }
}