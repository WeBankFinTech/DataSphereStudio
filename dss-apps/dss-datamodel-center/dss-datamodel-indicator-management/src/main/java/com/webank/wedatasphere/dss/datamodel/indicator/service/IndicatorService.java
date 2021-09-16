package com.webank.wedatasphere.dss.datamodel.indicator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicator;
import com.webank.wedatasphere.dss.datamodel.indicator.vo.IndicatorAddVO;
import com.webank.wedatasphere.dss.datamodel.indicator.vo.IndicatorQueryVO;
import com.webank.wedatasphere.dss.datamodel.indicator.vo.IndicatorUpdateVO;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.server.Message;

/**
 * @author helong
 * @date 2021/9/16
 */
public interface IndicatorService extends IService<DssDatamodelIndicator> {

    /**
     * 新增
     * @param vo
     * @param version
     * @return
     */
    int addIndicator(IndicatorAddVO vo, String version) throws ErrorException;

    /**
     * 更新
     * @param vo
     * @return
     * @throws ErrorException
     */
    int updateIndicator(Long id, IndicatorUpdateVO vo) throws ErrorException;


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
}
