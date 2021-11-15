package com.webank.wedatasphere.dss.datamodel.table.event;


import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableColumns;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class BindModelByColumnsEvent extends ApplicationEvent {

    private String user;

    private String tableName;

    List<DssDatamodelTableColumns> columns;

    public BindModelByColumnsEvent(Object source, String user, String tableName, List<DssDatamodelTableColumns> columns) {
        super(source);
        this.user = user;
        this.tableName = tableName;
        this.columns = columns;
    }
}
