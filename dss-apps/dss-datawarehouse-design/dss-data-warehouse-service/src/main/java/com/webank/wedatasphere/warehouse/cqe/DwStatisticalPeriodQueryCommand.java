package com.webank.wedatasphere.warehouse.cqe;

import com.webank.wedatasphere.warehouse.cqe.common.PageCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DwStatisticalPeriodQueryCommand extends PageCommand {
    private String name;
    private Boolean enabled;
    private String theme;
    private String layer;
}