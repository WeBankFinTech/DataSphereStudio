package com.webank.wedatasphere.dss.datamodel.table.event;


import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class BindLabelByTableEvent  extends ApplicationEvent {

    private String user;

    private DssDatamodelTable table;

    public BindLabelByTableEvent(Object source,String user,DssDatamodelTable table) {
        super(source);
        this.user = user;
        this.table = table;
    }
}
