package com.webank.wedatasphere.dss.datamodel.center.common.event;


import com.webank.wedatasphere.dss.data.governance.entity.ClassificationConstant;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;
@Getter
@ToString
public class CreateModelEvent extends ApplicationEvent {

    private String user;

    private ClassificationConstant type;

    private String name;

    public CreateModelEvent(Object source,String user, String name, ClassificationConstant type) {
        super(source);
        this.type = type;
        this.name = name;
        this.user = user;
    }
}
