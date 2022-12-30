package com.webank.wedatasphere.dss.datamodel.table.event;


import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UpdateBindModelByTableEvent extends ApplicationEvent {
    private String user;

    private DssDatamodelTable org;

    private DssDatamodelTable updateOne;

    public UpdateBindModelByTableEvent(Object source, String user, DssDatamodelTable org, DssDatamodelTable updateOne) {
        super(source);
        this.user = user;
        this.org = org;
        this.updateOne = updateOne;
    }
}
