package com.webank.wedatasphere.dss.data.asset.restful;

import com.webank.wedatasphere.dss.data.asset.entity.HivePartInfo;
import com.webank.wedatasphere.dss.data.asset.entity.HiveStorageInfo;
import com.webank.wedatasphere.dss.data.asset.entity.HiveTblClassificationInfo;
import com.webank.wedatasphere.dss.data.asset.entity.HiveTblSimpleInfo;
import com.webank.wedatasphere.dss.data.asset.service.AssetService;
import com.webank.wedatasphere.dss.data.asset.service.WorkspaceInfoService;
import org.apache.linkis.server.Message;
import lombok.AllArgsConstructor;
import org.apache.atlas.model.instance.AtlasClassification;
import org.apache.atlas.model.lineage.AtlasLineageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Classname HiveTblRestful
 * @Description TODO
 * @Date 2021/8/19 13:53
 * @Created by suyc
 */
@Path("/dss/data/governance/asset/hiveTbl")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Component
@AllArgsConstructor
public class AssetTblRestful {
    private static final Logger logger = LoggerFactory.getLogger(AssetTblRestful.class);

    private static final String DEFAULT_DIRECTION = "BOTH";
    private static final String DEFAULT_DEPTH     = "3";
    private static final String DEFAULT_LIMIT     = "10";
    private static final String DEFAULT_OFFSET     = "0";

    private AssetService assetService;
    private WorkspaceInfoService workspaceInfoService;

    /**
     * 获取存储量前10的表信息
     */
    @GET
    @Path("/topStorage")
    public  Response getTop10Storage(@Context HttpServletRequest req) throws  Exception{
        List<HiveStorageInfo> top10Table = assetService.getTop10Table();
        for (HiveStorageInfo hiveStorageInfo : top10Table) {
            String qualifiedName=hiveStorageInfo.getTableName();
            String hiveTblGuid = assetService.getHiveTblGuid(qualifiedName);
            hiveStorageInfo.setGuid(hiveTblGuid);
        }
        return  Message.messageToResponse(Message.ok().data("result",top10Table));
    }

    /**
     * 搜索hive表
     */
    @GET
    @Path("/search")
    public Response searchHiveTbl(@QueryParam("classification") String classification,
                                  @QueryParam("query") @DefaultValue("") String query,
                                  @QueryParam("owner") @DefaultValue("")  String keyword,
                                  @QueryParam("limit") @DefaultValue(DEFAULT_LIMIT) int limit,
                                  @QueryParam("offset") @DefaultValue(DEFAULT_OFFSET) int offset) throws Exception {
        List<HiveTblSimpleInfo> hiveTblBasicList = assetService.searchHiveTable(classification,'*'+query+'*',limit,offset);
        if(hiveTblBasicList ==null || keyword ==null || keyword.trim().equals("")) {
            return Message.messageToResponse(Message.ok().data("result",hiveTblBasicList));
        }
        else {
            Pattern regex = Pattern.compile(keyword);
            return Message.messageToResponse(Message.ok().data("result",hiveTblBasicList.stream().filter(ele -> regex.matcher(ele.getOwner()).find()).collect(Collectors.toList())));
        }
    }

    /**
     * 获取单个表的详细信息，包括：基本信息、字段信息
     */
    @GET
    @Path("/{guid}/basic")
    public Response getHiveTblBasic(@PathParam("guid") String guid) throws Exception {
        return Message.messageToResponse(Message.ok().data("result",assetService.getHiveTblDetail(guid)));
    }

    /**
     * 获取表分区信息
     */
    @GET
    @Path("/{guid}/partition")
    public Response getHiveTblPartition(@PathParam("guid") String guid) throws Exception {
        List<HivePartInfo> hiveTblPartition = assetService.getHiveTblPartition(guid);
        if (hiveTblPartition.size()>0){
            return Message.messageToResponse(Message.ok().data("result",hiveTblPartition));
        }
        else {
            return Message.messageToResponse(Message.ok().data("result",null));
        }
    }

