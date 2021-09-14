package com.webank.wedatasphere.dss.datamodel.dimension.restful;

import com.webank.wedatasphere.dss.datamodel.dimension.service.DimensionService;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionAddVO;
import com.webank.wedatasphere.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
public class DimensionRestfulApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(DimensionRestfulApi.class);

    @Autowired
    private DimensionService dimensionService;

    @POST
    @Path("/dimensions")
    public Response getView(@Context HttpServletRequest req, @RequestBody DimensionAddVO dimensionAddVO) throws IOException {
        LOGGER.info("dimensionAddVO : {}",dimensionAddVO.getName());
        dimensionService.addDimension(dimensionAddVO);
        return  Message.messageToResponse(Message.ok().data("fullTree","get"));
    }
}
