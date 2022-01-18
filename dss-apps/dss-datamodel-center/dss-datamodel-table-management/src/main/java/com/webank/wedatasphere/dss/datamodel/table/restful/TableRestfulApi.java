package com.webank.wedatasphere.dss.datamodel.table.restful;

import com.webank.wedatasphere.dss.datamodel.center.common.service.AuthenticationClientStrategy;
import com.webank.wedatasphere.dss.datamodel.center.common.service.DataWarehouseReferenceService;
import com.webank.wedatasphere.dss.datamodel.table.service.LabelService;
import com.webank.wedatasphere.dss.datamodel.table.service.TableService;
import com.webank.wedatasphere.dss.datamodel.table.vo.*;
import com.webank.wedatasphere.dss.framework.workspace.client.impl.LinkisWorkSpaceRemoteClient;
import com.webank.wedatasphere.dss.framework.workspace.client.request.GetWorkspaceUsersAction;
import com.webank.wedatasphere.dss.framework.workspace.client.response.GetWorkspaceUsersResult;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.server.Message;
import com.webank.wedatasphere.warehouse.client.GovernanceDwRemoteClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;


@Component
@Path("/datamodel/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TableRestfulApi implements AuthenticationClientStrategy {


    private static final Logger LOGGER = LoggerFactory.getLogger(TableRestfulApi.class);


    @Autowired
    private TableService tableService;


    @Resource
    private LabelService labelService;


    @Autowired
    private GovernanceDwRemoteClient governanceDwRemoteClient;


    @Resource
    private DataWarehouseReferenceService dataWarehouseReferenceService;

    @Resource
    private LinkisWorkSpaceRemoteClient linkisWorkSpaceRemoteClient;

    /**
     * 新增
     *
     * @param req
     * @param vo
     * @return
     * @throws IOException
     */
    @POST
    @Path("/tables")
    public Response add(@Context HttpServletRequest req, @RequestBody TableAddVO vo) throws ErrorException {
        vo.setCreator(getStrategyUser(req));
        LOGGER.info("tablesAddVO : {}", vo);
        return Message.messageToResponse(Message.ok().data("id", tableService.addTable(vo)));
    }


    /**
     * 更新
     *
     * @param req
     * @param vo
     * @return
     * @throws IOException
     */
    @PUT
    @Path("/tables/{id}")
    public Response update(@Context HttpServletRequest req, @PathParam("id") Long id, @RequestBody TableUpdateVO vo) throws ErrorException {
        vo.setCreator(getStrategyUser(req));
        LOGGER.info("update id : {}, tableUpdateVO : {}", id, vo);
        return Message.messageToResponse(Message.ok().data("count", tableService.updateTable(id, vo)));
    }

    /**
     * 删除
     *
     * @param req
     * @return
     * @throws IOException
     */
    @DELETE
    @Path("/tables/{id}")
    public Response delete(@Context HttpServletRequest req, @PathParam("id") Long id) throws ErrorException {
        LOGGER.info("delete id : {}", id);
        return Message.messageToResponse(Message.ok().data("count", tableService.deleteTable(id)));
    }


    /**
     * 主动绑定
     *
     * @param req
     * @return
     * @throws IOException
     */
    @PUT
    @Path("/tables/bind/{id}")
    public Response bind(@Context HttpServletRequest req, @PathParam("id") Long id) throws ErrorException {
        LOGGER.info("bind id : {}", id);
        tableService.tryBind(id);
        return Message.messageToResponse(Message.ok());
    }


    /**
     * 查看
     *
     * @param req
     * @param id
     * @return
     */
    @GET
    @Path("/tables/{id}")
    public Response query(@Context HttpServletRequest req, @PathParam("id") Long id) throws ErrorException {
        LOGGER.info("query id : {}", id);
        return Message.messageToResponse(Message.ok().data("detail", tableService.queryById(id)));
    }


    /**
     * 按表名搜索查看
     *
     * @param req
     * @param vo
     * @return
     */
    @POST
    @Path("/tables/name")
    public Response queryName(@Context HttpServletRequest req, @RequestBody TableQueryOneVO vo) throws ErrorException {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("query vo : {}", vo);
        return Message.messageToResponse(Message.ok().data("detail", tableService.queryByName(vo)));
    }


    /**
     * 分页搜索
     *
     * @param req
     * @return
     */
    @POST
    @Path("/tables/list")
    public Response list(@Context HttpServletRequest req, @RequestBody TableListVO vo) {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("list vo : {}", vo);
        return Message.messageToResponse(tableService.list(vo));
    }


    /**
     * 新增版本
     *
     * @param req
     * @param vo
     * @return
     * @throws IOException
     */
    @POST
    @Path("/tables/versions/{id}")
    public Response addVersion(@Context HttpServletRequest req, @PathParam("id") Long id, @RequestBody TableVersionAddVO vo) throws Exception {
        vo.setCreator(getStrategyUser(req));
        LOGGER.info("tableVersionAddVO : {}", vo);
        return Message.messageToResponse(Message.ok().data("count", tableService.addTableVersion(id, vo)));
    }


    /**
     * 回退指定版本
     *
     * @param req
     * @param vo
     * @return
     * @throws IOException
     */
    @POST
    @Path("/tables/versions/rollback")
    public Response versionRollBack(@Context HttpServletRequest req, @RequestBody TableVersionRollBackVO vo) throws Exception {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("tableVersionRollBackVO : {}", vo);
        return Message.messageToResponse(Message.ok().data("count", tableService.versionRollBack(vo)));
    }


    /**
     * 搜索表版本历史
     *
     * @param req
     * @return
     */
    @POST
    @Path("/tables/versions/list")
    public Response tableVersionsList(@Context HttpServletRequest req, @RequestBody TableVersionQueryVO vo) {
        LOGGER.info("version list vo : {}", vo);
        return Message.messageToResponse(tableService.listTableVersions(vo));
    }


//    /**
//     * 相关主题可选列表
//     *
//     * @param req
//     * @return
//     */
//    @POST
//    @Path("/tables/themes/list")
//    public Response tableThemesList(@Context HttpServletRequest req) {
//        ListDwThemeDomainAction action = ListDwThemeDomainAction.builder().setUser(getStrategyUser(req)).setIsAvailable(true).build();
//        return Message.messageToResponse(Message.ok().data("list", governanceDwRemoteClient.listThemeDomains(action).getAll()));
//    }


    /**
     * 主题引用情况
     *
     * @param req
     * @return
     */
    @GET
    @Path("/themes/reference/{name}")
    public Response themesReference(@Context HttpServletRequest req, @PathParam("name") String name) {
        LOGGER.info("themes reference name : {}", name);
        return Message.messageToResponse(Message.ok().data("result", dataWarehouseReferenceService.themeReferenceCount(name)));
    }

//    /**
//     * 相关分层
//     *
//     * @param req
//     * @return
//     */
//    @POST
//    @Path("/tables/layers/list")
//    public Response tableLayerList(@Context HttpServletRequest req) {
//        ListDwLayerAction action = ListDwLayerAction.builder().setIsAvailable(true).setUser(getStrategyUser(req)).build();
//        return Message.messageToResponse(Message.ok().data("list", governanceDwRemoteClient.listLayers(action).getAll()));
//    }

    /**
     * 分层引用情况
     *
     * @param req
     * @return
     */
    @GET
    @Path("/layers/reference/{name}")
    public Response layersReference(@Context HttpServletRequest req, @PathParam("name") String name) {
        LOGGER.info("layers reference name : {}", name);
        return Message.messageToResponse(Message.ok().data("result", dataWarehouseReferenceService.layerReferenceCount(name)));
    }

    /**
     * 周期引用情况
     *
     * @param req
     * @return
     */
    @GET
    @Path("/cycles/reference/{name}")
    public Response cycleReference(@Context HttpServletRequest req, @PathParam("name") String name) {
        LOGGER.info("cycles reference name : {}", name);
        return Message.messageToResponse(Message.ok().data("result", dataWarehouseReferenceService.cycleReferenceCount(name)));
    }

    /**
     * 修饰词引用情况
     *
     * @param req
     * @return
     */
    @GET
    @Path("/modifiers/reference/{name}")
    public Response modifiersReference(@Context HttpServletRequest req, @PathParam("name") String name) {
        LOGGER.info("modifiers reference name : {}", name);
        return Message.messageToResponse(Message.ok().data("result", dataWarehouseReferenceService.modifierReferenceCount(name)));
    }

    /**
     * 数据库列表
     *
     * @param req
     * @return
     */
    @POST
    @Path("/tables/databases/list")
    public Response tableDataBasesList(@Context HttpServletRequest req, TableDatabasesQueryVO vo) {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("table databases vo : {}", vo);
        return Message.messageToResponse(tableService.listDataBases(vo));
    }


    /**
     * 收藏
     *
     * @param req
     * @return
     */
    @POST
    @Path("/tables/collect/")
    public Response tableCollect(@Context HttpServletRequest req, @RequestBody TableCollectVO vo) throws ErrorException {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("table collection vo : {}", vo);
        return Message.messageToResponse(Message.ok().data("count", tableService.tableCollect(vo)));
    }

    /**
     * 取消收藏
     *
     * @param req
     * @return
     */
    @POST
    @Path("/tables/collect/cancel")
    public Response tableCancelCollect(@Context HttpServletRequest req, @RequestBody TableCollectCancelVO vo) throws ErrorException {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("table collection cancel vo : {}", vo);
        return Message.messageToResponse(Message.ok().data("count", tableService.tableCancel(vo)));
    }


    /**
     * 我的收藏列表
     *
     * @param req
     * @return
     */
    @POST
    @Path("/tables/collect/list")
    public Response tableCollectList(@Context HttpServletRequest req, @RequestBody TableCollectQueryVO vo) throws ErrorException {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("table collection list vo : {}", vo);
        return Message.messageToResponse(tableService.tableCollections(vo));
    }

    /**
     * 表数据预览
     *
     * @param req
     * @param vo
     * @return
     */
    @POST
    @Path("/tables/data/preview/")
    public Response tableDataPreview(@Context HttpServletRequest req, @RequestBody TableDataPreviewVO vo) throws ErrorException {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("table data preview : {}", vo);
        return Message.messageToResponse(tableService.previewData(vo));
    }


    /**
     * 执行建表
     *
     * @param req
     * @param vo
     * @return
     */
    @POST
    @Path("/tables/create/")
    public Response tableCreate(@Context HttpServletRequest req, @RequestBody TableCreateVO vo) throws ErrorException {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("table create vo : {}", vo);
        return Message.messageToResponse(Message.ok().data("count", tableService.tableCreate(vo)));
    }

    /**
     * 生成建表语句
     *
     * @param req
     * @param vo
     * @return
     */
    @POST
    @Path("/tables/create/sql")
    public Response tableCreateSql(@Context HttpServletRequest req, @RequestBody TableCreateSqlVO vo) throws ErrorException {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("table create sql vo : {}", vo);
        return Message.messageToResponse(Message.ok().data("detail", tableService.tableCreateSql(vo)));
    }


    /**
     * 字典列表
     *
     * @param req
     * @param vo
     * @return
     */
    @POST
    @Path("/tables/dictionaries/list")
    public Response tableDictionaryList(@Context HttpServletRequest req, @RequestBody TableDictionaryListVO vo) {
        LOGGER.info("table dictionaries list vo : {}", vo);
        return Message.messageToResponse(tableService.dictionaryList(vo));
    }


    /**
     * 新增字段
     *
     * @param req
     * @param vo
     * @return
     */
    @POST
    @Path("/tables/columns/add")
    public Response tableColumnsAdd(@Context HttpServletRequest req, @RequestBody TableColumnsAddVO vo) throws ErrorException {
        LOGGER.info("table column add vo : {}", vo);
        return Message.messageToResponse(Message.ok().data("count", tableService.addTableColumn(vo)));
    }

    /**
     * 字段绑定模型
     *
     * @param req
     * @param vo
     * @return
     */
    @POST
    @Path("/tables/columns/bind/{columnId}")
    public Response tableColumnBind(@Context HttpServletRequest req, @PathParam("columnId") Long columnId, @RequestBody TableColumnBindVO vo) throws ErrorException {
        LOGGER.info("table column bind model columnId : {}, vo : {}", columnId, vo);
        return Message.messageToResponse(Message.ok().data("count", tableService.tableColumnBind(columnId, vo)));
    }

    /**
     * 分区统计信息
     *
     * @param vo
     * @return
     */
    @POST
    @Path("/tables/partition/stats/")
    public Response tblPartitionStats(@Context HttpServletRequest req, TblPartitionStatsVO vo) {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("table partition stats vo : {}", vo);
        return Message.messageToResponse(tableService.listTablePartitionStats(vo));
    }


    /**
     * 获取当前用户
     *
     * @param req
     * @return
     */
    @GET
    @Path("/current/user")
    public Response currentUser(@Context HttpServletRequest req) {
        return Message.messageToResponse(Message.ok().data("user", getStrategyUser(req)));
    }


    /**
     * 检测表是否有数据
     *
     * @param req
     * @return
     */
    @POST
    @Path("/tables/check/data")
    public Response tableCheckData(@Context HttpServletRequest req, @RequestBody TableCheckDataVO vo) throws ErrorException {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("table partition stats vo : {}", vo);
        return Message.messageToResponse(Message.ok().data("status", tableService.tableCheckData(vo)));
    }


    /**
     * 新增标签
     *
     * @param req
     * @param vo
     * @return
     * @throws IOException
     */
    @POST
    @Path("/labels")
    public Response addLabels(@Context HttpServletRequest req, @RequestBody LabelAddVO vo) throws ErrorException {
        LOGGER.info("addLabels vo : {}", vo);
        return Message.messageToResponse(Message.ok().data("id", labelService.add(vo)));
    }


    /**
     * 更新标签
     *
     * @param req
     * @param vo
     * @return
     * @throws IOException
     */
    @PUT
    @Path("/labels/{id}")
    public Response updateLabels(@Context HttpServletRequest req, @PathParam("id") Long id, @RequestBody LabelUpdateVO vo) throws ErrorException {
        LOGGER.info("updateLabels id : {}, vo : {}", id, vo);
        return Message.messageToResponse(Message.ok().data("count", labelService.update(id, vo)));
    }

    /**
     * 删除标签
     *
     * @param req
     * @return
     * @throws IOException
     */
    @DELETE
    @Path("/labels/{id}")
    public Response deleteLabels(@Context HttpServletRequest req, @PathParam("id") Long id) throws ErrorException {
        LOGGER.info("deleteLabels id : {}", id);
        return Message.messageToResponse(Message.ok().data("count", labelService.delete(id)));
    }

    /**
     * 查看标签
     *
     * @param req
     * @param id
     * @return
     */
    @GET
    @Path("/labels/{id}")
    public Response queryLabels(@Context HttpServletRequest req, @PathParam("id") Long id) throws ErrorException {
        LOGGER.info("queryLabels id : {}", id);
        return Message.messageToResponse(Message.ok().data("detail", labelService.query(id)));
    }


    /**
     * 标签搜索
     *
     * @param req
     * @return
     */
    @POST
    @Path("/labels/list")
    public Response listLabels(@Context HttpServletRequest req, @RequestBody LabelsQueryVO vo) throws ErrorException {
        LOGGER.info("listLabels vo : {}", vo);
        return Message.messageToResponse(labelService.list(vo));
    }

    /**
     * 启用/禁用
     *
     * @param req
     * @param id
     * @param vo
     * @return
     */
    @PUT
    @Path("/labels/enable/{id}")
    public Response enableLabel(@Context HttpServletRequest req, @PathParam("id") Long id, @RequestBody LabelEnableVO vo) throws ErrorException {
        LOGGER.info("enableLabel id : {}, vo : {}", id, vo);
        return Message.messageToResponse(Message.ok().data("count", labelService.enable(id, vo)));
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
        LOGGER.info("users workspaceId : {}", workspaceId);
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
        LOGGER.info("roles workspaceId : {}", workspaceId);
        GetWorkspaceUsersResult result = linkisWorkSpaceRemoteClient.getWorkspaceUsers(GetWorkspaceUsersAction.builder().setUser(getStrategyUser(req)).setWorkspaceId(workspaceId).build());
        return Message.messageToResponse(Message.ok().data("users", result.getWorkspaceRoleList()));
    }

}
