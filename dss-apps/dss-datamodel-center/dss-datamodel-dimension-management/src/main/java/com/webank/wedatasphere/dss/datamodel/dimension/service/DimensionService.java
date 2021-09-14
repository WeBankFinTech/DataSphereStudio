package com.webank.wedatasphere.dss.datamodel.dimension.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.datamodel.dimension.entity.DssDatamodelDimension;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionAddVO;

/**
 * @author helong
 * @date 2021/9/14
 */
public interface DimensionService {

    /**
     * 新增维度
     * @param vo
     * @return
     */
    int addDimension(DimensionAddVO vo);
}
