package com.webank.wedatasphere.dss.datamodel.dimension.restful;

import com.webank.wedatasphere.dss.datamodel.dimension.service.DimensionService;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionAddVO;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionEnableVO;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionQueryVO;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionUpdateVO;
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
 * @date 2021/9/14
 */
@Component
@Path("/datamodel/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DimensionRestfulApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(DimensionRestfulApi.class);

    @Autowired
    private DimensionService dimensionService;

    /**
     * 新增
     *
     * @param req
     * @param vo
     * @return
     * @throws IOException
     */
    @POST
    @Path("/dimensions")
    public Response add(@Context HttpServletRequest req, @RequestBody DimensionAddVO vo) throws IOException {
        LOGGER.info("dimensionAddVO : {}", vo);
        return Message.messageToResponse(Message.ok().data("count", dimensionService.addDimension(vo)));
    }

    /**
     * 启用/禁用
     * @param req
     * @param id
     * @param vo
     * @return
     */
    @PUT
    @Path("/dimensions/enable/{id}")
    public Response enable(@Context HttpServletRequest req, @PathParam("id") Long id, @RequestBody DimensionEnableVO vo) {
        LOGGER.info("enable id : {}, vo : {}", id, vo);
        return Message.messageToResponse(Message.ok().data("count", dimensionService.enableDimension(id, vo)));
    }

    /**
     * 修改
     * @param req
     * @param id
     * @param vo
     * @return
     */
    @PUT
    @Path("/dimensions/{id}")
    public Response update(@Context HttpServletRequest req, @PathParam("id") Long id, @RequestBody DimensionUpdateVO vo) {
        LOGGER.info("update id : {}, vo : {}", id, vo);
        return Message.messageToResponse(Message.ok().data("count",dimensionService.updateDimension(id,vo)));
    }

    /**
     * 删除
     * @param req
     * @param id
     * @return
     */
    @DELETE
    @Path("/dimensions/{id}")
    public Response delete(@Context HttpServletRequest req, @PathParam("id") Long id) {
        LOGGER.info("delete id : {}", id);
        return Message.messageToResponse(Message.ok().data("count",dimensionService.deleteDimension(id)));
    }

    /**
     * 分页搜索
     * @param req
     * @return
     */
    @POST
    @Path("/dimensions/list")
    public Response list(@Context HttpServletRequest req,@RequestBody DimensionQueryVO vo){
        LOGGER.info("list vo : {}",vo);
        return Message.messageToResponse(dimensionService.listDimensions(vo));
    }


    /**
     * 查看
     * @param req
     * @param id
     * @return
     */
    @GET
    @Path("/dimensions/{id}")
    public Response query(@Context HttpServletRequest req, @PathParam("id") Long id) throws ErrorException {
        LOGGER.info("query id : {}", id);
        return Message.messageToResponse(Message.ok().data("detail",dimensionService.queryById(id)));
    }
}
