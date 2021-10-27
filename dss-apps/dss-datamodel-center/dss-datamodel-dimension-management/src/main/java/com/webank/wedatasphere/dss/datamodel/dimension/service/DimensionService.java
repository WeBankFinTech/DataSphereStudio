package com.webank.wedatasphere.dss.datamodel.dimension.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.datamodel.center.common.exception.DSSDatamodelCenterException;
import com.webank.wedatasphere.dss.datamodel.dimension.dto.DimensionQueryDTO;
import com.webank.wedatasphere.dss.datamodel.dimension.entity.DssDatamodelDimension;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionAddVO;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionEnableVO;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionQueryVO;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionUpdateVO;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.server.Message;


public interface DimensionService extends IService<DssDatamodelDimension> {

    /**
     * 新增维度
     * @param vo
     * @return
     */
    int addDimension(DimensionAddVO vo);

    /**
     * 启用/禁用
     * @param vo
     * @return
     */
    int enableDimension(Long id, DimensionEnableVO vo);


    /**
     * 更新
     * @param id
     * @param vo
     * @return
     */
    int updateDimension(Long id, DimensionUpdateVO vo);


    /**
     * 删除
     * @param id
     * @return
     */
    int deleteDimension(Long id) throws ErrorException;


    /**
     * 查看
     * @param id
     * @return
     */
    DimensionQueryDTO queryById(Long id) throws ErrorException;


    /**
     * 查询列表
     * @param vo
     * @return
     */
    Message listDimensions(DimensionQueryVO vo);
}
