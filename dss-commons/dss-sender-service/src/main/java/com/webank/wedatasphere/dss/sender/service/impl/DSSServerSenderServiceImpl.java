package com.webank.wedatasphere.dss.sender.service.impl;

import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.sender.service.DSSSenderService;
import com.webank.wedatasphere.dss.sender.service.conf.DSSSenderServiceConf;
import org.apache.linkis.rpc.Sender;

import java.util.List;

public class DSSServerSenderServiceImpl implements DSSSenderService {
    private final Sender dssServerSender = Sender.getSender(DSSSenderServiceConf.DSS_SERVER_NAME.getValue());

    @Override
    public Sender getOrcSender() {
        return dssServerSender;
    }

    @Override
    public Sender getOrcSender(List<DSSLabel> dssLabels) {
        return dssServerSender;
    }

    @Override
    public Sender getScheduleOrcSender() {
        return dssServerSender;
    }

    @Override
    public Sender getWorkflowSender(List<DSSLabel> dssLabels) {
        return dssServerSender;
    }

    @Override
    public Sender getWorkflowSender() {
        return dssServerSender;
    }

    @Override
    public Sender getSchedulerWorkflowSender() {
        return dssServerSender;
    }

    @Override
    public Sender getProjectServerSender() {
        return dssServerSender;
    }

    @Override
    public Sender getGitSender() {
        return dssServerSender;
    }
}
