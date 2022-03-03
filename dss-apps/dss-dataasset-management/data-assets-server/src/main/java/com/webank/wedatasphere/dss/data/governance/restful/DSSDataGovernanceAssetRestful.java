package com.webank.wedatasphere.dss.data.governance.restful;

import com.webank.wedatasphere.dss.data.governance.entity.*;
import com.webank.wedatasphere.dss.data.governance.service.AssetService;
import com.webank.wedatasphere.dss.data.governance.service.AuthenticationClientStrategy;
import com.webank.wedatasphere.dss.data.governance.service.WorkspaceInfoService;
import com.webank.wedatasphere.dss.data.governance.vo.*;
import com.webank.wedatasphere.dss.framework.workspace.client.impl.LinkisWorkSpaceRemoteClient;
import com.webank.wedatasphere.dss.framework.workspace.client.request.GetWorkspaceUsersAction;
import com.webank.wedatasphere.dss.framework.workspace.client.response.GetWorkspaceUsersResult;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.server.Message;
import org.apache.atlas.model.lineage.AtlasLineageInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Path("/data-assets/asset")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Component
public class DSSDataGovernanceAssetRestful implements AuthenticationClientStrategy {
    private static final Logger logger = LoggerFactory.getLogger(DSSDataGovernanceAssetRestful.class);

    private static final String DEFAULT_DIRECTION = "BOTH";
    private static final String DEFAULT_DEPTH = "3";
    private static final String DEFAULT_LIMIT = "25";
    private static final String DEFAULT_OFFSET = "0";

    @Autowired
    private AssetService assetService;
    @Autowired
    private WorkspaceInfoService workspaceInfoService;

    @Resource
    private LinkisWorkSpaceRemoteClient linkisWorkSpaceRemoteClient;

    /**
     * 获取数据资产概要：hivedb数、hivetable数据、总存储量
     */
    @GET
    @Path("/hiveSummary")
    public Response getHiveSummary(@Context HttpServletRequest req) throws Exception {

        return Message.messageToResponse(Message.ok().data("result", assetService.getHiveSummary()));
    }

    /**
     * 搜索hive表
     */
    @GET
    @Path("/hiveTbl/search")
    public Response searchHiveTbl(@QueryParam("classification") String classification,
                                  @QueryParam("query") String query,
                                  @QueryParam("label") String label,
                                  @QueryParam("type") String type,
                                  @QueryParam("precise")@DefaultValue("0") int precise,
                                  @QueryParam("owner") @DefaultValue("") String owner,
                                  @QueryParam("limit") @DefaultValue(DEFAULT_LIMIT) int limit,
                                  @QueryParam("offset") @DefaultValue(DEFAULT_OFFSET) int offset) throws Exception {

        //适配模型
        if (ClassificationConstant.isTypeScope(type)) {
            if (StringUtils.isNotBlank(classification)) {
                classification = ClassificationConstant.getPrefix(type).orElse(null) + classification;
            } else {
                classification = ClassificationConstant.getRoot(type).orElse(null);
            }
        }
        //适配标签
        if (!StringUtils.isBlank(label)){
            label = GlossaryConstant.LABEL.formatQuery(label);
        }
        //判断是否精确查询
        if (QueryType.PRECISE.getCode()!=precise){
            query = "*" + query + "*";
        }
        List<HiveTblSimpleInfo> hiveTblBasicList = assetService.searchHiveTable(classification, query,label,limit, offset);
        if (StringUtils.isBlank(owner) || owner.equals("undefined")|| CollectionUtils.isEmpty(hiveTblBasicList)) {
            return Message.messageToResponse(Message.ok().data("result", hiveTblBasicList));
        } else {
            List<HiveTblSimpleInfo> res = new ArrayList<>();
            for (HiveTblSimpleInfo hiveTblSimpleInfo : hiveTblBasicList) {
                if (hiveTblSimpleInfo.getOwner().equals(owner)) {
                    res.add(hiveTblSimpleInfo);
                }
            }
            return Message.messageToResponse(Message.ok().data("result", res));
        }
    }

    /**
     * 搜索hive表统计信息
     */
    @GET
    @Path("/hiveTbl/stats")
    public Response searchHiveTblStats(@QueryParam("dbName") String dbName,
                                       @QueryParam("tableName") String tableName,
                                       @QueryParam("guid") String guid) throws Exception {
        logger.info("searchHiveTblStats dbName : {}, tableName : {}, guid : {}", dbName, tableName, guid);
        return Message.messageToResponse(Message.ok().data("result", assetService.hiveTblStats(dbName, tableName, guid)));

    }

