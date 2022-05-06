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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/dss/data/governance/asset/hiveTbl", produces = {"application/json"})
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
    @RequestMapping(method = RequestMethod.GET, path ="/topStorage")
    public  Message getTop10Storage() throws  Exception{
        List<HiveStorageInfo> top10Table = assetService.getTop10Table();
        for (HiveStorageInfo hiveStorageInfo : top10Table) {
            String qualifiedName=hiveStorageInfo.getTableName();
            String hiveTblGuid = assetService.getHiveTblGuid(qualifiedName);
            hiveStorageInfo.setGuid(hiveTblGuid);
        }
        return  Message.ok().data("result",top10Table);
    }

    /**
     * 搜索hive表
     */
    @RequestMapping(method = RequestMethod.GET, path ="/search")
    public Message searchHiveTbl(@RequestParam(required = false) String classification,
                                 @RequestParam(defaultValue = "") String query,
                                 @RequestParam(defaultValue = "")  String keyword,
                                 @RequestParam(defaultValue = DEFAULT_LIMIT) int limit,
                                 @RequestParam(defaultValue = DEFAULT_OFFSET) int offset) throws Exception {
        List<HiveTblSimpleInfo> hiveTblSimpleInfoList = assetService.searchHiveTable(classification,query.trim(),limit,offset);
        if(hiveTblSimpleInfoList ==null || keyword ==null || keyword.trim().equals("")) {
            return Message.ok().data("result",hiveTblSimpleInfoList);
        }
        else {
            Pattern regex = Pattern.compile(keyword);
            return Message.ok().data("result",hiveTblSimpleInfoList.stream().filter(ele -> regex.matcher(ele.getOwner()).find()).collect(Collectors.toList()));
        }
    }

    /**
     * 获取单个表的详细信息，包括：基本信息、字段信息
     */
    @RequestMapping(method = RequestMethod.GET, path ="/{guid}/basic")
    public Message getHiveTblBasic(@PathVariable String guid) throws Exception {
        return Message.ok().data("result",assetService.getHiveTblDetail(guid));
    }

    /**
     * 获取表分区信息
     */
    @RequestMapping(method = RequestMethod.GET, path ="/{guid}/partition")
    public Message getHiveTblPartition(@PathVariable String guid) throws Exception {
        List<HivePartInfo> hiveTblPartition = assetService.getHiveTblPartition(guid);
        if (hiveTblPartition.size()>0){
            return Message.ok().data("result",hiveTblPartition);
        }
        else {
            return Message.ok().data("result",null);
        }
    }

    /**
     * 获取表的血缘信息
     */
    @RequestMapping(method = RequestMethod.GET, path ="/{guid}/lineage")
    public Message getHiveTblLineage(@PathVariable String guid,
                                     @RequestParam(defaultValue = DEFAULT_DIRECTION) AtlasLineageInfo.LineageDirection direction,
                                     @RequestParam(defaultValue = DEFAULT_DEPTH) int depth) throws Exception {
        return Message.ok().data("result",assetService.getHiveTblLineage(guid,direction,depth));
    }

    /**
     * 获取表的select语句
     */
    @RequestMapping(method = RequestMethod.GET, path ="/{guid}/select")
    public Message getHiveTblSelect(@PathVariable String guid) throws Exception {
        return  Message.ok().data("result",assetService.getTbSelect(guid));
    }

    /**
     * 获取表的create语句
     */
    @RequestMapping(method = RequestMethod.GET, path ="/{guid}/create")
    public Message getHiveTblCreate(@PathVariable String guid) throws Exception {
        return  Message.ok().data("result",assetService.getTbCreate(guid));

    }

    /**
     * 获取分类
     */
    @RequestMapping(method = RequestMethod.GET, path ="/{guid}/classifications")
    public Message getClassifications(@PathVariable String guid) throws Exception {
        return Message.ok().data("result",assetService.getClassifications(guid));
    }

    /**
     * 添加分类
     */
    @Deprecated
    @RequestMapping(method = RequestMethod.POST, path ="/{guid}/classifications")
    public Message addClassifications(@PathVariable String guid, @RequestBody List<AtlasClassification> classifications) throws Exception {
        assetService.addClassifications(guid, classifications);
        return Message.ok().data("result","添加成功");
    }

//    /**
//     * 删除已有全部旧分类，并添加新分类
//     * linkis-gateway无法正常转换json为list
//     * [{"typeName": "test"},{"typeName": "DWD"}]  --->  List<AtlasClassification> classifications
//     * ["test","DWD"]  ---> List<String> typeNames
//     */
//    @RequestMapping(method = RequestMethod.PUT, path ="/{guid}/classifications")
//    public Message removeAndAddNewClassifications(@PathVariable String guid, @RequestBody List<AtlasClassification> classifications) throws Exception {
//        assetService.removeAndAddClassifications(guid, classifications);
//
//        return Message.ok().data("result","更新成功");
//    }

    /**
     * 删除已有全部旧分类，并添加新分类
     * 支持 {"newClassifications":["test","DWD"]} 非顶层的List数组转换
     */
    @RequestMapping(method = RequestMethod.PUT, path ="/{guid}/classifications")
    public Message removeAndAddClassifications(@PathVariable String guid, @RequestBody HiveTblClassificationInfo hiveTblClassificationInfo) throws Exception {
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

        return Message.ok().data("result","更新成功");
    }

    /**
     * 删除分类
     * @DELETE  linkis-gateway 不支持DELETE方式
     */
    @RequestMapping(method = RequestMethod.POST, path ="/{guid}/classification/{classificationName}")
    public Message deleteClassification(@PathVariable String guid, @PathVariable final String classificationName) throws Exception {
        assetService.deleteClassification(guid, classificationName);

        return Message.ok().data("result","删除成功");
    }
}
