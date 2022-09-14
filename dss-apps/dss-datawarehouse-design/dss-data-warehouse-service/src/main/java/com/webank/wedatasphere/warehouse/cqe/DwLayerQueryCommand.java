package com.webank.wedatasphere.warehouse.cqe;

import com.webank.wedatasphere.warehouse.cqe.common.PageCommand;
import lombok.*;

@Setter
@Getter
@ToString
//@Builder
//@Accessors(chain = true)
public class DwLayerQueryCommand extends PageCommand {
    private String name;
    private Boolean enabled;
}
