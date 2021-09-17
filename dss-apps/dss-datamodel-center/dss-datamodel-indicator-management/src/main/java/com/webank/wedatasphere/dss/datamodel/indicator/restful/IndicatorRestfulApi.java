package com.webank.wedatasphere.dss.datamodel.indicator.restful;

import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorService;
import com.webank.wedatasphere.dss.datamodel.indicator.vo.IndicatorAddVO;
import com.webank.wedatasphere.dss.datamodel.indicator.vo.IndicatorEnableVO;
import com.webank.wedatasphere.dss.datamodel.indicator.vo.IndicatorQueryVO;
import com.webank.wedatasphere.dss.datamodel.indicator.vo.IndicatorUpdateVO;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.server.Message;
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

/**
 * @author helong
 * @date 2021/9/16
 */

@Component
@Path("/datamodel/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IndicatorRestfulApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorRestfulApi.class);

    @Autowired
    private IndicatorService indicatorService;

    /**
     * 新增维度
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
        return Message.messageToResponse(Message.ok().data("count",indicatorService.addIndicator(vo,"v1" )));
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
        LOGGER.info("indicatorAddVO : {}", vo);
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
        return null;//Message.messageToResponse(Message.ok().data("count", dimensionService.enableDimension(id, vo)));
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
        return Message.messageToResponse(Message.ok().data("detail",indicatorService.queryById(id)));
    }
}