    /**
     * 搜索hive表容量
     */
    @GET
    @Path("/hiveTbl/size")
    public Response searchHiveTblSize(@QueryParam("dbName") String dbName,
                                      @QueryParam("tableName") String tableName,
                                      @QueryParam("guid") String guid) throws Exception {
        logger.info("searchHiveTblSize dbName : {}, tableName : {}, guid : {}", dbName, tableName, guid);
        return Message.messageToResponse(Message.ok().data("result", assetService.hiveTblSize(dbName, tableName, guid)));

    }


    /**
     * 搜索hive库
     */
    @GET
    @Path("/hiveDb/search")
    public Response searchHiveDb(@QueryParam("classification") String classification,
                                 @QueryParam("query") String query,
                                 @QueryParam("owner") @DefaultValue("") String owner,
                                 @QueryParam("limit") @DefaultValue(DEFAULT_LIMIT) int limit,
                                 @QueryParam("offset") @DefaultValue(DEFAULT_OFFSET) int offset) throws Exception {

        List<HiveTblSimpleInfo> hiveTblBasicList = assetService.searchHiveDb(classification, '*' + query + '*', limit, offset);
        if (StringUtils.isBlank(owner) || owner.equals("undefined")) {
            return Message.messageToResponse(Message.ok().data("result", hiveTblBasicList));
        } else {
            List<HiveTblSimpleInfo> res = new ArrayList<>();
            for (HiveTblSimpleInfo hiveTblSimpleInfo : hiveTblBasicList) {
                if (hiveTblSimpleInfo.getOwner().equals(owner)) {
                    res.add(hiveTblSimpleInfo);
                }
            }
            return Message.messageToResponse(Message.ok().data("result", res));
        }
    }

    /**
     * 获取单个表的详细信息，包括：基本信息、字段信息
     */
    @GET
    @Path("/hiveTbl/{guid}/basic")
    public Response getHiveTblBasic(@PathParam("guid") String guid) throws Exception {
        return Message.messageToResponse(Message.ok().data("result", assetService.getHiveTblDetail(guid)));
    }

    /**
     * 获取表分区信息
     */
    @GET
    @Path("/hiveTbl/{guid}/partition")
    public Response getHiveTblPartition(@PathParam("guid") String guid) throws Exception {
        List<PartInfo> hiveTblPartition = assetService.getHiveTblPartition(guid);
        if (hiveTblPartition.size() > 0) {
            return Message.messageToResponse(Message.ok().data("result", hiveTblPartition));
        } else {
            return Message.messageToResponse(Message.ok().data("result", null));
        }
    }

    /**
     * 根据表名获取表分区信息
     */
    @GET
    @Path("/hiveTbl/partition/name")
    public Response getHiveTblPartitionByName(@QueryParam("dbName") String dbName,@QueryParam("tableName") String tableName) throws Exception {
        logger.info("getHiveTblPartitionByName  dbName : {}, tableName : {}", dbName, tableName);
        List<PartInfo> hiveTblPartition = assetService.getHiveTblPartitionByName(dbName,tableName);
        return Message.messageToResponse(Message.ok().data("result", hiveTblPartition));
    }

    /**
     * 获取表的血缘信息
     */
    @GET
    @Path("/hiveTbl/{guid}/lineage")
    public Response getHiveTblLineage(@PathParam("guid") String guid,
                                      @QueryParam("direction") @DefaultValue(DEFAULT_DIRECTION) AtlasLineageInfo.LineageDirection direction,
                                      @QueryParam("depth") @DefaultValue(DEFAULT_DEPTH) int depth) throws Exception {
        return Message.messageToResponse(Message.ok().data("result", assetService.getHiveTblLineage(guid, direction, depth)));
    }

    /**
     * 获取表的select语句
     */
    @GET
    @Path("/hiveTbl/{guid}/select")
    public Response getHiveTblSelect(@PathParam("guid") String guid) throws Exception {


        return Message.messageToResponse(Message.ok().data("result", assetService.getTbSelect(guid)));

    }

    /**
     * 获取表的create语句
     */
    @GET
    @Path("/hiveTbl/{guid}/create")
    public Response getHiveTblCreate(@PathParam("guid") String guid) throws Exception {
        return Message.messageToResponse(Message.ok().data("result", assetService.getTbCreate(guid)));

    }

