package com.webank.wedatasphere.dss.datamodel.dimension.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.datamodel.dimension.dto.DimensionQueryDTO;
import com.webank.wedatasphere.dss.datamodel.dimension.entity.DssDatamodelDimension;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionAddVO;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionEnableVO;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionQueryVO;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionUpdateVO;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.server.Message;


public interface DimensionService extends IService<DssDatamodelDimension> {

    /**
     * 新增维度
     * @param vo
     * @return
     */
    long addDimension(DimensionAddVO vo)throws ErrorException;

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
    int updateDimension(Long id, DimensionUpdateVO vo) throws ErrorException;


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


    /**
     * 主题引用情况
     * @param name
     * @return
     */
    int dimensionThemeReferenceCount(String name);
}
