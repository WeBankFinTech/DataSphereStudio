package com.webank.wedatasphere.dss.datamodel.center.common.event;


import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class DeleteLabelEvent extends ApplicationEvent {
    private String user;

    private String name;

    public DeleteLabelEvent(Object source, String user, String name) {
        super(source);
        this.user = user;
        this.name = name;
    }
}
