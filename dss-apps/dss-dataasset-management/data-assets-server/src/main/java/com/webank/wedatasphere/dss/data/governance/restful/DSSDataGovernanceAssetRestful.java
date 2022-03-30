package com.webank.wedatasphere.dss.data.governance.restful;

import com.webank.wedatasphere.dss.data.governance.entity.*;
import com.webank.wedatasphere.dss.data.governance.service.AssetService;
import com.webank.wedatasphere.dss.data.governance.service.AuthenticationClientStrategy;
import com.webank.wedatasphere.dss.data.governance.service.WorkspaceInfoService;
import com.webank.wedatasphere.dss.data.governance.vo.*;
import com.webank.wedatasphere.dss.framework.workspace.client.impl.LinkisWorkSpaceRemoteClient;
import com.webank.wedatasphere.dss.framework.workspace.client.request.GetWorkspaceUsersAction;
import com.webank.wedatasphere.dss.framework.workspace.client.response.GetWorkspaceUsersResult;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.server.Message;
import org.apache.atlas.model.lineage.AtlasLineageInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;



@RestController
@RequestMapping(value = "/data-assets/asset", produces = {"application/json;charset=utf-8"})
public class DSSDataGovernanceAssetRestful implements AuthenticationClientStrategy {
    private static final Logger logger = LoggerFactory.getLogger(DSSDataGovernanceAssetRestful.class);

    private static final String DEFAULT_DIRECTION = "BOTH";
    private static final String DEFAULT_DEPTH = "3";
    private static final String DEFAULT_LIMIT = "25";
    private static final String DEFAULT_OFFSET = "0";

    @Autowired
    private AssetService assetService;
    @Autowired
    private WorkspaceInfoService workspaceInfoService;

    @Resource
    private LinkisWorkSpaceRemoteClient linkisWorkSpaceRemoteClient;

    /**
     * 获取数据资产概要：hivedb数、hivetable数据、总存储量
     */
    @RequestMapping( value = "/hiveSummary", method = RequestMethod.GET)
    public Message getHiveSummary(HttpServletRequest req) throws Exception {

        return Message.ok().data("result", assetService.getHiveSummary());
    }

    /**
     * 搜索hive表
     */

    @RequestMapping( value = "/hiveTbl/search", method = RequestMethod.GET)
    public Message searchHiveTbl(HttpServletRequest req,
                                 @RequestParam(value = "classification",required = false) String classification,
                                  @RequestParam(value = "query",required = false) String query,
                                  @RequestParam(value = "label",required = false) String label,
                                  @RequestParam(value="type",required = false) String type,
                                  @RequestParam(value="precise",defaultValue="0") int precise,
                                  @RequestParam(value="owner",defaultValue="")  String owner,
                                  @RequestParam(value="limit",defaultValue=DEFAULT_LIMIT )  int limit,
                                  @RequestParam(value="offset",defaultValue=DEFAULT_OFFSET ) int offset) throws Exception {

        //适配模型
        if (ClassificationConstant.isTypeScope(type)) {
            if (StringUtils.isNotBlank(classification)) {
                classification = ClassificationConstant.getPrefix(type).orElse(null) + classification;
            } else {
                classification = ClassificationConstant.getRoot(type).orElse(null);
            }
        }
        //适配标签
        if (!StringUtils.isBlank(label)){
            label = GlossaryConstant.LABEL.formatQuery(label);
        }
        //判断是否精确查询
        if (QueryType.PRECISE.getCode()!=precise){
            query = "*" + query + "*";
        }
        List<HiveTblSimpleInfo> hiveTblBasicList = assetService.searchHiveTable(classification, query,label,limit, offset);
        if (StringUtils.isBlank(owner) || owner.equals("undefined")|| CollectionUtils.isEmpty(hiveTblBasicList)) {
            return Message.ok().data("result", hiveTblBasicList);
        } else {
            List<HiveTblSimpleInfo> res = new ArrayList<>();
            for (HiveTblSimpleInfo hiveTblSimpleInfo : hiveTblBasicList) {
                if (hiveTblSimpleInfo.getOwner().equals(owner)) {
                    res.add(hiveTblSimpleInfo);
                }
            }
            return Message.ok().data("result", res);
        }
    }

    /**
     * 搜索hive表统计信息
     */

    @RequestMapping( value = "/hiveTbl/stats", method = RequestMethod.GET)
    public Message searchHiveTblStats(@RequestParam(value = "dbName",required = false) String dbName,
                                       @RequestParam(value = "tableName",required = false) String tableName,
                                       @RequestParam(value = "guid",required = false) String guid) throws Exception {
        logger.info("searchHiveTblStats dbName : {}, tableName : {}, guid : {}", dbName, tableName, guid);
        return Message.ok().data("result", assetService.hiveTblStats(dbName, tableName, guid));

    }

