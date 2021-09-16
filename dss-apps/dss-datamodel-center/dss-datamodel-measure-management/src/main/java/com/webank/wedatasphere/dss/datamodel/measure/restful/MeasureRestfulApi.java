package com.webank.wedatasphere.dss.datamodel.measure.restful;

import com.webank.wedatasphere.dss.datamodel.center.common.exception.DSSDatamodelCenterException;
import com.webank.wedatasphere.dss.datamodel.measure.service.MeasureService;
import com.webank.wedatasphere.dss.datamodel.measure.vo.MeasureAddVO;
import com.webank.wedatasphere.dss.datamodel.measure.vo.MeasureEnableVO;
import com.webank.wedatasphere.dss.datamodel.measure.vo.MeasureQueryVO;
import com.webank.wedatasphere.dss.datamodel.measure.vo.MeasureUpdateVO;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
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
 * @author Dylan
 * @date 2021/9/14
 */
@Component
@Path("/datamodel/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MeasureRestfulApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasureRestfulApi.class);

    @Autowired
    private MeasureService measureService;

    /**
     * 新增维度
     *
     * @param req
     * @param vo
     * @return
     * @throws IOException
     */
    @POST
    @Path("/measures")
    public Response add(@Context HttpServletRequest req, @RequestBody MeasureAddVO vo) throws IOException {
        LOGGER.info("measureAddVO : {}", vo);
        String userName = SecurityFilter.getLoginUsername(req);
        return Message.messageToResponse(Message.ok().data("count", measureService.addMeasure(vo)));
    }

    /**
     * 启用/禁用
     * @param req
     * @param id
     * @param vo
     * @return
     */
    @PUT
    @Path("/measures/enable/{id}")
    public Response enable(@Context HttpServletRequest req, @PathParam("id") Long id, @RequestBody MeasureEnableVO vo) {
        LOGGER.info("enable id : {}, vo : {}", id, vo);
        return Message.messageToResponse(Message.ok().data("count", measureService.enableMeasure(id, vo)));
    }

    /**
     * 修改
     * @param req
     * @param id
     * @param vo
     * @return
     */
    @PUT
    @Path("/measures/{id}")
    public Response update(@Context HttpServletRequest req, @PathParam("id") Long id, @RequestBody MeasureUpdateVO vo) {
        LOGGER.info("update id : {}, vo : {}", id, vo);
        return Message.messageToResponse(Message.ok().data("count",measureService.updateMeasure(id,vo)));
    }


    /**
     * 查看
     * @param req
     * @param id
     * @return
     */
    @GET
    @Path("/measures/{id}")
    public Response query(@Context HttpServletRequest req, @PathParam("id") Long id) throws ErrorException {
        LOGGER.info("query id : {}", id);
        return Message.messageToResponse(Message.ok().data("detail",measureService.queryById(id)));
    }

    /**
     * 删除
     * @param req
     * @param id
     * @return
     */
    @DELETE
    @Path("/measures/{id}")
    public Response delete(@Context HttpServletRequest req, @PathParam("id") Long id) {
        LOGGER.info("delete id : {}", id);
        return Message.messageToResponse(Message.ok().data("count",measureService.deleteMeasure(id)));
    }

    /**
     * 分页搜索
     * @param req
     * @return
     */
    @POST
    @Path("/measures/list")
    public Response list(@Context HttpServletRequest req,@RequestBody MeasureQueryVO vo){
        LOGGER.info("list vo : {}",vo);
        return Message.messageToResponse(measureService.listMeasures(vo));
    }
}
