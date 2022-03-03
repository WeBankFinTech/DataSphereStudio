package com.webank.wedatasphere.dss.datamodel.table.listener;

import com.webank.wedatasphere.dss.datamodel.table.event.BindLabelByTableEvent;
import com.webank.wedatasphere.dss.datamodel.table.event.TableFirstBindEvent;
import com.webank.wedatasphere.dss.datamodel.table.service.TableMaterializedHistoryService;
import com.webank.wedatasphere.dss.datamodel.table.service.TableService;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
public class TableFirstBindListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TableFirstBindListener.class);

    @Resource
    private TableService tableService;

    @Resource
    private TableMaterializedHistoryService tableMaterializedHistoryService;


    @EventListener
    @Async("taskExecutor")
    public void firstBindTable(TableFirstBindEvent event) throws ErrorException, InterruptedException {
        int count = 0;
        do {
            LOGGER.info("table id : {}, tableName : {}, user : {}  wait 5s to bind ",event.getTableId(),event.getTableName(),event.getUser());
            TimeUnit.SECONDS.sleep(5);
            if (tableMaterializedHistoryService.tableExists(event.getTableName(), event.getUser())) {
                LOGGER.info("table id : {}, tableName : {}, user : {} exists bind",event.getTableId(),event.getTableName(),event.getUser());
                tableService.bind(event.getTableId(), event.getUser());
                count++;
                break;
            }
            count++;
        } while (count < 5);
        LOGGER.info("table id : {}, tableName : {}, user : {}  try bind {} times  ",event.getTableId(),event.getTableName(),event.getUser(),count);
    }
}
