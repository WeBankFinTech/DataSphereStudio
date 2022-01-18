package com.webank.wedatasphere.dss.datamodel.table.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.datamodel.table.dto.LabelQueryDTO;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelLabel;
import com.webank.wedatasphere.dss.datamodel.table.vo.LabelAddVO;
import com.webank.wedatasphere.dss.datamodel.table.vo.LabelEnableVO;
import com.webank.wedatasphere.dss.datamodel.table.vo.LabelUpdateVO;
import com.webank.wedatasphere.dss.datamodel.table.vo.LabelsQueryVO;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.server.Message;

public interface LabelService extends IService<DssDatamodelLabel> {

    /**
     * 新增标签
     * @param vo
     * @return
     * @throws ErrorException
     */
    Long add(LabelAddVO vo)  throws ErrorException;


    /**
     * 更新标签
     * @return
     * @throws ErrorException
     */
    int update(Long id, LabelUpdateVO vo) throws ErrorException;


    /**
     * 查看标签
     * @param id
     * @return
     * @throws ErrorException
     */
    LabelQueryDTO query(Long id) throws ErrorException;


    /**
     * 启用/禁用
     * @param id
     * @param vo
     * @return
     * @throws ErrorException
     */
    int enable(Long id, LabelEnableVO vo) throws ErrorException;


    /**
     * 删除标签
     * @param id
     * @return
     * @throws ErrorException
     */
    int delete(Long id) throws ErrorException;


    /**
     * 查询标签
     * @param vo
     * @return
     * @throws ErrorException
     */
    Message list(LabelsQueryVO vo) throws ErrorException;


    /**
     * 标签引用主题统计
     * @param themeName
     * @return
     * @throws ErrorException
     */
    int labelThemeReferenceCount(String themeName);
}
