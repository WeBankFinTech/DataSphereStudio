package com.webank.wedatasphere.dss.datamodel.table.event;


import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableColumns;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class UpdateBindModelByColumnsEvent extends ApplicationEvent {
    private String user;

    private String tableName;

    private List<DssDatamodelTableColumns> orgColumns;

    private List<DssDatamodelTableColumns> updateColumns;

    public UpdateBindModelByColumnsEvent(Object source, String user, String tableName, List<DssDatamodelTableColumns> orgColumns, List<DssDatamodelTableColumns> updateColumns) {
        super(source);
        this.user = user;
        this.tableName = tableName;
        this.orgColumns = orgColumns;
        this.updateColumns = updateColumns;
    }
}
