package com.webank.wedatasphere.dss.datamodel.dimension.dao;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import com.webank.wedatasphere.dss.datamodel.dimension.entity.DssDatamodelDimension;

public interface DssDatamodelDimensionMapper extends BaseMapper<DssDatamodelDimension> {
    int deleteByPrimaryKey(Long id);

    int insert(DssDatamodelDimension record);

    int insertSelective(DssDatamodelDimension record);

    DssDatamodelDimension selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DssDatamodelDimension record);

    int updateByPrimaryKey(DssDatamodelDimension record);

    int updateIsAvailableById(@Param("updatedIsAvailable")Integer updatedIsAvailable,@Param("id")Long id);

}