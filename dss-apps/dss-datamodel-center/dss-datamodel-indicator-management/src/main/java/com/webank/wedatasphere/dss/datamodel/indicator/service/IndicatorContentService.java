package com.webank.wedatasphere.dss.datamodel.indicator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicatorContent;
import com.webank.wedatasphere.dss.datamodel.indicator.vo.IndicatorContentVO;
import org.apache.linkis.common.exception.ErrorException;

import java.util.List;


public interface IndicatorContentService extends IService<DssDatamodelIndicatorContent> {

    /**
     * 新增
     *
     * @param vo
     * @return
     */
    int addIndicatorContent(Long indicateId, String version, IndicatorContentVO vo) throws ErrorException;


    /**
     * 修改
     * @param indicateId
     * @param version
     * @param vo
     * @return
     * @throws ErrorException
     */
    int updateIndicatorContent(Long indicateId, String version, IndicatorContentVO vo) throws ErrorException;


    /**
     * 查看
     * @param indicateId
     * @return
     */
    DssDatamodelIndicatorContent queryByIndicateId(Long indicateId);


    /**
     * 根据indicator 删除指标详情
     * @param id
     * @return
     * @throws ErrorException
     */
    int deleteByIndicatorId(Long id)throws ErrorException;


    /**
     * 校验indicator引用情况
     * @param name
     * @return
     * @throws ErrorException
     */
    List<DssDatamodelIndicatorContent> sourceInfoReference(String name);


}
