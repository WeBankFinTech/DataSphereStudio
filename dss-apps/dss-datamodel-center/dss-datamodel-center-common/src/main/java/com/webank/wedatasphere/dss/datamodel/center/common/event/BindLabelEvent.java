package com.webank.wedatasphere.dss.datamodel.center.common.event;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class BindLabelEvent extends ApplicationEvent {

    private String user;

    private String label;

    private String tableName;

    private String labelGuid;

    private String tableGuid;

    public BindLabelEvent(Object source, String user, String label, String tableName, String labelGuid, String tableGuid) {
        super(source);
        this.user = user;
        this.label = label;
        this.tableName = tableName;
        this.labelGuid = labelGuid;
        this.tableGuid = tableGuid;
    }

    public BindLabelEvent(Object source, String user, String label, String tableName) {
        super(source);
        this.user = user;
        this.label = label;
        this.tableName = tableName;
    }
}
