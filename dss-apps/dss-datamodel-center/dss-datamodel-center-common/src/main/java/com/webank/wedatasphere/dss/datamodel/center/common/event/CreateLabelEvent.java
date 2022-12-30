package com.webank.wedatasphere.dss.datamodel.center.common.event;


import com.webank.wedatasphere.dss.data.governance.entity.ClassificationConstant;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class CreateLabelEvent extends ApplicationEvent {

    private String user;

    private String name;

    public CreateLabelEvent(Object source, String user, String name) {
        super(source);
        this.user = user;
        this.name = name;
    }
}
