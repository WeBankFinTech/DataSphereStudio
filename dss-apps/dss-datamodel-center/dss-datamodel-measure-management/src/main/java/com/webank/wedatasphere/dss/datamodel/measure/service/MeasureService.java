package com.webank.wedatasphere.dss.datamodel.measure.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.datamodel.measure.entity.DssDatamodelMeasure;
import com.webank.wedatasphere.dss.datamodel.measure.vo.MeasureAddVO;
import com.webank.wedatasphere.dss.datamodel.measure.vo.MeasureEnableVO;
import com.webank.wedatasphere.dss.datamodel.measure.vo.MeasureQueryVO;
import com.webank.wedatasphere.dss.datamodel.measure.vo.MeasureUpdateVO;
import com.webank.wedatasphere.linkis.server.Message;

/**
 * @author helong
 * @date 2021/9/14
 */
public interface MeasureService extends IService<DssDatamodelMeasure> {

    /**
     * 新增维度
     * @param vo
     * @return
     */
    int addMeasure(MeasureAddVO vo);

    /**
     * 启用/禁用
     * @param vo
     * @return
     */
    int enableMeasure(Long id, MeasureEnableVO vo);


    /**
     * 更新
     * @param id
     * @param vo
     * @return
     */
    int updateMeasure(Long id, MeasureUpdateVO vo);


    /**
     * 删除
     * @param id
     * @return
     */
    int deleteMeasure(Long id);


    /**
     * 查询列表
     * @param vo
     * @return
     */
    Message listMeasures(MeasureQueryVO vo);
}
