package com.webank.wedatasphere.dss.datamodel.table.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TableFirstBindEvent extends ApplicationEvent {
    private String user;
    private Long tableId;
    private String tableName;

    public TableFirstBindEvent(Object source, String user, Long tableId, String tableName) {
        super(source);
        this.user = user;
        this.tableId = tableId;
        this.tableName = tableName;
    }
}
