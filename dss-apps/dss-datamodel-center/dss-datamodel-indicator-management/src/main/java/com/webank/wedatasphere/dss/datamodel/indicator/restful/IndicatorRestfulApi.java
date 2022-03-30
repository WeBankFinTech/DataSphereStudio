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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;


@RestController
@RequestMapping(value = "datamodel", produces = {"application/json;charset=utf-8"})
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
    @RequestMapping( value = "/indicators", method = RequestMethod.POST)
    public Message add(HttpServletRequest req, @Valid @RequestBody IndicatorAddVO vo) throws Exception {
        LOGGER.info("indicatorAddVO : {}", vo);
        return Message.ok().data("count",indicatorService.addIndicator(vo,"1" ));
    }


    /**
     * 修改
     * @param req
     * @param id
     * @param vo
     * @return
     * @throws Exception
     */
    @RequestMapping( value = "/indicators/{id}", method = RequestMethod.PUT)
    public Message update(HttpServletRequest req, @PathVariable("id") Long id , @RequestBody IndicatorUpdateVO vo) throws Exception {
        LOGGER.info("update id : {}, indicatorUpdateVO : {}", id, vo);
        return Message.ok().data("count",indicatorService.updateIndicator(id,vo));
    }


    /**
     * 启用/禁用
     * @param req
     * @param id
     * @param vo
     * @return
     */
    @RequestMapping( value = "/indicators/enable/{id}", method = RequestMethod.PUT)
    public Message enable(HttpServletRequest req, @PathVariable("id") Long id, @RequestBody IndicatorEnableVO vo) {
        LOGGER.info("enable id : {}, vo : {}", id, vo);
        return Message.ok().data("count", indicatorService.enableIndicator(id, vo));
    }


    /**
     * 分页搜索
     * @param req
     * @return
     */
    @RequestMapping( value = "/indicators/list", method = RequestMethod.POST)
    public Message list(HttpServletRequest req, @RequestBody IndicatorQueryVO vo){
        LOGGER.info("list vo : {}",vo);
        return indicatorService.listIndicators(vo);
    }



    /**
     * 查看
     * @param req
     * @param id
     * @return
     */
    @RequestMapping( value = "/indicators/{id}", method = RequestMethod.GET)
    public Message query(HttpServletRequest req, @PathVariable("id") Long id) throws ErrorException {
        LOGGER.info("query id : {}", id);
        return indicatorService.queryById(id);
    }

    /**
     * 删除
     * @param req
     * @param id
     * @return
     */

    @RequestMapping( value = "/indicators/{id}", method = RequestMethod.DELETE)
    public Message delete(HttpServletRequest req, @PathVariable("id") Long id) throws ErrorException {
        LOGGER.info("delete id : {}", id);
        return Message.ok().data("count",indicatorService.deleteIndicator(id));
    }


    /**
     * 新增版本
     *
     * @param req
     * @param vo
     * @return
     * @throws IOException
     */
    @RequestMapping( value = "/indicators/versions/{id}", method = RequestMethod.POST)
    public Message addVersion(HttpServletRequest req, @PathVariable("id") Long id,@Valid @RequestBody IndicatorVersionAddVO vo) throws Exception {
        LOGGER.info("indicatorVersionAddVO : {}", vo);
        return Message.ok().data("count",indicatorService.addIndicatorVersion(id,vo));
    }



    /**
     *
     * 回退指定版本
     * @param req
     * @param vo
     * @return
     * @throws IOException
     */
    @RequestMapping( value = "/indicators/versions/rollback", method = RequestMethod.POST)
    public Message versionRollBack(HttpServletRequest req,@Valid @RequestBody IndicatorVersionRollBackVO vo) throws Exception {
        LOGGER.info("indicatorVersionRollBackVO : {}", vo);
        return Message.ok().data("count",indicatorService.versionRollBack(vo));
    }



    /**
     * 搜索指标版本
     * @param req
     * @return
     */
    @RequestMapping( value = "/indicators/versions/list", method = RequestMethod.POST)
    public Message indicatorVersionsList(HttpServletRequest req, @RequestBody IndicatorVersionQueryVO vo){
        LOGGER.info("version list vo : {}",vo);
        return indicatorService.listIndicatorVersions(vo);
    }


    /**
     * 指标相关主题可选列表
     * @param req
     * @return
     */
    @RequestMapping( value = "/themes/list", method = RequestMethod.POST)
    public Message themesList(HttpServletRequest req){
        ListDwThemeDomainAction action = ListDwThemeDomainAction.builder().setUser(getStrategyUser(req)).setIsAvailable(true).build();
        return Message.ok().data("list",governanceDwRemoteClient.listThemeDomains(action).getAll());
    }

    /**
     * 指标相关分层
     * @param req
     * @return
     */
    @RequestMapping( value = "/layers/list", method = RequestMethod.POST)
    public Message layerList(HttpServletRequest req,@RequestBody LayerVO vo){
        LOGGER.info("layerList vo : {}",vo);
        ListDwLayerAction action = ListDwLayerAction.builder().setIsAvailable(true).setDb(vo.getDbName()).setUser(getStrategyUser(req)).build();
        return Message.ok().data("list",governanceDwRemoteClient.listLayers(action).getAll());
    }

    /**
     * 指标相关周期列表
     * @param req
     * @return
     */

    @RequestMapping( value = "/cycles/list", method = RequestMethod.POST)
    public Message cycleList(HttpServletRequest req,@RequestBody CycleVO vo){
        LOGGER.info("cycleList vo : {}",vo);
        ListDwStatisticalPeriodAction action =ListDwStatisticalPeriodAction.builder().setUser(getStrategyUser(req)).setLayer(vo.getLayer()).setIsAvailable(true).setTheme(vo.getTheme()).build();
        return Message.ok().data("list",governanceDwRemoteClient.listStatisticalPeriods(action).getAll());
    }

    /**
     * 指标相关修饰词列表
     * @param req
     * @return
     */

    @RequestMapping( value = "/modifiers/list", method = RequestMethod.POST)
    public Message modifierList(HttpServletRequest req,@RequestBody ModifierVO vo){
        LOGGER.info("modifierList vo : {}",vo);
        ListDwModifierAction action = ListDwModifierAction.builder().setUser(getStrategyUser(req)).setLayer(vo.getLayer()).setIsAvailable(true).setTheme(vo.getTheme()).build();
        return Message.ok().data("list",governanceDwRemoteClient.listModifiers(action).getAll());
    }



    @RequestMapping( value = "/current/user", method = RequestMethod.POST)
    public Message currentUser(HttpServletRequest req){
        return Message.ok().data("user",getStrategyUser(req));
    }
}