    /**
     * 获取存储量前10的表信息
     */
    @GET
    @Path("/hiveTbl/topStorage")
    public Response getTop10Storage(@Context HttpServletRequest req) throws Exception {
        List<TableInfo> top10Table = assetService.getTop10Table();
        return Message.messageToResponse(Message.ok().data("result", top10Table));
    }

    /**
     * 修改单个表或单个列注释
     */
    @PUT
    @Path("/comment/{guid}")
    public Response modifyComment(@PathParam("guid") String guid, @QueryParam("comment") String comment) throws Exception {
        comment = "\"" + comment + "\"";
        assetService.modifyComment(guid, comment);
        return Message.messageToResponse(Message.ok().data("result", "修改成功"));
    }

    /**
     * 批量修改多个个表或列注释
     */
    @PUT
    @Path("/comment/bulk")
    public Response modifyComment(@RequestBody Map<String, String> commentMap) throws Exception {
        for (Map.Entry<String, String> stringStringEntry : commentMap.entrySet()) {
            stringStringEntry.setValue("\"" + stringStringEntry.getValue() + "\"");
        }
        assetService.bulkModifyComment(commentMap);

        return Message.messageToResponse(Message.ok().data("result", "修改成功"));
    }

    /**
     * 创建模型
     *
     * @param req
     * @param vo
     * @return
     * @throws Exception
     */
    @POST
    @Path("/model/type")
    public Response createModelType(@Context HttpServletRequest req, @RequestBody CreateModelTypeVO vo) throws Exception {
        logger.info("createModelType : {}", vo);
        return Message.messageToResponse(Message.ok().data("result", assetService.createModelType(vo)));
    }

    /**
     * 删除模型
     *
     * @param req
     * @param vo
     * @return
     * @throws Exception
     */
    @POST
    @Path("/model/type/delete")
    public Response deleteModelType(@Context HttpServletRequest req, @RequestBody DeleteModelTypeVO vo) throws Exception {
        logger.info("deleteModelTypeVO : {}", vo);
        assetService.deleteModelType(vo);
        return Message.messageToResponse(Message.ok().data("result", "删除成功"));
    }


    /**
     * 绑定模型
     *
     * @param req
     * @param vo
     * @return
     * @throws Exception
     */
    @POST
    @Path("/model/bind")
    public Response bindModelType(@Context HttpServletRequest req, @RequestBody BindModelVO vo) throws Exception {
        logger.info("bindModelVO : {}", vo);
        assetService.bindModelType(vo);
        return Message.messageToResponse(Message.ok().data("result", "绑定成功"));
    }

    /**
     * 解绑模型
     *
     * @param req
     * @param vo
     * @return
     * @throws Exception
     */
    @POST
    @Path("/model/unbind")
    public Response unBindModelType(@Context HttpServletRequest req, @RequestBody UnBindModelVO vo) throws Exception {
        logger.info("unBindModelVO : {}", vo);
        assetService.unBindModel(vo);
        return Message.messageToResponse(Message.ok().data("result", "解绑成功"));
    }


    /**
     * 更新模型
     *
     * @param req
     * @param vo
     * @return
     * @throws Exception
     */
    @POST
    @Path("/model/type/modify")
    public Response updateModelType(@Context HttpServletRequest req, @RequestBody UpdateModelTypeVO vo) throws Exception {
        logger.info("updateModelTypeVO : {}", vo);
        return Message.messageToResponse(Message.ok().data("result", assetService.updateModelType(vo)));
    }

    /**
     * 创建标签
     *
     * @param req
     * @param vo
     * @return
     * @throws Exception
     */
    @POST
    @Path("/labels")
    public Response createLabel(@Context HttpServletRequest req, @RequestBody CreateLabelVO vo) throws Exception {
        logger.info("createLabel vo : {}", vo);
        return Message.messageToResponse(Message.ok().data("result", assetService.createLabel(vo)));
    }

    /**
     * 更新标签
     *
     * @param req
     * @param vo
     * @return
     * @throws Exception
     */
    @POST
    @Path("/labels/modify")
    public Response updateLabel(@Context HttpServletRequest req, @RequestBody UpdateLabelVO vo) throws Exception {
        logger.info("updateLabel vo : {}", vo);
        return Message.messageToResponse(Message.ok().data("result", assetService.updateLabel(vo)));
    }

