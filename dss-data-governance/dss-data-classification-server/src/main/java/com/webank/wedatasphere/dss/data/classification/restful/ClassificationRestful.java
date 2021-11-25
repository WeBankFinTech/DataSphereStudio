package com.webank.wedatasphere.dss.data.classification.restful;

import com.webank.wedatasphere.dss.data.classification.service.ClassificationService;
import com.webank.wedatasphere.dss.data.common.conf.AtlasConf;
import com.webank.wedatasphere.linkis.server.Message;
import lombok.AllArgsConstructor;
import org.apache.atlas.model.typedef.AtlasClassificationDef;
import org.apache.atlas.model.typedef.AtlasTypesDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author suyc
 * @Classname ClassificationRestful
 * @Description TODO
 * @Date 2021/9/24 10:35
 * @Created by suyc
 */
@Path("/dss/data/governance/classification")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Component
@AllArgsConstructor
public class ClassificationRestful {
    private static final Logger logger = LoggerFactory.getLogger(ClassificationRestful.class);

    private ClassificationService classificationService;

    /**
     * 获取所有分类
     */
    @GET
    @Path("/all")
    public Response getClassificationDef(@Context HttpServletRequest req) throws Exception {
        return Message.messageToResponse(Message.ok().data("result", classificationService.getClassificationDef()));
    }

    /**
     * 根据名称获取分类,不包括包括子分类
     */
    @GET
    @Path("/{name}")
    public Response getClassificationDef(@Context HttpServletRequest req, @PathParam("name") String name) throws Exception {
        return Message.messageToResponse(Message.ok().data("result",classificationService.getClassificationDefByName(name)));
    }

    /**
     * 根据名称获取分类,包括一级子分类
     * keyword 按照分类名称模糊搜索
     */
    @GET
    @Path("/{name}/subtypes")
    public Response getClassificationDefList(@Context HttpServletRequest req,
                                             @PathParam("name") String name,
                                             @QueryParam("keyword") @DefaultValue("")  String keyword) throws Exception {
        List<AtlasClassificationDef> atlasClassificationDefList = classificationService.getClassificationDefListByName(name);
        if(atlasClassificationDefList ==null || keyword ==null || keyword.trim().equalsIgnoreCase("")) {
            return Message.messageToResponse(Message.ok().data("result", atlasClassificationDefList));
        }
        else{
            Pattern regex = Pattern.compile(keyword);
            return Message.messageToResponse(Message.ok().data("result",atlasClassificationDefList.stream().filter(ele -> regex.matcher(ele.getName()).find()).collect(Collectors.toList())));
        }
    }

    /**
     * 获取所有分层的一级子类型，包括系统预置分层和用户自定义分层
     */
    @GET
    @Path("/layers/all")
    public Response getClassificationDefListForLayer(@Context HttpServletRequest req) throws Exception {
        return Message.messageToResponse(Message.ok().data("result",classificationService.getClassificationDefListForLayer()));
    }

    /**
     * 创建新的分类
     */
    @POST
    @Path("/batch")
    public Response createClassificationDef(@Context HttpServletRequest req, AtlasTypesDef typesDef) throws Exception {
        return Message.messageToResponse(Message.ok().data("result", classificationService.createAtlasTypeDefs(typesDef)));
    }

    /**
     * 更新的分类
     */
    @PUT
    @Path("/batch")
    public Response updateClassificationDef(@Context HttpServletRequest req, AtlasTypesDef typesDef) throws Exception {
        classificationService.updateAtlasTypeDefs(typesDef);
        return Message.messageToResponse(Message.ok().data("result","更新成功" ));
    }

    /**
     * 删除分类
     * @DELETE  linkis-gateway 不支持DELETE方式
     */
    @POST
    @Path("/{name}/delete")
    public Response deleteClassificationDef(@Context HttpServletRequest req, @PathParam("name") String name) throws Exception {
        classificationService.deleteClassificationDefByName(name);

        return Message.messageToResponse(Message.ok().data("result","删除成功"));
    }
}
