package com.webank.wedatasphere.dss.datamodel.table.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.datamodel.table.dto.TableQueryDTO;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;
import com.webank.wedatasphere.dss.datamodel.table.vo.*;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.server.Message;

public interface TableService extends IService<DssDatamodelTable> {

    /**
     * 新增表
     * @param vo
     * @return
     */
    int addTable(TableAddVO vo)  throws ErrorException;


    /**
     * 更新
     *
     * @param id
     * @param vo
     * @return
     * @throws ErrorException
     */
    int updateTable(Long id, TableUpdateVO vo) throws ErrorException;


    /**
     * 按表名搜索
     * @param vo
     * @return
     */
    TableQueryDTO queryByName(TableQueryOneVO vo)throws ErrorException;


    /**
     * 按id搜索
     * @param id
     * @return
     */
    TableQueryDTO queryById(Long id)throws ErrorException;


    /**
     * 新增版本
     * @param id
     * @return
     * @throws ErrorException
     */
    Integer addTableVersion(Long id, TableVersionAddVO vo) throws ErrorException;

    /**
     * 版本回退
     * @param vo
     * @return
     * @throws ErrorException
     */
    Integer versionRollBack(TableVersionRollBackVO vo) throws ErrorException;


    /**
     * 查询历史版本列表
     * @param vo
     * @return
     */
    Message listTableVersions(TableVersionQueryVO vo);


    /**
     * 添加我的收藏
     * @param vo
     * @return
     */
    Integer tableCollect(TableCollectVO vo) throws ErrorException;

    /**
     * 取消收藏
     * @param vo
     * @return
     * @throws ErrorException
     */
    Integer tableCancel(TableCollectCancelVO vo) throws ErrorException;

    /**
     * 基于收藏搜索
     * @param vo
     * @return
     * @throws ErrorException
     */
    Message tableCollections(TableCollectQueryVO vo) throws ErrorException;

    /**
     *
     * @param vo
     * @return
     * @throws ErrorException
     */
    Message dictionaryList(TableDictionaryListVO vo);


    /**
     * 增加字段
     * @param vo
     * @return
     */
    Integer addTableColumn(TableColumnsAddVO vo) throws ErrorException;


    /**
     * 字段绑定模型信息
     * @param columnId
     * @param vo
     * @return
     */
    Integer tableColumnBind(Long columnId, TableColumnBindVO vo)throws ErrorException;


    /**
     * 执行建表
     * @param vo
     * @return
     * @throws ErrorException
     */
    Integer tableCreate(TableCreateVO vo) throws ErrorException;



    /**
     * 生成建表语句
     * @param vo
     * @return
     * @throws ErrorException
     */
    String tableCreateSql(TableCreateSqlVO vo) throws ErrorException;


    /**
     * table 列表
     * @param vo
     * @return
     * @throws ErrorException
     */
    Message list(TableListVO vo);


    /**
     * 分区统计信息
     * @param vo
     * @return
     */
    Message listTablePartitionStats(TblPartitionStatsVO vo);


    /**
     * 库列表
     * @return
     * @param vo
     */
    Message listDataBases(TableDatabasesQueryVO vo);


    /**
     * 数据预览
     * @param vo
     * @return
     */
    Message previewData(TableDataPreviewVO vo) throws ErrorException;


    /**
     * 检测表状态
     * @param vo
     * @return
     */
    Integer tableCheckData(TableCheckDataVO vo) throws ErrorException;
}