    /**
     * 搜索hive表容量
     */

    @RequestMapping( value = "/hiveTbl/size", method = RequestMethod.GET)
    public Message searchHiveTblSize(@RequestParam(value = "dbName",required = false) String dbName,
                                      @RequestParam(value = "tableName",required = false) String tableName,
                                      @RequestParam(value = "guid",required = false) String guid) throws Exception {
        logger.info("searchHiveTblSize dbName : {}, tableName : {}, guid : {}", dbName, tableName, guid);
        return Message.ok().data("result", assetService.hiveTblSize(dbName, tableName, guid));

    }


    /**
     * 搜索hive库
     */

    @RequestMapping( value = "/hiveDb/search", method = RequestMethod.GET)
    public Message searchHiveDb(@RequestParam(value = "classification",required = false) String classification,
                                 @RequestParam(value = "query",required = false) String query,
                                 @RequestParam(value="owner",defaultValue="")  String owner,
                                 @RequestParam(value="limit",defaultValue=DEFAULT_LIMIT)  int limit,
                                 @RequestParam(value="offset",defaultValue=DEFAULT_OFFSET)  int offset) throws Exception {

        List<HiveTblSimpleInfo> hiveTblBasicList = assetService.searchHiveDb(classification, '*' + query + '*', limit, offset);
        if (StringUtils.isBlank(owner) || owner.equals("undefined")) {
            return Message.ok().data("result", hiveTblBasicList);
        } else {
            List<HiveTblSimpleInfo> res = new ArrayList<>();
            for (HiveTblSimpleInfo hiveTblSimpleInfo : hiveTblBasicList) {
                if (hiveTblSimpleInfo.getOwner().equals(owner)) {
                    res.add(hiveTblSimpleInfo);
                }
            }
            return Message.ok().data("result", res);
        }
    }

    /**
     * 获取单个表的详细信息，包括：基本信息、字段信息
     */

    @RequestMapping( value = "/hiveTbl/{guid}/basic", method = RequestMethod.GET)
    public Message getHiveTblBasic(@PathVariable("guid") String guid) throws Exception {
        return Message.ok().data("result", assetService.getHiveTblDetail(guid));
    }

    /**
     * 获取表分区信息
     */

    @RequestMapping( value = "/hiveTbl/{guid}/partition", method = RequestMethod.GET)
    public Message getHiveTblPartition(@PathVariable("guid") String guid) throws Exception {
        List<PartInfo> hiveTblPartition = assetService.getHiveTblPartition(guid);
        if (hiveTblPartition.size() > 0) {
            return Message.ok().data("result", hiveTblPartition);
        } else {
            return Message.ok().data("result", null);
        }
    }

    /**
     * 根据表名获取表分区信息
     */

    @RequestMapping( value = "/hiveTbl/partition/name", method = RequestMethod.GET)
    public Message getHiveTblPartitionByName(@RequestParam("dbName") String dbName,@RequestParam("tableName") String tableName) throws Exception {
        logger.info("getHiveTblPartitionByName  dbName : {}, tableName : {}", dbName, tableName);
        List<PartInfo> hiveTblPartition = assetService.getHiveTblPartitionByName(dbName,tableName);
        return Message.ok().data("result", hiveTblPartition);
    }

    /**
     * 获取表的血缘信息
     */

    @RequestMapping( value = "/hiveTbl/{guid}/lineage", method = RequestMethod.GET)
    public Message getHiveTblLineage(@PathVariable("guid") String guid,
                                      @RequestParam(value = "direction",defaultValue = DEFAULT_DIRECTION) AtlasLineageInfo.LineageDirection direction,
                                      @RequestParam(value = "depth",defaultValue = DEFAULT_DEPTH)  int depth) throws Exception {
        return Message.ok().data("result", assetService.getHiveTblLineage(guid, direction, depth));
    }

    /**
     * 获取表的select语句
     */

    @RequestMapping( value = "/hiveTbl/{guid}/select", method = RequestMethod.GET)
    public Message getHiveTblSelect(@PathVariable("guid") String guid) throws Exception {


        return Message.ok().data("result", assetService.getTbSelect(guid));

    }

    /**
     * 获取表的create语句
     */

    @RequestMapping( value = "/hiveTbl/{guid}/create", method = RequestMethod.GET)
    public Message getHiveTblCreate(@PathVariable("guid") String guid) throws Exception {
        return Message.ok().data("result", assetService.getTbCreate(guid));

    }

    /**
     * 获取存储量前10的表信息
     */

