package com.webank.wedatasphere.dss.datamodel.indicator.restful;

import com.webank.wedatasphere.dss.datamodel.center.common.service.AuthenticationClientStrategy;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorService;
import com.webank.wedatasphere.dss.datamodel.indicator.vo.*;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.server.Message;
import com.webank.wedatasphere.warehouse.client.GovernanceDwRemoteClient;
import com.webank.wedatasphere.warehouse.client.action.ListDwLayerAction;
import com.webank.wedatasphere.warehouse.client.action.ListDwModifierAction;
import com.webank.wedatasphere.warehouse.client.action.ListDwStatisticalPeriodAction;
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
@Path("/datamodel")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IndicatorRestfulApi implements AuthenticationClientStrategy {


    private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorRestfulApi.class);

    @Autowired
    private IndicatorService indicatorService;

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
    @Path("/indicators")
    public Response add(@Context HttpServletRequest req, @RequestBody IndicatorAddVO vo) throws Exception {
        LOGGER.info("indicatorAddVO : {}", vo);
        return Message.messageToResponse(Message.ok().data("count",indicatorService.addIndicator(vo,"1" )));
    }


    /**
     * 修改
     * @param req
     * @param id
     * @param vo
     * @return
     * @throws Exception
     */
    @PUT
    @Path("/indicators/{id}")
    public Response update(@Context HttpServletRequest req, @PathParam("id") Long id , @RequestBody IndicatorUpdateVO vo) throws Exception {
        LOGGER.info("update id : {}, indicatorUpdateVO : {}", id, vo);
        return Message.messageToResponse(Message.ok().data("count",indicatorService.updateIndicator(id,vo)));
    }


    /**
     * 启用/禁用
     * @param req
     * @param id
     * @param vo
     * @return
     */
    @PUT
    @Path("/indicators/enable/{id}")
    public Response enable(@Context HttpServletRequest req, @PathParam("id") Long id, @RequestBody IndicatorEnableVO vo) {
        LOGGER.info("enable id : {}, vo : {}", id, vo);
        return Message.messageToResponse(Message.ok().data("count", indicatorService.enableIndicator(id, vo)));
    }


    /**
     * 分页搜索
     * @param req
     * @return
     */
    @POST
    @Path("/indicators/list")
    public Response list(@Context HttpServletRequest req, @RequestBody IndicatorQueryVO vo){
        LOGGER.info("list vo : {}",vo);
        return Message.messageToResponse(indicatorService.listIndicators(vo));
    }



    /**
     * 查看
     * @param req
     * @param id
     * @return
     */
    @GET
    @Path("/indicators/{id}")
    public Response query(@Context HttpServletRequest req, @PathParam("id") Long id) throws ErrorException {
        LOGGER.info("query id : {}", id);
        return Message.messageToResponse(indicatorService.queryById(id));
    }

    /**
     * 删除
     * @param req
     * @param id
     * @return
     */
    @DELETE
    @Path("/indicators/{id}")
    public Response delete(@Context HttpServletRequest req, @PathParam("id") Long id) throws ErrorException {
        LOGGER.info("delete id : {}", id);
        return Message.messageToResponse(Message.ok().data("count",indicatorService.deleteIndicator(id)));
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
    @Path("/indicators/versions/{id}")
    public Response addVersion(@Context HttpServletRequest req, @PathParam("id") Long id,@RequestBody IndicatorVersionAddVO vo) throws Exception {
        LOGGER.info("indicatorVersionAddVO : {}", vo);
        return Message.messageToResponse(Message.ok().data("count",indicatorService.addIndicatorVersion(id,vo)));
    }



    /**
     *
     * 回退指定版本
     * @param req
     * @param vo
     * @return
     * @throws IOException
     */
    @POST
    @Path("/indicators/versions/rollback")
    public Response versionRollBack(@Context HttpServletRequest req,IndicatorVersionRollBackVO vo) throws Exception {
        LOGGER.info("indicatorVersionRollBackVO : {}", vo);
        return Message.messageToResponse(Message.ok().data("count",indicatorService.versionRollBack(vo)));
    }



    /**
     * 搜索指标版本
     * @param req
     * @return
     */
    @POST
    @Path("/indicators/versions/list")
    public Response indicatorVersionsList(@Context HttpServletRequest req, @RequestBody IndicatorVersionQueryVO vo){
        LOGGER.info("version list vo : {}",vo);
        return Message.messageToResponse(indicatorService.listIndicatorVersions(vo));
    }


    /**
     * 指标相关主题可选列表
     * @param req
     * @return
     */
    @POST
    @Path("/themes/list")
    public Response themesList(@Context HttpServletRequest req){
        ListDwThemeDomainAction action = ListDwThemeDomainAction.builder().setUser(getStrategyUser(req)).setIsAvailable(true).build();
        return Message.messageToResponse(Message.ok().data("list",governanceDwRemoteClient.listThemeDomains(action).getAll()));
    }

    /**
     * 指标相关分层
     * @param req
     * @return
     */
    @POST
    @Path("/layers/list")
    public Response layerList(@Context HttpServletRequest req,@RequestBody LayerVO vo){
        LOGGER.info("layerList vo : {}",vo);
        ListDwLayerAction action = ListDwLayerAction.builder().setIsAvailable(true).setDb(vo.getDbName()).setUser(getStrategyUser(req)).build();
        return Message.messageToResponse(Message.ok().data("list",governanceDwRemoteClient.listLayers(action).getAll()));
    }

    /**
     * 指标相关周期列表
     * @param req
     * @return
     */
    @POST
    @Path("/cycles/list")
    public Response cycleList(@Context HttpServletRequest req,@RequestBody CycleVO vo){
        LOGGER.info("cycleList vo : {}",vo);
        ListDwStatisticalPeriodAction action =ListDwStatisticalPeriodAction.builder().setUser(getStrategyUser(req)).setLayer(vo.getLayer()).setIsAvailable(true).setTheme(vo.getTheme()).build();
        return Message.messageToResponse(Message.ok().data("list",governanceDwRemoteClient.listStatisticalPeriods(action).getAll()));
    }

    /**
     * 指标相关修饰词列表
     * @param req
     * @return
     */
    @POST
    @Path("/modifiers/list")
    public Response modifierList(@Context HttpServletRequest req,@RequestBody ModifierVO vo){
        LOGGER.info("modifierList vo : {}",vo);
        ListDwModifierAction action = ListDwModifierAction.builder().setUser(getStrategyUser(req)).setLayer(vo.getLayer()).setIsAvailable(true).setTheme(vo.getTheme()).build();
        return Message.messageToResponse(Message.ok().data("list",governanceDwRemoteClient.listModifiers(action).getAll()));
    }


    @POST
    @Path("/current/user")
    public Response currentUser(@Context HttpServletRequest req){
        return Message.messageToResponse(Message.ok().data("user",getStrategyUser(req)));
    }
}
