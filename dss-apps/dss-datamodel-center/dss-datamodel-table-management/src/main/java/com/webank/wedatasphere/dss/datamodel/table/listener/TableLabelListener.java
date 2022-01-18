package com.webank.wedatasphere.dss.datamodel.table.listener;

import com.webank.wedatasphere.dss.datamodel.center.common.constant.LabelConstant;
import com.webank.wedatasphere.dss.datamodel.center.common.event.BindLabelEvent;
import com.webank.wedatasphere.dss.datamodel.center.common.event.UnBindLabelEvent;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;
import com.webank.wedatasphere.dss.datamodel.table.event.BindLabelByTableEvent;
import com.webank.wedatasphere.dss.datamodel.table.event.UnBindLabelByTableEvent;
import com.webank.wedatasphere.dss.datamodel.table.event.UpdateBindLabelByTableEvent;
import jersey.repackaged.com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TableLabelListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TableLabelListener.class);

    public static final String LABEL_SEPARATOR = LabelConstant.SEPARATOR;

    @Resource
    private ApplicationEventPublisher publisher;

    @EventListener
    @Async("taskExecutor")
    public void bindLabelByTable(BindLabelByTableEvent event){
        if (preEvent(event.getTable())) return;
        String labels = event.getTable().getLabel();
        String[] binds = StringUtils.split(labels,LABEL_SEPARATOR);

        Arrays.stream(binds).forEach(bind->{
            publishBind(event.getUser(),bind,event.getTable().getName());
        });
    }

    private boolean preEvent(DssDatamodelTable table) {
        return StringUtils.isBlank(table.getLabel());
    }

    private void publishBind(String user,String labelName,String tableName){
        publisher.publishEvent(new BindLabelEvent(this,user,labelName,tableName));
    }

    private void publishUnBind(String user,String labelName,String tableName){
        publisher.publishEvent(new UnBindLabelEvent(this,user,labelName,tableName));
    }

    @EventListener
    @Async("taskExecutor")
    public void unBindLabelByTable(UnBindLabelByTableEvent event){
        if (preEvent(event.getTable())) return;
        String labels = event.getTable().getLabel();
        String[] unBinds = StringUtils.split(labels,LABEL_SEPARATOR);

        Arrays.stream(unBinds).forEach(bind->{
            publishBind(event.getUser(),bind,event.getTable().getName());
        });
    }

    @EventListener
    @Async("taskExecutor")
    public void updateBindLabelByTable(UpdateBindLabelByTableEvent event){
        String tableName = event.getOri().getName();
        String bindLabels = event.getUpdateOne().getLabel();
        String unBindLabels = event.getOri().getLabel();
        Set<String> bindSets = Sets.newHashSet();
        Set<String> unBindSets = Sets.newHashSet();
        if (StringUtils.isNotBlank(bindLabels)){
            bindSets.addAll(Arrays.stream(StringUtils.split(bindLabels,LABEL_SEPARATOR)).collect(Collectors.toSet()));
        }
        if (StringUtils.isNotBlank(unBindLabels)){
            unBindSets.addAll(Arrays.stream(StringUtils.split(unBindLabels,LABEL_SEPARATOR)).collect(Collectors.toSet()));
        }

        Set<String> willBinds = bindSets.stream().filter(bind->!unBindSets.contains(bind)).collect(Collectors.toSet());
        Set<String> willUnBinds = unBindSets.stream().filter(unBind->!bindLabels.contains(unBind)).collect(Collectors.toSet());
        LOGGER.info("bind labels : {}",willBinds);
        willBinds.forEach(bind->publishBind(event.getUser(),bind,tableName));
        LOGGER.info("unBind labels : {}",willUnBinds);
        willUnBinds.forEach(unBind->publishUnBind(event.getUser(),unBind,tableName));
    }
}
