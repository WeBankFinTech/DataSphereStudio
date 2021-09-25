package com.webank.wedatasphere.warehouse.cqe;

import com.webank.wedatasphere.warehouse.cqe.common.PageCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DwThemeDomainQueryCommand extends PageCommand {
    private String name;
}
