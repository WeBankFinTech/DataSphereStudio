package com.webank.wedatasphere.dss.datamodel.dimension.dao;

import com.webank.wedatasphere.dss.datamodel.dimension.entity.DssDatamodelDimension;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DssDatamodelDimensionMapper  {
    int deleteByPrimaryKey(Long id);

    int insert(DssDatamodelDimension record);

    int insertSelective(DssDatamodelDimension record);

    DssDatamodelDimension selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DssDatamodelDimension record);

    int updateByPrimaryKey(DssDatamodelDimension record);
}