package com.webank.wedatasphere.dss.datamodel.center.common.event;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class UnBindLabelEvent extends ApplicationEvent {

    private String user;

    private String label;

    private String tableName;

    private String labelGuid;

    private String tableGuid;

    private String relationGuid;

    public UnBindLabelEvent(Object source, String user, String label, String tableName, String labelGuid, String tableGuid, String relationGuid) {
        super(source);
        this.user = user;
        this.label = label;
        this.tableName = tableName;
        this.labelGuid = labelGuid;
        this.tableGuid = tableGuid;
        this.relationGuid = relationGuid;
    }

    public UnBindLabelEvent(Object source, String user, String label, String tableName) {
        super(source);
        this.user = user;
        this.label = label;
        this.tableName = tableName;
    }
}