    /**
     * 删除标签
     *
     * @param req
     * @param vo
     * @return
     * @throws Exception
     */
    @POST
    @Path("/labels/delete")
    public Response deleteLabel(@Context HttpServletRequest req, @RequestBody DeleteLabelVO vo) throws Exception {
        logger.info("deleteLabel vo : {}", vo);
        assetService.deleteLabel(vo);
        return Message.messageToResponse(Message.ok().data("result", "删除成功"));
    }

    /**
     * 实体绑定标签
     *
     * @param req
     * @param vo
     * @return
     * @throws Exception
     */
    @POST
    @Path("/labels/bind")
    public Response bindLabel(@Context HttpServletRequest req, @RequestBody BindLabelVO vo) throws Exception {
        logger.info("bindLabel vo : {}", vo);
        assetService.bindLabel(vo);
        return Message.messageToResponse(Message.ok().data("result", "绑定成功"));
    }


    /**
     * 实体解绑标签
     *
     * @param req
     * @param vo
     * @return
     * @throws Exception
     */
    @POST
    @Path("/labels/unbind")
    public Response unBindLabel(@Context HttpServletRequest req, @RequestBody UnBindLabelVO vo) throws Exception {
        logger.info("unBindLabel vo : {}", vo);
        assetService.unBindLabel(vo);
        return Message.messageToResponse(Message.ok().data("result", "解绑成功"));
    }


    /**
     * 搜索标签
     *
     * @param req
     * @param query
     * @return
     * @throws Exception
     */
    @GET
    @Path("/labels/search")
    public Response searchLabel(@Context HttpServletRequest req
                                    , @QueryParam("query") String query
                                    , @QueryParam("limit") @DefaultValue(DEFAULT_LIMIT) int limit
                                    , @QueryParam("offset") @DefaultValue(DEFAULT_OFFSET) int offset) throws Exception {
        logger.info("searchLabel query : {}", query);
        return Message.messageToResponse(Message.ok().data("result",assetService.listLabels(query,limit,offset)));
    }

    /**
     * 设置单个表或单个列的标签
     */
    @POST
    @Path("/label/{guid}")
    public Response setLabels(@PathParam("guid") String guid, @RequestBody Set<String> labels) throws Exception {
        logger.info("setLabels guid : {}, labels : {}", guid, labels);
        assetService.setLabels(guid, labels);

        return Message.messageToResponse(Message.ok().data("result", "设置成功"));
    }

    /**
     * 获取工作空间下所有用户名
     */
    @GET
    @Path("getWorkspaceUsers/{workspaceId}/{search}")
    public Response getWorkspaceUsers(@PathParam("workspaceId") int workspaceId, @PathParam("search") String search) throws Exception {
        String searchs = "%" + search + "%";
        List<String> workspaceUsers = workspaceInfoService.getWorkspaceUsers(workspaceId, searchs);
        return Message.messageToResponse(Message.ok().data("result", workspaceUsers));

    }

    /**
     * 查询用户
     *
     * @param req
     * @param workspaceId
     * @return
     */
    @GET
    @Path("/users/{workspaceId}")
    public Response users(@Context HttpServletRequest req, @PathParam("workspaceId") String workspaceId) throws ErrorException {
        logger.info("users workspaceId : {}", workspaceId);
        GetWorkspaceUsersResult result = linkisWorkSpaceRemoteClient.getWorkspaceUsers(GetWorkspaceUsersAction.builder().setUser(getStrategyUser(req)).setWorkspaceId(workspaceId).build());
        return Message.messageToResponse(Message.ok().data("users", result.getWorkspaceUserList()));
    }


    /**
     * 查询角色
     *
     * @param req
     * @param workspaceId
     * @return
     */
    @GET
    @Path("/roles/{workspaceId}")
    public Response roles(@Context HttpServletRequest req, @PathParam("workspaceId") String workspaceId) throws ErrorException {
        logger.info("roles workspaceId : {}", workspaceId);
        GetWorkspaceUsersResult result = linkisWorkSpaceRemoteClient.getWorkspaceUsers(GetWorkspaceUsersAction.builder().setUser(getStrategyUser(req)).setWorkspaceId(workspaceId).build());
        return Message.messageToResponse(Message.ok().data("users", result.getWorkspaceRoleList()));
    }


}