    @RequestMapping( value = "/hiveTbl/topStorage", method = RequestMethod.GET)
    public Message getTop10Storage(HttpServletRequest req) throws Exception {
        List<TableInfo> top10Table = assetService.getTop10Table();
        return Message.ok().data("result", top10Table);
    }

    /**
     * 根据标签和日期获取存储量前10的表信息
     */

    @RequestMapping( value = "/hiveTbl/topStorageByLabel", method = RequestMethod.GET)
    public Message getTop10StorageByLabel(@RequestParam(value = "label",defaultValue = "")  String label,
                                           @RequestParam(value = "startDate",required = false) String startDate,
                                           @RequestParam(value = "endDate",required = false) String endDate) {
        try{
            List<TableInfo> top10Table = assetService.getTop10TableByLabelDay(label,startDate,endDate);
            return Message.ok().data("result", top10Table);
        }catch (Exception e){
            logger.error("topStorageByLabel msg:{}", e.getMessage());
        }
        return Message.ok();
    }

    /**
     * 修改单个表或单个列注释
     */

    @RequestMapping( value = "/comment/{guid}", method = RequestMethod.PUT)
    public Message modifyComment(@PathVariable("guid") String guid, @RequestParam(value = "comment",required = false) String comment) throws Exception {
        comment = "\"" + comment + "\"";
        assetService.modifyComment(guid, comment);
        return Message.ok().data("result", "修改成功");
    }

    /**
     * 批量修改多个个表或列注释
     */
    @RequestMapping( value = "/comment/bulk", method = RequestMethod.PUT)
    public Message modifyComment(@RequestBody Map<String, String> commentMap) throws Exception {
        for (Map.Entry<String, String> stringStringEntry : commentMap.entrySet()) {
            stringStringEntry.setValue("\"" + stringStringEntry.getValue() + "\"");
        }
        assetService.bulkModifyComment(commentMap);

        return Message.ok().data("result", "修改成功");
    }

    /**
     * 创建模型
     *
     * @param req
     * @param vo
     * @return
     * @throws Exception
     */

    @RequestMapping( value = "/model/type", method = RequestMethod.POST)
    public Message createModelType(HttpServletRequest req, @RequestBody CreateModelTypeVO vo) throws Exception {
        logger.info("createModelType : {}", vo);
        return Message.ok().data("result", assetService.createModelType(vo));
    }

    /**
     * 删除模型
     *
     * @param req
     * @param vo
     * @return
     * @throws Exception
     */

    @RequestMapping( value = "/model/type/delete", method = RequestMethod.POST)
    public Message deleteModelType(HttpServletRequest req, @RequestBody DeleteModelTypeVO vo) throws Exception {
        logger.info("deleteModelTypeVO : {}", vo);
        assetService.deleteModelType(vo);
        return Message.ok().data("result", "删除成功");
    }


    /**
     * 绑定模型
     *
     * @param req
     * @param vo
     * @return
     * @throws Exception
     */

    @RequestMapping( value = "/model/bind", method = RequestMethod.POST)
    public Message bindModelType( HttpServletRequest req, @RequestBody BindModelVO vo) throws Exception {
        logger.info("bindModelVO : {}", vo);
        assetService.bindModelType(vo);
        return Message.ok().data("result", "绑定成功");
    }

    /**
     * 解绑模型
     *
     * @param req
     * @param vo
     * @return
     * @throws Exception
     */

    @RequestMapping( value = "/model/unbind", method = RequestMethod.POST)
    public Message unBindModelType(HttpServletRequest req, @RequestBody UnBindModelVO vo) throws Exception {
        logger.info("unBindModelVO : {}", vo);
        assetService.unBindModel(vo);
        return Message.ok().data("result", "解绑成功");
    }


    /**
     * 更新模型
     *
     * @param req
     * @param vo
     * @return
     * @throws Exception
     */

    @RequestMapping( value = "/model/type/modify", method = RequestMethod.POST)
    public Message updateModelType(HttpServletRequest req, @RequestBody UpdateModelTypeVO vo) throws Exception {
        logger.info("updateModelTypeVO : {}", vo);
        return Message.ok().data("result", assetService.updateModelType(vo));
    }

    /**
     * 创建标签
     *
     * @param req
     * @param vo
     * @return
     * @throws Exception
     */

    @RequestMapping( value = "/labels", method = RequestMethod.POST)
    public Message createLabel(HttpServletRequest req, @RequestBody CreateLabelVO vo) throws Exception {
        logger.info("createLabel vo : {}", vo);
        return Message.ok().data("result", assetService.createLabel(vo));
    }

    /**
     * 更新标签
     *
     * @param req
     * @param vo
     * @return
     * @throws Exception
     */

