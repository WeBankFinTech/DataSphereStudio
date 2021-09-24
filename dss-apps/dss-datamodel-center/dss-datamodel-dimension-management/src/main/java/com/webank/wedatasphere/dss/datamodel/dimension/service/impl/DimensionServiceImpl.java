package com.webank.wedatasphere.dss.datamodel.dimension.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.ErrorCode;
import com.webank.wedatasphere.dss.datamodel.center.common.exception.DSSDatamodelCenterException;
import com.webank.wedatasphere.dss.datamodel.dimension.dao.DssDatamodelDimensionMapper;
import com.webank.wedatasphere.dss.datamodel.dimension.dto.DimensionQueryDTO;
import com.webank.wedatasphere.dss.datamodel.dimension.entity.DssDatamodelDimension;
import com.webank.wedatasphere.dss.datamodel.dimension.service.DimensionService;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionAddVO;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionEnableVO;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionQueryVO;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionUpdateVO;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.server.Message;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.stream.Collectors;



@Service
public class DimensionServiceImpl extends ServiceImpl<DssDatamodelDimensionMapper,DssDatamodelDimension>  implements DimensionService {


    private final ModelMapper modelMapper = new ModelMapper();


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addDimension(DimensionAddVO vo) {
        DssDatamodelDimension newOne = modelMapper.map(vo,DssDatamodelDimension.class);
        newOne.setCreateTime(new Date());
        newOne.setUpdateTime(new Date());
        return getBaseMapper().insert(newOne);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int enableDimension(Long id ,DimensionEnableVO vo) {
        DssDatamodelDimension enableOne = new DssDatamodelDimension();
        enableOne.setIsAvailable(vo.getIsAvailable());
        enableOne.setUpdateTime(new Date());
        return getBaseMapper().update(enableOne, Wrappers.<DssDatamodelDimension>lambdaUpdate().eq(DssDatamodelDimension::getId,id));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDimension(Long id, DimensionUpdateVO vo) {
        DssDatamodelDimension updateOne =modelMapper.map(vo,DssDatamodelDimension.class);
        updateOne.setUpdateTime(new Date());
        return getBaseMapper().update(updateOne, Wrappers.<DssDatamodelDimension>lambdaUpdate().eq(DssDatamodelDimension::getId,id));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDimension(Long id) {
        //todo 校验引用情况
        return getBaseMapper().deleteById(id);
    }


    @Override
    public Message listDimensions(DimensionQueryVO vo) {
        QueryWrapper<DssDatamodelDimension> queryWrapper = new QueryWrapper<DssDatamodelDimension>()
                .like(StringUtils.isNotBlank(vo.getName()),"name",vo.getName())
                .eq(vo.getIsAvailable()!=null,"is_available",vo.getIsAvailable())
                .like(StringUtils.isNotBlank(vo.getOwner()),"owner",vo.getOwner());
        PageHelper.clearPage();
        PageHelper.startPage(vo.getPageNum(),vo.getPageSize());
        PageInfo<DssDatamodelDimension> pageInfo = new PageInfo<>(getBaseMapper().selectList(queryWrapper));
        //IPage<DssDatamodelDimension> iPage = page(new Page<>(vo.getPageNum(),vo.getPageSize()),queryWrapper);

        return Message.ok()
                .data("list",pageInfo
                        .getList()
                        .stream()
                        .map(dssDatamodelDimension -> modelMapper.map(dssDatamodelDimension, DimensionQueryDTO.class))
                        .collect(Collectors.toList()))
                .data("total",pageInfo.getTotal());
    }


    @Override
    public DimensionQueryDTO queryById(Long id) throws ErrorException {
        DssDatamodelDimension dssDatamodelDimension = getBaseMapper().selectById(id);
        if (dssDatamodelDimension == null){
            throw new DSSDatamodelCenterException(ErrorCode.DIMENSION_QUERY_ERROR.getCode(), "dimension id " +id +" not exists");
        }
        return modelMapper.map(dssDatamodelDimension,DimensionQueryDTO.class);
    }
}
