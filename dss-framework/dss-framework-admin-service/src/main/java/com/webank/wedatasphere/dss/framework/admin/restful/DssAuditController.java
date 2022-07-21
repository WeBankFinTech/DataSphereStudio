package com.webank.wedatasphere.dss.framework.admin.restful;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssScriptDownloadAudit;
import com.webank.wedatasphere.dss.framework.admin.service.DssScriptDownloadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.linkis.server.Message;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @Auther: Han Tang
 * @Date: 2022/1/11-01-11-14:20
 */


@RestController
@RequestMapping(path = "/dss/framework/admin/audit", produces = {"application/json"})
@Slf4j
public class DssAuditController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DssAuditController.class);
    @Autowired
    DssScriptDownloadService dssScriptDownloadService;

    @RequestMapping(path = "script/download/save", method = RequestMethod.POST)
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
        return Message.ok();
    }

    @RequestMapping(path = "script/download/query", method = RequestMethod.GET)
    public Message getScriptDownload(@RequestParam(required = false) String userName, @RequestParam(required = false) String startTime,
                                     @RequestParam(required = false) String endTime, @RequestParam Integer pn) {
        PageHelper.startPage(pn, 10, true);
        LOGGER.info("user {} try to queryScriptDownload, startTime:{}, endTime:{}, page:{}", userName, startTime, endTime, pn);
        List<DssScriptDownloadAudit> userPage = dssScriptDownloadService.getDownloadAuditList(userName, startTime, endTime);
        PageInfo<DssScriptDownloadAudit> pageInfo = new PageInfo<>(userPage);
        return Message.ok().data("data", pageInfo);
    }
}


