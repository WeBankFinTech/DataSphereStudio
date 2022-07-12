package com.webank.wedatasphere.dss.data.api.server.restful;

import com.webank.wedatasphere.dss.data.api.server.entity.request.CallMonitorResquest;
import com.webank.wedatasphere.dss.data.api.server.entity.request.SingleCallMonitorRequest;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiInfo;
import com.webank.wedatasphere.dss.data.api.server.service.ApiManagerService;
import com.webank.wedatasphere.dss.data.api.server.service.ApiMonitorService;
import com.webank.wedatasphere.dss.data.api.server.util.TimeUtil;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/dss/data/api/apimonitor", produces = {"application/json"})
public class DSSDbApiMonitorRestful {
    public static final Logger LOGGER = LoggerFactory.getLogger(DSSDbApiMonitorRestful.class);

    @Autowired
    private ApiMonitorService apiMonitorService;
    @Autowired
    private ApiManagerService apiManagerService;

    @RequestMapping(path = "list", method = RequestMethod.GET)
    public Message getApiList(HttpServletRequest request,
                              @RequestParam(value = "workspaceId", required = false) Long workspaceId, @RequestParam(value = "apiName", required = false) String apiName,
                              @RequestParam("pageNow") Integer pageNow, @RequestParam("pageSize") Integer pageSize) {
        if (pageNow == null) {
            pageNow = 1;
        }
        if (pageSize == null) {
            pageSize = 20;
        }
        Workspace workspace = SSOHelper.getWorkspace(request);
        LOGGER.info("workspace is: {}", workspace.getWorkspaceName());
        List<Long> totals = new ArrayList<>();
        List<ApiInfo> apiInfoList = apiManagerService.getOnlineApiInfoList(Long.valueOf(workspace.getWorkspaceName()),
                apiName, totals, pageNow, pageSize);
        return Message.ok().data("list", apiInfoList).data("total", totals.get(0));
    }


    @RequestMapping(path = "onlineApiCnt", method = RequestMethod.GET)
    public Message getOnlineApiCnt(HttpServletRequest request, @RequestParam(value = "workspaceId", required = false) Long workspaceId) {
        Workspace workspace = SSOHelper.getWorkspace(request);
        LOGGER.info("workspace is: {}", workspace.getWorkspaceName());
        return Message.ok().data("onlineApiCnt", apiMonitorService.getOnlineApiCnt(Long.valueOf(workspace.getWorkspaceName())));
    }


    @RequestMapping(path = "offlineApiCnt", method = RequestMethod.GET)
    public Message getOfflineApiCnt(HttpServletRequest request, @RequestParam(value = "workspaceId", required = false) Long workspaceId) {
        Workspace workspace = SSOHelper.getWorkspace(request);
        LOGGER.info("workspace is: {}", workspace.getWorkspaceName());
        return Message.ok().data("offlineApiCnt", apiMonitorService.getOfflineApiCnt(Long.valueOf(workspace.getWorkspaceName())));
    }


    @RequestMapping(path = "callTotalCnt", method = RequestMethod.GET)
    public Message getCallTotalCnt(@BeanParam CallMonitorResquest callMonitorResquest) {
        return Message.ok().data("callTotalCnt", apiMonitorService.getCallTotalCnt(callMonitorResquest));
    }


    @RequestMapping(path = "callTotalTime", method = RequestMethod.GET)
    public Message getCallTotalTime(@BeanParam CallMonitorResquest callMonitorResquest) {
        return Message.ok().data("callTotalTime", apiMonitorService.getCallTotalTime(callMonitorResquest));
    }


    @RequestMapping(path = "callListByCnt", method = RequestMethod.GET)
    public Message getCallListByCnt(@BeanParam CallMonitorResquest callMonitorResquest) {
        return Message.ok().data("list", apiMonitorService.getCallListByCnt(callMonitorResquest));
    }


    @RequestMapping(path = "callListByFailRate", method = RequestMethod.GET)
    public Message getCallListByFailRate(@BeanParam CallMonitorResquest callMonitorResquest) {
        return Message.ok().data("list", apiMonitorService.getCallListByFailRate(callMonitorResquest));
    }

    /**
     * 过去24小时内每小时请求次数（平均QPS）
     */

    @RequestMapping(path = "callCntForPast24H", method = RequestMethod.GET)
    public Message getCallCntForPast24H(HttpServletRequest request, @RequestParam(value = "workspaceId", required = false) Long workspaceId)
            throws Exception {
        Workspace workspace = SSOHelper.getWorkspace(request);
        LOGGER.info("workspace is: {}", workspace.getWorkspaceName());
        return Message.ok().data("list", apiMonitorService.getCallCntForPast24H(Long.valueOf(workspace.getWorkspaceName())));
    }

    /**
     * 单个API每小时的平均响应时间
     */

    @RequestMapping(path = "callTimeForSinleApi", method = RequestMethod.GET)
    public Message getCallTimeForSinleApi(@BeanParam SingleCallMonitorRequest singleCallMonitorRequest) throws Exception {
        long hourCnt = TimeUtil.getHourCnt(singleCallMonitorRequest.getStartTime(), singleCallMonitorRequest.getEndTime());
        singleCallMonitorRequest.setHourCnt(hourCnt);

        return Message.ok().data("list", apiMonitorService.getCallTimeForSinleApi(singleCallMonitorRequest));
    }

    /**
     * 单个API每小时的调用次数
     */

    @RequestMapping(path = "callCntForSinleApi", method = RequestMethod.GET)
    public Message getCallCntForSinleApi(@BeanParam SingleCallMonitorRequest singleCallMonitorRequest) throws Exception {
        long hourCnt = TimeUtil.getHourCnt(singleCallMonitorRequest.getStartTime(), singleCallMonitorRequest.getEndTime());
        singleCallMonitorRequest.setHourCnt(hourCnt);

        return Message.ok().data("list", apiMonitorService.getCallCntForSinleApi(singleCallMonitorRequest));
    }
}