    /**
     * 获取表的血缘信息
     */
    @GET
    @Path("/{guid}/lineage")
    public Response getHiveTblLineage(@PathParam("guid") String guid,
                                    @QueryParam("direction") @DefaultValue(DEFAULT_DIRECTION) AtlasLineageInfo.LineageDirection direction,
                                    @QueryParam("depth") @DefaultValue(DEFAULT_DEPTH) int depth) throws Exception {
        return Message.messageToResponse(Message.ok().data("result",assetService.getHiveTblLineage(guid,direction,depth)));
    }

    /**
     * 获取表的select语句
     */
    @GET
    @Path("/{guid}/select")
    public Response getHiveTblSelect(@PathParam("guid") String guid) throws Exception {
        return  Message.messageToResponse(Message.ok().data("result",assetService.getTbSelect(guid)));
    }

    /**
     * 获取表的create语句
     */
    @GET
    @Path("/{guid}/create")
    public Response getHiveTblCreate(@PathParam("guid") String guid) throws Exception {
        return  Message.messageToResponse(Message.ok().data("result",assetService.getTbCreate(guid)));

    }

    /**
     * 获取分类
     */
    @GET
    @Path("/{guid}/classifications")
    public Response getClassifications(@PathParam("guid") String guid) throws Exception {
        return Message.messageToResponse(Message.ok().data("result",assetService.getClassifications(guid)));
    }

    /**
     * 添加分类
     */
    @POST
    @Path("/{guid}/classifications")
    @Deprecated
    public Response addClassifications(@PathParam("guid") String guid, @RequestBody List<AtlasClassification> classifications) throws Exception {
        assetService.addClassifications(guid, classifications);

        return Message.messageToResponse(Message.ok().data("result","添加成功"));
    }

//    /**
//     * 删除已有全部旧分类，并添加新分类
//     * linkis-gateway无法正常转换json为list
//     * [{"typeName": "test"},{"typeName": "DWD"}]  --->  List<AtlasClassification> classifications
//     * ["test","DWD"]  ---> List<String> typeNames
//     */
//    @PUT
//    @Path("/{guid}/classifications")
//    public Response removeAndAddNewClassifications(@PathParam("guid") String guid, @RequestBody List<AtlasClassification> classifications) throws Exception {
//        assetService.removeAndAddClassifications(guid, classifications);
//
//        return Message.messageToResponse(Message.ok().data("result","更新成功"));
//    }

    /**
     * 删除已有全部旧分类，并添加新分类
     * 支持 {"newClassifications":["test","DWD"]} 非顶层的List数组转换
     */
    @PUT
    @Path("/{guid}/classifications")
    public Response removeAndAddClassifications(@PathParam("guid") String guid, @RequestBody HiveTblClassificationInfo hiveTblClassificationInfo) throws Exception {
        List<AtlasClassification> newClassifications = new ArrayList<>();
        Optional.ofNullable(hiveTblClassificationInfo.getNewClassifications()).orElseGet(()-> {
            logger.warn("hive table uid is %s, newClassifications is null",guid);
            return new ArrayList<>();
        }).stream().filter(Objects::nonNull).forEach(typeName -> {
            AtlasClassification atlasClassification =new AtlasClassification(typeName);
            atlasClassification.setPropagate(false);
            atlasClassification.setRemovePropagationsOnEntityDelete(true);
            newClassifications.add(atlasClassification);
        });
        assetService.removeAndAddClassifications(guid, newClassifications);

        return Message.messageToResponse(Message.ok().data("result","更新成功"));
    }

    /**
     * 删除分类
     * @DELETE  linkis-gateway 不支持DELETE方式
     */
    @POST
    @Path("/{guid}/classification/{classificationName}")
    public Response deleteClassification(@PathParam("guid") String guid, @PathParam("classificationName") final String classificationName) throws Exception {
        assetService.deleteClassification(guid, classificationName);

        return Message.messageToResponse(Message.ok().data("result","删除成功"));
    }
}
