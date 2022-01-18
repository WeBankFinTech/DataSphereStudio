package com.webank.wedatasphere.dss.datamodel.indicator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.datamodel.center.common.exception.DSSDatamodelCenterException;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicator;
import com.webank.wedatasphere.dss.datamodel.indicator.vo.*;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.server.Message;


public interface IndicatorService extends IService<DssDatamodelIndicator> {

    /**
     * 新增
     * @param vo
     * @param version
     * @return
     */
    Long addIndicator(IndicatorAddVO vo, String version) throws ErrorException;



    /**
     * 更新
     * @param vo
     * @return
     * @throws ErrorException
     */
    int updateIndicator(Long id, IndicatorUpdateVO vo) throws ErrorException;



    /**
     * 启用/禁用
     * @param vo
     * @return
     */
    int enableIndicator(Long id, IndicatorEnableVO vo);



    /**
     * 删除
     * @param id
     * @return
     * @throws ErrorException
     */
    int deleteIndicator(Long id) throws ErrorException;



    /**
     * 查询列表
     * @param vo
     * @return
     */
    Message listIndicators(IndicatorQueryVO vo);


    /**
     * 查看
     * @param id
     * @return
     */
    Message queryById(Long id) throws DSSDatamodelCenterException;


    /**
     * 新增版本
     * @param id
     * @return
     */
    int addIndicatorVersion(Long id, IndicatorVersionAddVO vo) throws ErrorException;


    /**
     * 回退到某一版本
     * @param vo
     * @return
     * @throws ErrorException
     */
    int versionRollBack(IndicatorVersionRollBackVO vo) throws ErrorException;


    /**
     * 查询历史版本列表
     * @param vo
     * @return
     */
    Message listIndicatorVersions(IndicatorVersionQueryVO vo);


    /**
     * 主题引用情况
     * @param name
     * @return
     */
    int indicatorThemeReferenceCount(String name);

    /**
     * 分层引用情况
     * @param name
     * @return
     */
    int indicatorLayerReferenceCount(String name);


    /**
     * 周期引用情况
     * @param name
     * @return
     */
    int indicatorCycleReferenceCount(String name);


    /**
     * 修饰词引用情况
     * @param name
     * @return
     */
    int indicatorModifierReferenceCount(String name);


    /**
     * 维度引用情况
     * @param name
     * @return
     */
    int indicatorDimensionReferenceCount(String name);


    /**
     * 度量引用情况
     * @param name
     * @return
     */
    int indicatorMeasureReferenceCount(String name);


    /**
     * 指标引用指标情况
     * @param name
     * @return
     */
    int indicatorIndicatorReferenceCount(String name);
}
