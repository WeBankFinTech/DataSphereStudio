package com.webank.wedatasphere.dss.datamodel.center.common.event;


import com.webank.wedatasphere.dss.data.governance.entity.ClassificationConstant;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class UnBindModelEvent extends ApplicationEvent {

    private String user;

    private String modelName;

    private ClassificationConstant modelType;

    private String tableName;

    private String guid;


    public UnBindModelEvent(Object source,String user,String guid,String tableName,String modelName,ClassificationConstant modelType) {
        super(source);
        this.user = user;
        this.guid = guid;
        this.tableName = tableName;
        this.modelName = modelName;
        this.modelType = modelType;
    }
}
