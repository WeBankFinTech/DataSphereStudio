package com.webank.wedatasphere.dss.datamodel.center.common.event;


import com.webank.wedatasphere.dss.data.governance.entity.ClassificationConstant;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class UpdateModelEvent extends ApplicationEvent {

    private String name;

    private ClassificationConstant type;

    private String user;

    private String orgName;


    public UpdateModelEvent(Object source,String user,String name,String orgName, ClassificationConstant type) {
        super(source);
        this.user = user;
        this.type = type;
        this.name = name;
        this.orgName = orgName;
    }
}
