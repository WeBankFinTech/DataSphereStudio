package com.webank.wedatasphere.dss.data.asset.restful;

import com.webank.wedatasphere.dss.data.asset.entity.HiveTblLabelInfo;
import com.webank.wedatasphere.dss.data.asset.service.AssetService;
import com.webank.wedatasphere.dss.data.asset.service.WorkspaceInfoService;
import com.webank.wedatasphere.linkis.server.Message;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Classname AssetRestful
 * @Description TODO
 * @Date 2021/8/19 13:53
 * @Created by suyc
 */
@Path("/dss/data/governance/asset")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Component
@AllArgsConstructor
public class AssetRestful {
    private static final Logger logger = LoggerFactory.getLogger(AssetRestful.class);

    private AssetService assetService;
    private WorkspaceInfoService workspaceInfoService;

    /**
     * 获取数据资产概要：hivedb数、hivetable数据、总存储量
     */
    @GET
    @Path("/hiveSummary")
    public Response getHiveSummary(@Context HttpServletRequest req) throws Exception {
        return Message.messageToResponse(Message.ok().data("result", assetService.getHiveSummary()));
    }

    /**
     * 修改单个表或单个列注释
     */
    @PUT
    @Path("/comment/{guid}")
    public Response modifyComment(@PathParam("guid") String guid,@QueryParam("comment") String comment) throws Exception {
        comment="\""+comment+"\"";
        assetService.modifyComment(guid,comment);
        return Message.messageToResponse(Message.ok().data("result","修改成功"));
    }

    /**
     * 批量修改多个个表或列注释
     */
    @PUT
    @Path("/comment/bulk")
    public Response modifyComment(@RequestBody Map<String,String> commentMap) throws Exception {
        for (Map.Entry<String, String> stringStringEntry : commentMap.entrySet()) {
            stringStringEntry.setValue("\""+stringStringEntry.getValue()+"\"");
        }
        assetService.bulkModifyComment(commentMap);

        return Message.messageToResponse(Message.ok().data("result","修改成功"));
    }


    /**
     * 设置单个表或单个列的标签
     */
    @POST
    @Path("/label/{guid}")
    public Response setLabels(@PathParam("guid") String guid, @RequestBody HiveTblLabelInfo hiveTblLabelInfo) throws Exception {
        assetService.setLabels(guid,hiveTblLabelInfo.getLabels());

        return Message.messageToResponse(Message.ok().data("result","设置成功"));
    }

    /**
     * 删除单个表或单个列的标签,linkis-gateway不支持DELETE方法
     */
    @PUT
    @Path("/label/{guid}")
    public Response removeLabels(@PathParam("guid") String guid, @RequestBody HiveTblLabelInfo hiveTblLabelInfo) throws Exception {
        assetService.removeLabels(guid,hiveTblLabelInfo.getLabels());

        return Message.messageToResponse(Message.ok().data("result","删除成功"));
    }

    /**
     * 获取工作空间下所有用户名
     */
    @GET
    @Path("getWorkspaceUsers/{workspaceId}/{search}")
    public Response getWorkspaceUsers(@PathParam("workspaceId") int workspaceId,@PathParam("search")  String search) throws  Exception{
        String searchs="%"+search+"%";
        List<String> workspaceUsers = workspaceInfoService.getWorkspaceUsers(workspaceId,searchs);
        return  Message.messageToResponse(Message.ok().data("result",workspaceUsers));

    }
}
