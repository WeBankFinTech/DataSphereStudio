package com.webank.wedatasphere.warehouse.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.warehouse.dao.domain.DwLayer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface DwLayerMapper extends BaseMapper<DwLayer> {
    @Select("select * from dss_datawarehouse_layer where name = #{layer} or en_name = #{layer}")
    Optional<DwLayer> findByName(@Param("layer") String layer);
}
