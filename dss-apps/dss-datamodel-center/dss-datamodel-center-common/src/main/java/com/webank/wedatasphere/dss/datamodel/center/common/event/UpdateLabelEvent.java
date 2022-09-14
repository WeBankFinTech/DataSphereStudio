package com.webank.wedatasphere.dss.datamodel.center.common.event;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class UpdateLabelEvent extends ApplicationEvent {
    private String user;

    private String name;

    private String oriName;

    public UpdateLabelEvent(Object source, String user, String name, String oriName) {
        super(source);
        this.user = user;
        this.name = name;
        this.oriName = oriName;
    }
}
