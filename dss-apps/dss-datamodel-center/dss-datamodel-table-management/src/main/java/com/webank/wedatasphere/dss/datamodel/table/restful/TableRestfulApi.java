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
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;



@RestController
@RequestMapping(value = "datamodel", produces = {"application/json;charset=utf-8"})
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
    @RequestMapping( value = "/tables", method = RequestMethod.POST)
    public Message add(HttpServletRequest req, @RequestBody TableAddVO vo) throws ErrorException {
        vo.setCreator(getStrategyUser(req));
        LOGGER.info("tablesAddVO : {}", vo);
        return Message.ok().data("id", tableService.addTable(vo));
    }


    /**
     * 更新
     *
     * @param req
     * @param vo
     * @return
     * @throws IOException
     */
    @RequestMapping( value = "/tables/{id}", method = RequestMethod.PUT)
    public Message update(HttpServletRequest req, @PathVariable("id") Long id, @RequestBody TableUpdateVO vo) throws ErrorException {
        vo.setCreator(getStrategyUser(req));
        LOGGER.info("update id : {}, tableUpdateVO : {}", id, vo);
        return Message.ok().data("count", tableService.updateTable(id, vo));
    }

    /**
     * 删除
     *
     * @param req
     * @return
     * @throws IOException
     */
    @RequestMapping( value = "/tables/{id}", method = RequestMethod.DELETE)
    public Message delete(HttpServletRequest req, @PathVariable("id") Long id) throws ErrorException {
        LOGGER.info("delete id : {}", id);
        return Message.ok().data("count", tableService.deleteTable(id));
    }


    /**
     * 主动绑定
     *
     * @param req
     * @return
     * @throws IOException
     */
    @RequestMapping( value = "/tables/bind/{id}", method = RequestMethod.PUT)
    public Message bind(HttpServletRequest req, @PathVariable("id") Long id) throws ErrorException {
        LOGGER.info("bind id : {}", id);
        tableService.tryBind(id);
        return Message.ok();
    }


    /**
     * 查看
     *
     * @param req
     * @param id
     * @return
     */
    @RequestMapping( value = "/tables/{id}", method = RequestMethod.GET)
    public Message query(HttpServletRequest req, @PathVariable("id") Long id) throws ErrorException {
        LOGGER.info("query id : {}", id);
        return Message.ok().data("detail", tableService.queryById(id));
    }


    /**
     * 按表名搜索查看
     *
     * @param req
     * @param vo
     * @return
     */
    @RequestMapping( value = "/tables/name", method = RequestMethod.POST)
    public Message queryName(HttpServletRequest req, @RequestBody TableQueryOneVO vo) throws ErrorException {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("query vo : {}", vo);
        return Message.ok().data("detail", tableService.queryByName(vo));
    }


    /**
     * 分页搜索
     *
     * @param req
     * @return
     */
    @RequestMapping( value = "/tables/list", method = RequestMethod.POST)
    public Message list(HttpServletRequest req, @RequestBody TableListVO vo) {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("list vo : {}", vo);
        return tableService.list(vo);
    }


    /**
     * 新增版本
     *
     * @param req
     * @param vo
     * @return
     * @throws IOException
     */
    @RequestMapping( value = "/tables/versions/{id}", method = RequestMethod.POST)
    public Message addVersion(HttpServletRequest req, @PathVariable("id") Long id, @RequestBody TableVersionAddVO vo) throws Exception {
        vo.setCreator(getStrategyUser(req));
        LOGGER.info("tableVersionAddVO : {}", vo);
        return Message.ok().data("count", tableService.addTableVersion(id, vo));
    }


    /**
     * 回退指定版本
     *
     * @param req
     * @param vo
     * @return
     * @throws IOException
     */
    @RequestMapping( value = "/tables/versions/rollback", method = RequestMethod.POST)
    public Message versionRollBack(HttpServletRequest req, @RequestBody TableVersionRollBackVO vo) throws Exception {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("tableVersionRollBackVO : {}", vo);
        return Message.ok().data("count", tableService.versionRollBack(vo));
    }


    /**
     * 搜索表版本历史
     *
     * @param req
     * @return
     */
    @RequestMapping( value = "/tables/versions/list", method = RequestMethod.POST)
    public Message tableVersionsList(HttpServletRequest req, @RequestBody TableVersionQueryVO vo) {
        LOGGER.info("version list vo : {}", vo);
        return tableService.listTableVersions(vo);
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

    @RequestMapping( value = "/themes/reference/{name}", method = RequestMethod.GET)
    public Message themesReference(HttpServletRequest req, @PathVariable("name") String name) {
        LOGGER.info("themes reference name : {}", name);
        return Message.ok().data("result", dataWarehouseReferenceService.themeReferenceCount(name));
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
    @RequestMapping( value = "/layers/reference/{name}", method = RequestMethod.GET)
    public Message layersReference(HttpServletRequest req, @PathVariable("name") String name) {
        LOGGER.info("layers reference name : {}", name);
        return Message.ok().data("result", dataWarehouseReferenceService.layerReferenceCount(name));
    }

    /**
     * 周期引用情况
     *
     * @param req
     * @return
     */
    @RequestMapping( value = "/cycles/reference/{name}", method = RequestMethod.GET)
    public Message cycleReference(HttpServletRequest req, @PathVariable("name") String name) {
        LOGGER.info("cycles reference name : {}", name);
        return Message.ok().data("result", dataWarehouseReferenceService.cycleReferenceCount(name));
    }

    /**
     * 修饰词引用情况
     *
     * @param req
     * @return
     */
    @RequestMapping( value = "/modifiers/reference/{name}", method = RequestMethod.GET)
    public Message modifiersReference(HttpServletRequest req, @PathVariable("name") String name) {
        LOGGER.info("modifiers reference name : {}", name);
        return Message.ok().data("result", dataWarehouseReferenceService.modifierReferenceCount(name));
    }

    /**
     * 数据库列表
     *
     * @param req
     * @return
     */
    @RequestMapping( value = "/tables/databases/list", method = RequestMethod.POST)
    public Message tableDataBasesList(HttpServletRequest req,@RequestBody TableDatabasesQueryVO vo) {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("table databases vo : {}", vo);
        return tableService.listDataBases(vo);
    }


    /**
     * 收藏
     *
     * @param req
     * @return
     */
    @RequestMapping( value = "/tables/collect", method = RequestMethod.POST)
    public Message tableCollect(HttpServletRequest req, @RequestBody TableCollectVO vo) throws ErrorException {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("table collection vo : {}", vo);
        return Message.ok().data("count", tableService.tableCollect(vo));
    }

    /**
     * 取消收藏
     *
     * @param req
     * @return
     */
    @RequestMapping( value = "/tables/collect/cancel", method = RequestMethod.POST)
    public Message tableCancelCollect(HttpServletRequest req, @RequestBody TableCollectCancelVO vo) throws ErrorException {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("table collection cancel vo : {}", vo);
        return Message.ok().data("count", tableService.tableCancel(vo));
    }


    /**
     * 我的收藏列表
     *
     * @param req
     * @return
     */
    @RequestMapping( value = "/tables/collect/list", method = RequestMethod.POST)
    public Message tableCollectList(HttpServletRequest req, @RequestBody TableCollectQueryVO vo) throws ErrorException {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("table collection list vo : {}", vo);
        return tableService.tableCollections(vo);
    }

    /**
     * 表数据预览
     *
     * @param req
     * @param vo
     * @return
     */
    @RequestMapping( value = "/tables/data/preview", method = RequestMethod.POST)
    public Message tableDataPreview(HttpServletRequest req, @RequestBody TableDataPreviewVO vo) throws ErrorException {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("table data preview : {}", vo);
        return tableService.previewData(vo);
    }


    /**
     * 执行建表
     *
     * @param req
     * @param vo
     * @return
     */
    @RequestMapping( value = "/tables/create", method = RequestMethod.POST)
    public Message tableCreate(HttpServletRequest req, @RequestBody TableCreateVO vo) throws ErrorException {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("table create vo : {}", vo);
        return Message.ok().data("count", tableService.tableCreate(vo));
    }

    /**
     * 生成建表语句
     *
     * @param req
     * @param vo
     * @return
     */
    @RequestMapping( value = "/tables/create/sql", method = RequestMethod.POST)
    public Message tableCreateSql(HttpServletRequest req, @RequestBody TableCreateSqlVO vo) throws ErrorException {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("table create sql vo : {}", vo);
        return Message.ok().data("detail", tableService.tableCreateSql(vo));
    }


    /**
     * 字典列表
     *
     * @param req
     * @param vo
     * @return
     */
    @RequestMapping( value = "/tables/dictionaries/list", method = RequestMethod.POST)
    public Message tableDictionaryList(HttpServletRequest req, @RequestBody TableDictionaryListVO vo) {
        LOGGER.info("table dictionaries list vo : {}", vo);
        return tableService.dictionaryList(vo);
    }


    /**
     * 新增字段
     *
     * @param req
     * @param vo
     * @return
     */
    @RequestMapping( value = "/tables/columns/add", method = RequestMethod.POST)
    public Message tableColumnsAdd(HttpServletRequest req, @RequestBody TableColumnsAddVO vo) throws ErrorException {
        LOGGER.info("table column add vo : {}", vo);
        return Message.ok().data("count", tableService.addTableColumn(vo));
    }

    /**
     * 字段绑定模型
     *
     * @param req
     * @param vo
     * @return
     */

    @RequestMapping( value = "/tables/columns/bind/{columnId}", method = RequestMethod.POST)
    public Message tableColumnBind(HttpServletRequest req, @PathVariable("columnId") Long columnId, @RequestBody TableColumnBindVO vo) throws ErrorException {
        LOGGER.info("table column bind model columnId : {}, vo : {}", columnId, vo);
        return Message.ok().data("count", tableService.tableColumnBind(columnId, vo));
    }

    /**
     * 分区统计信息
     *
     * @param vo
     * @return
     */
    @RequestMapping( value = "/tables/partition/stats", method = RequestMethod.POST)
    public Message tblPartitionStats(HttpServletRequest req,@RequestBody TblPartitionStatsVO vo) {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("table partition stats vo : {}", vo);
        return tableService.listTablePartitionStats(vo);
    }


    /**
     * 获取当前用户
     *
     * @param req
     * @return
     */

    @RequestMapping( value = "/current/user", method = RequestMethod.GET)
    public Message currentUser(HttpServletRequest req) {
        return Message.ok().data("user", getStrategyUser(req));
    }


    /**
     * 检测表是否有数据
     *
     * @param req
     * @return
     */
    @RequestMapping( value = "/tables/check/data", method = RequestMethod.POST)
    public Message tableCheckData(HttpServletRequest req, @RequestBody TableCheckDataVO vo) throws ErrorException {
        vo.setUser(getStrategyUser(req));
        LOGGER.info("table partition stats vo : {}", vo);
        return Message.ok().data("status", tableService.tableCheckData(vo));
    }


    /**
     * 新增标签
     *
     * @param req
     * @param vo
     * @return
     * @throws IOException
     */

    @RequestMapping( value = "/labels", method = RequestMethod.POST)
    public Message addLabels(HttpServletRequest req, @RequestBody LabelAddVO vo) throws ErrorException {
        LOGGER.info("addLabels vo : {}", vo);
        return Message.ok().data("id", labelService.add(vo));
    }


    /**
     * 更新标签
     *
     * @param req
     * @param vo
     * @return
     * @throws IOException
     */
    @RequestMapping( value = "/labels/{id}", method = RequestMethod.PUT)
    public Message updateLabels(HttpServletRequest req, @PathVariable("id") Long id, @RequestBody LabelUpdateVO vo) throws ErrorException {
        LOGGER.info("updateLabels id : {}, vo : {}", id, vo);
        return Message.ok().data("count", labelService.update(id, vo));
    }

    /**
     * 删除标签
     *
     * @param req
     * @return
     * @throws IOException
     */
    @RequestMapping( value = "/labels/{id}", method = RequestMethod.DELETE)
    public Message deleteLabels(HttpServletRequest req, @PathVariable("id") Long id) throws ErrorException {
        LOGGER.info("deleteLabels id : {}", id);
        return Message.ok().data("count", labelService.delete(id));
    }

    /**
     * 查看标签
     *
     * @param req
     * @param id
     * @return
     */

    @RequestMapping( value = "/labels/{id}", method = RequestMethod.GET)
    public Message queryLabels(HttpServletRequest req, @PathVariable("id") Long id) throws ErrorException {
        LOGGER.info("queryLabels id : {}", id);
        return Message.ok().data("detail", labelService.query(id));
    }


    /**
     * 标签搜索
     *
     * @param req
     * @return
     */

    @RequestMapping( value = "/labels/list", method = RequestMethod.POST)
    public Message listLabels(HttpServletRequest req, @RequestBody LabelsQueryVO vo) throws ErrorException {
        LOGGER.info("listLabels vo : {}", vo);
        return labelService.list(vo);
    }

    /**
     * 启用/禁用
     *
     * @param req
     * @param id
     * @param vo
     * @return
     */
    @RequestMapping( value = "/labels/enable/{id}", method = RequestMethod.PUT)
    public Message enableLabel(HttpServletRequest req, @PathVariable("id") Long id, @RequestBody LabelEnableVO vo) throws ErrorException {
        LOGGER.info("enableLabel id : {}, vo : {}", id, vo);
        return Message.ok().data("count", labelService.enable(id, vo));
    }


    /**
     * 查询用户
     *
     * @param req
     * @param workspaceId
     * @return
     */

    @RequestMapping( value = "/users/{workspaceId}", method = RequestMethod.GET)
    public Message users(HttpServletRequest req, @PathVariable("workspaceId") String workspaceId) throws ErrorException {
        LOGGER.info("users workspaceId : {}", workspaceId);
        GetWorkspaceUsersResult result = linkisWorkSpaceRemoteClient.getWorkspaceUsers(GetWorkspaceUsersAction.builder().setUser(getStrategyUser(req)).setWorkspaceId(workspaceId).build());
        return Message.ok().data("users", result.getWorkspaceUserList());
    }


    /**
     * 查询角色
     *
     * @param req
     * @param workspaceId
     * @return
     */

    @RequestMapping( value = "/roles/{workspaceId}", method = RequestMethod.GET)
    public Message roles(HttpServletRequest req, @PathVariable("workspaceId") String workspaceId) throws ErrorException {
        LOGGER.info("roles workspaceId : {}", workspaceId);
        GetWorkspaceUsersResult result = linkisWorkSpaceRemoteClient.getWorkspaceUsers(GetWorkspaceUsersAction.builder().setUser(getStrategyUser(req)).setWorkspaceId(workspaceId).build());
        return Message.ok().data("users", result.getWorkspaceRoleList());
    }

}
