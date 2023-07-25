package com.webank.wedatasphere.dss.scriptis.restful;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import com.webank.wedatasphere.dss.scriptis.pojo.entity.DssScriptDownloadAudit;
import com.webank.wedatasphere.dss.scriptis.service.DssScriptDownloadService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/dss/scriptis/audit", produces = {"application/json"})
public class ScriptisResultSetAuditController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptisResultSetAuditController.class);
    @Autowired
    private DssScriptDownloadService dssScriptDownloadService;

    @RequestMapping(path = "download/save", method = RequestMethod.POST)
    public Message saveScriptDownload(@RequestBody @Valid DssScriptDownloadAudit dssScriptDownloadAudit, HttpServletRequest request) {
        String userName = SecurityFilter.getLoginUsername(request);
        dssScriptDownloadAudit.setCreator(userName);
        String sql = dssScriptDownloadAudit.getSql();
        if (sql.length() > 2000) {
            sql = sql.substring(0, 2000);
            dssScriptDownloadAudit.setSql(sql);
        }
        LOGGER.info("user {} try to saveScriptDownload, params:{}", userName, dssScriptDownloadAudit);
        dssScriptDownloadService.save(dssScriptDownloadAudit);
        return Message.ok("保存成功！");
    }

    @RequestMapping(path = "download/query", method = RequestMethod.GET)
    public Message getScriptDownload(HttpServletRequest req,
            @RequestParam(required = false) String userName, @RequestParam(required = false) String startTime,
                                     @RequestParam(required = false) String endTime, @RequestParam(required = false) Integer pn) {
        if(pn == null || pn < 1) {
            pn = 1;
        }
        String loginUser = SecurityFilter.getLoginUsername(req);
        if(!ArrayUtils.contains(DSSCommonConf.SUPER_ADMIN_LIST, loginUser)) {
            return Message.error("Only super admin can query the download history of user " + userName);
        }
        PageHelper.startPage(pn, 10, true);
        LOGGER.info("user {} try to queryScriptDownload, query userName: {}, startTime:{}, endTime:{}, page:{}", loginUser, userName, startTime, endTime, pn);
        List<DssScriptDownloadAudit> userPage = dssScriptDownloadService.getDownloadAuditList(userName, startTime, endTime);
        PageInfo<DssScriptDownloadAudit> pageInfo = new PageInfo<>(userPage);
        return Message.ok().data("data", pageInfo);
    }
}


