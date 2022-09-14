package com.webank.wedatasphere.warehouse.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.warehouse.dao.domain.DwThemeDomain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface DwThemeDomainMapper extends BaseMapper<DwThemeDomain> {
    @Select("select * from dss_datawarehouse_theme_domain where name = #{theme} or en_name = #{theme}")
    Optional<DwThemeDomain> findByName(@Param("theme") String theme);
}
