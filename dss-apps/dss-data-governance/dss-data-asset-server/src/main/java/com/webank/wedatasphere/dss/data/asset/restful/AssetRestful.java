package com.webank.wedatasphere.dss.data.asset.restful;

import com.webank.wedatasphere.dss.data.asset.entity.HiveTblLabelInfo;
import com.webank.wedatasphere.dss.data.asset.service.AssetService;
import com.webank.wedatasphere.dss.data.asset.service.WorkspaceInfoService;
import org.apache.linkis.server.Message;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/dss/data/governance/asset", produces = {"application/json"})
@AllArgsConstructor
public class AssetRestful {
    private static final Logger logger = LoggerFactory.getLogger(AssetRestful.class);

    private AssetService assetService;
    private WorkspaceInfoService workspaceInfoService;

    /**
     * 获取数据资产概要：hivedb数、hivetable数据、总存储量
     */
    @RequestMapping(method = RequestMethod.GET, path ="/hiveSummary")
    public Message getHiveSummary() throws Exception {
        return Message.ok().data("result", assetService.getHiveSummary());
    }

    /**
     * 修改单个表或单个列注释
     */
    @RequestMapping(method = RequestMethod.PUT, path ="/comment/{guid}")
    public Message modifyComment(@PathVariable String guid, @RequestParam String comment) throws Exception {
        comment="\""+comment+"\"";
        assetService.modifyComment(guid,comment);
        return Message.ok().data("result","修改成功");
    }

    /**
     * 批量修改多个个表或列注释
     */
    @RequestMapping(method = RequestMethod.PUT, path ="/comment/bulk")
    public Message modifyComment(@RequestBody Map<String,String> commentMap) throws Exception {
        for (Map.Entry<String, String> stringStringEntry : commentMap.entrySet()) {
            stringStringEntry.setValue("\""+stringStringEntry.getValue()+"\"");
        }
        assetService.bulkModifyComment(commentMap);

        return Message.ok().data("result","修改成功");
    }


    /**
     * 设置单个表或单个列的标签
     */
    @RequestMapping(method = RequestMethod.POST, path ="/label/{guid}")
    public Message setLabels(@PathVariable String guid, @RequestBody HiveTblLabelInfo hiveTblLabelInfo) throws Exception {
        assetService.setLabels(guid,hiveTblLabelInfo.getLabels());

        return Message.ok().data("result","设置成功");
    }

    /**
     * 删除单个表或单个列的标签,linkis-gateway不支持DELETE方法
     */
    @RequestMapping(method = RequestMethod.PUT, path ="/label/{guid}")
    public Message removeLabels(@PathVariable String guid, @RequestBody HiveTblLabelInfo hiveTblLabelInfo) throws Exception {
        assetService.removeLabels(guid,hiveTblLabelInfo.getLabels());

        return Message.ok().data("result","删除成功");
    }

    /**
     * 获取工作空间下所有用户名
     */
    @RequestMapping(method = RequestMethod.GET, path ="getWorkspaceUsers/{workspaceId}/{search}")
    public Message getWorkspaceUsers(@PathVariable int workspaceId,@PathVariable String search) throws  Exception{
        String searchs="%"+search+"%";
        List<String> workspaceUsers = workspaceInfoService.getWorkspaceUsers(workspaceId,searchs);
        return  Message.ok().data("result",workspaceUsers);

    }
}
