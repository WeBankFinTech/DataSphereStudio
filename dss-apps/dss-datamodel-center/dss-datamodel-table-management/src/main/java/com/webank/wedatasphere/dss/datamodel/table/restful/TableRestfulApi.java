package com.webank.wedatasphere.dss.datamodel.table.restful;

import com.webank.wedatasphere.dss.datamodel.table.service.TableService;
import com.webank.wedatasphere.dss.datamodel.table.vo.*;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import com.webank.wedatasphere.warehouse.client.GovernanceDwRemoteClient;
import com.webank.wedatasphere.warehouse.client.action.ListDwLayerAction;
import com.webank.wedatasphere.warehouse.client.action.ListDwThemeDomainAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

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
public class TableRestfulApi {


    private static final Logger LOGGER = LoggerFactory.getLogger(TableRestfulApi.class);

    @Autowired
    private TableService tableService;


    @Autowired
    private GovernanceDwRemoteClient governanceDwRemoteClient;

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
        LOGGER.info("tablesAddVO : {}", vo);
        String userName = SecurityFilter.getLoginUsername(req);
        vo.setCreator(userName);
        return Message.messageToResponse(Message.ok().data("count",tableService.addTable(vo)));
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
    public Response update(@Context HttpServletRequest req, @PathParam("id") Long id,@RequestBody TableUpdateVO vo) throws ErrorException {
        LOGGER.info("update id : {}, tableUpdateVO : {}", id, vo);
        String userName = SecurityFilter.getLoginUsername(req);
        vo.setCreator(userName);
        return Message.messageToResponse(Message.ok().data("count",tableService.updateTable(id,vo)));
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
        return Message.messageToResponse(Message.ok().data("detail",tableService.queryById(id)));
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
        LOGGER.info("query vo : {}", vo);
        return Message.messageToResponse(Message.ok().data("detail",tableService.queryByName(vo)));
    }


    /**
     * 分页搜索
     *
     * @param req
     * @return
     */
    @POST
    @Path("/tables/list")
    public Response list(@Context HttpServletRequest req, @RequestBody TableQueryVO vo) {
        LOGGER.info("list vo : {}", vo);
        return null;
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
        LOGGER.info("tableVersionAddVO : {}", vo);
        return Message.messageToResponse(Message.ok().data("count",tableService.addTableVersion(id,vo)));
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
    public Response versionRollBack(@Context HttpServletRequest req, TableVersionRollBackVO vo) throws Exception {
        LOGGER.info("tableVersionRollBackVO : {}", vo);
        return Message.messageToResponse(Message.ok().data("count",tableService.versionRollBack(vo)));
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


    /**
     * 相关主题可选列表
     *
     * @param req
     * @return
     */
    @POST
    @Path("/tables/themes/list")
    public Response tableThemesList(@Context HttpServletRequest req) {
        //todo
        return Message.messageToResponse(Message.ok().data("list", governanceDwRemoteClient.listThemeDomains(new ListDwThemeDomainAction()).getAll()));
    }

    /**
     * 相关分层
     *
     * @param req
     * @return
     */
    @POST
    @Path("/tables/layers/list")
    public Response tableLayerList(@Context HttpServletRequest req) {
        //todo
        return Message.messageToResponse(Message.ok().data("list", governanceDwRemoteClient.listLayers(new ListDwLayerAction()).getAll()));
    }

    /**
     * 数据库列表
     *
     * @param req
     * @return
     */
    @POST
    @Path("/tables/databases/list")
    public Response tableDataBasesList(@Context HttpServletRequest req) {
        //todo
        return null;
    }


    /**
     * 收藏
     *
     * @param req
     * @return
     */
    @POST
    @Path("/tables/collect/")
    public Response tableCollect(@Context HttpServletRequest req, TableCollectVO vo) throws ErrorException {
        LOGGER.info("table collection vo : {}", vo);
        String userName = SecurityFilter.getLoginUsername(req);
        vo.setUser(userName);
        return Message.messageToResponse(Message.ok().data("count",tableService.tableCollect(vo)));
    }

    /**
     * 取消收藏
     *
     * @param req
     * @return
     */
    @POST
    @Path("/tables/collect/cancel")
    public Response tableCancelCollect(@Context HttpServletRequest req, TableCollectCancelVO vo) throws ErrorException {
        LOGGER.info("table collection cancel vo : {}", vo);
        String userName = SecurityFilter.getLoginUsername(req);
        vo.setUser(userName);
        return Message.messageToResponse(Message.ok().data("count",tableService.tableCancel(vo)));
    }


    /**
     * 我的收藏列表
     *
     * @param req
     * @return
     */
    @POST
    @Path("/tables/collect/list")
    public Response tableCancelList(@Context HttpServletRequest req, TableCollectQueryVO vo) throws ErrorException {
        LOGGER.info("table collection list vo : {}", vo);
        String userName = SecurityFilter.getLoginUsername(req);
        vo.setUser(userName);
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
    public Response tableDataPreview(@Context HttpServletRequest req, TableDataPreviewVO vo) {
        return null;
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
    public Response tableCreate(@Context HttpServletRequest req, TableCreateVO vo) {
        return null;
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
    public Response tableDictionaryList(@Context HttpServletRequest req, TableDictionaryListVO vo){
        LOGGER.info("table dictionaries list vo : {}", vo);
        return Message.messageToResponse(tableService.dictionaryList(vo));
    }


    /**
     * 新增字段
     * @param req
     * @param vo
     * @return
     */
    @POST
    @Path("/tables/columns/add")
    public Response tableColumnsAdd(@Context HttpServletRequest req, TableColumnsAddVO vo) throws ErrorException {
        LOGGER.info("table column add vo : {}", vo);
        return Message.messageToResponse(Message.ok().data("count",tableService.addTableColumn(vo)));
    }

}
