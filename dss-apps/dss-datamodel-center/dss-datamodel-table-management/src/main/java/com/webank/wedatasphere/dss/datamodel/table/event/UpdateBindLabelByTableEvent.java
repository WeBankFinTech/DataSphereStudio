package com.webank.wedatasphere.dss.datamodel.table.event;

import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@ToString
@Getter
public class UpdateBindLabelByTableEvent extends ApplicationEvent {
    private String user;

    private DssDatamodelTable ori;

    private DssDatamodelTable updateOne;

    public UpdateBindLabelByTableEvent(Object source, String user, DssDatamodelTable ori, DssDatamodelTable updateOne) {
        super(source);
        this.user = user;
        this.ori = ori;
        this.updateOne = updateOne;
    }
}
