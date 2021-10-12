package com.webank.wedatasphere.dss.datamodel.table.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.datamodel.table.dto.TableQueryDTO;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;
import com.webank.wedatasphere.dss.datamodel.table.vo.TableAddVO;
import com.webank.wedatasphere.dss.datamodel.table.vo.TableQueryOneVO;
import com.webank.wedatasphere.dss.datamodel.table.vo.TableUpdateVO;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;

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
    TableQueryDTO queryByName(TableQueryOneVO vo);


    /**
     * 按id搜索
     * @param id
     * @return
     */
    TableQueryDTO queryById(Long id);
}
