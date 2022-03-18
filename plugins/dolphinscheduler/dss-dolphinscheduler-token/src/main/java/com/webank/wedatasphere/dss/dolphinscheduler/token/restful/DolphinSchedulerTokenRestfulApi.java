package com.webank.wedatasphere.dss.dolphinscheduler.token.restful;

import com.webank.wedatasphere.dss.appconn.core.ext.OptionalAppConn;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.appconn.scheduler.SchedulerAppConn;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRefImpl;
import com.webank.wedatasphere.dss.standard.app.structure.optional.OptionalOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author enjoyyin
 * @date 2022-03-18
 * @since 0.5.0
 */
@RequestMapping(path = "/dss/framework/project", produces = {"application/json"})
@RestController
public class DolphinSchedulerTokenRestfulApi {

    @RequestMapping(path ="/ds/token", method = RequestMethod.GET)
    public Message dsApiServiceTokenCreate(HttpServletRequest req) {
        String userName = SecurityFilter.getLoginUsername(req);
        SchedulerAppConn schedulerAppConn = AppConnManager.getAppConnManager().getAppConn(SchedulerAppConn.class);
        if("dolphinscheduler".equals(schedulerAppConn.getAppDesc().getAppName().toLowerCase())) {
            return Message.error("Ask for a token failed, the integrated Scheduler is not DolphinScheduler.");
        }
        AppInstance appInstance = schedulerAppConn.getAppDesc().getAppInstances().get(0);
        OptionalOperation optionalOperation = ((OptionalAppConn) schedulerAppConn).getOrCreateOptionalStandard()
                .getOptionalService(appInstance).getOptionalOperation("getToken");
        ResponseRef responseRef = optionalOperation.apply(new StructureRequestRefImpl().setUserName(userName));
        return Message.ok().data("token", responseRef.getValue("token"))
                .data("expire_time", Long.valueOf((String)responseRef.getValue("expireTime")));
    }
}