    @RequestMapping( value = "/labels/modify", method = RequestMethod.POST)
    public Message updateLabel(HttpServletRequest req, @RequestBody UpdateLabelVO vo) throws Exception {
        logger.info("updateLabel vo : {}", vo);
        return Message.ok().data("result", assetService.updateLabel(vo));
    }

    /**
     * 删除标签
     *
     * @param req
     * @param vo
     * @return
     * @throws Exception
     */

    @RequestMapping( value = "/labels/delete", method = RequestMethod.POST)
    public Message deleteLabel(HttpServletRequest req, @RequestBody DeleteLabelVO vo) throws Exception {
        logger.info("deleteLabel vo : {}", vo);
        assetService.deleteLabel(vo);
        return Message.ok().data("result", "删除成功");
    }

    /**
     * 实体绑定标签
     *
     * @param req
     * @param vo
     * @return
     * @throws Exception
     */

    @RequestMapping( value = "/labels/bind", method = RequestMethod.POST)
    public Message bindLabel(HttpServletRequest req, @RequestBody BindLabelVO vo) throws Exception {
        logger.info("bindLabel vo : {}", vo);
        assetService.bindLabel(vo);
        return Message.ok().data("result", "绑定成功");
    }


    /**
     * 实体解绑标签
     *
     * @param req
     * @param vo
     * @return
     * @throws Exception
     */

    @RequestMapping( value = "/labels/unbind", method = RequestMethod.POST)
    public Message unBindLabel(HttpServletRequest req, @RequestBody UnBindLabelVO vo) throws Exception {
        logger.info("unBindLabel vo : {}", vo);
        assetService.unBindLabel(vo);
        return Message.ok().data("result", "解绑成功");
    }


    /**
     * 搜索标签
     *
     * @param req
     * @param query
     * @return
     * @throws Exception
     */

    @RequestMapping( value = "/labels/search", method = RequestMethod.GET)
    public Message searchLabel(HttpServletRequest req
                                    , @RequestParam(value = "query",required = false) String query
                                    , @RequestParam(value = "limit", defaultValue=DEFAULT_LIMIT)  int limit
                                    , @RequestParam(value = "offset",defaultValue=DEFAULT_OFFSET) int offset) throws Exception {
        logger.info("searchLabel query : {}", query);
        return Message.ok().data("result",assetService.listLabels(query,limit,offset));
    }

    /**
     * 设置单个表或单个列的标签
     */

    @RequestMapping( value = "/label/{guid}", method = RequestMethod.POST)
    public Message setLabels(@PathVariable("guid") String guid, @RequestBody Set<String> labels) throws Exception {
        logger.info("setLabels guid : {}, labels : {}", guid, labels);
        assetService.setLabels(guid, labels);

        return Message.ok().data("result", "设置成功");
    }

    /**
     * 获取工作空间下所有用户名
     */

    @RequestMapping( value = "getWorkspaceUsers/{workspaceId}/{search}", method = RequestMethod.GET)
    public Message getWorkspaceUsers(@PathVariable("workspaceId") int workspaceId, @PathVariable("search") String search) throws Exception {
        String searchs = "%" + search + "%";
        List<String> workspaceUsers = workspaceInfoService.getWorkspaceUsers(workspaceId, searchs);
        return Message.ok().data("result", workspaceUsers);

    }

    /**
     * 查询用户
     *
     * @param req
     * @param workspaceId
     * @return
     */

    @RequestMapping( value = "/users/{workspaceId}", method = RequestMethod.GET)
    public Message users(HttpServletRequest req, @PathVariable("workspaceId") String workspaceId) throws ErrorException {
        logger.info("users workspaceId : {}", workspaceId);
        GetWorkspaceUsersResult result = linkisWorkSpaceRemoteClient.getWorkspaceUsers(GetWorkspaceUsersAction.builder().setUser(getStrategyUser(req)).setWorkspaceId(workspaceId).build());
        return Message.ok().data("users", result.getWorkspaceUserList());
    }


    /**
     * 查询角色
     *
     * @param req
     * @param workspaceId
     * @return
     */

    @RequestMapping( value = "/roles/{workspaceId}", method = RequestMethod.GET)
    public Message roles(HttpServletRequest req, @PathVariable("workspaceId") String workspaceId) throws ErrorException {
        logger.info("roles workspaceId : {}", workspaceId);
        GetWorkspaceUsersResult result = linkisWorkSpaceRemoteClient.getWorkspaceUsers(GetWorkspaceUsersAction.builder().setUser(getStrategyUser(req)).setWorkspaceId(workspaceId).build());
        return Message.ok().data("users", result.getWorkspaceRoleList());
    }


}
