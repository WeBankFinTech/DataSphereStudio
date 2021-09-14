package com.webank.wedatasphere.dss.datamodel.dimension.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.webank.wedatasphere.dss.datamodel.dimension.dao.DssDatamodelDimensionMapper;
import com.webank.wedatasphere.dss.datamodel.dimension.entity.DssDatamodelDimension;
import com.webank.wedatasphere.dss.datamodel.dimension.service.DimensionService;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionAddVO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * @author helong
 * @date 2021/9/14
 */
@Service
public class DimensionServiceImpl  implements DimensionService {


    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private DssDatamodelDimensionMapper dssDatamodelDimensionMapper;

    @Override
    @Transactional
    public int addDimension(DimensionAddVO vo) {
        DssDatamodelDimension newOne = modelMapper.map(vo,DssDatamodelDimension.class);
        return dssDatamodelDimensionMapper.insert(newOne);
    }
}
