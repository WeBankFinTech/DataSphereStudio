package com.webank.wedatasphere.dss.framework.admin.service.impl;

import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssApplicationInfo;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssCreateApplicationData;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssOnestopMenuInfo;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssOnestopMenuJoinApplication;
import com.webank.wedatasphere.dss.framework.admin.service.DssAdminComponentService;
import com.webank.wedatasphere.dss.framework.admin.xml.DssComponentMapper;
import org.springframework.stereotype.Service;
import java.util.List;


import javax.annotation.Resource;

/**
 * @ClassName: DssAdminComponentServiceImpl
 * @Description: dss component service impl层
 * @author: lijw
 * @date: 2022/1/6 10:29
 */
@Service
public class  DssAdminComponentServiceImpl implements DssAdminComponentService {
    @Resource
    DssComponentMapper dssComponentMapper;
    @Override
    public List<DssOnestopMenuInfo> queryMenu() {
        return dssComponentMapper.queryMenu();
    }

    @Override
    public List<DssOnestopMenuJoinApplication> queryAll() {
        return dssComponentMapper.queryAll();
    }

    @Override
    public DssOnestopMenuJoinApplication query(int id) {
        return dssComponentMapper.query(id);
    }

    @Override
    public void insertApplication(DssApplicationInfo dssApplicationInfo) {
        dssComponentMapper.insertApplication(dssApplicationInfo);
    }

    @Override
    public void insertMenuApplication(DssCreateApplicationData dssCreateApplicationData, int id) {
        dssComponentMapper.insertMenuApplication(dssCreateApplicationData,id);
    }

    @Override
    public void updateApplication(DssApplicationInfo dssApplicationInfo) {
        dssComponentMapper.updateApplication(dssApplicationInfo);
    }

    @Override
    public void updateMenuApplication(DssCreateApplicationData dssCreateApplicationData) {
        dssComponentMapper.updateMenuApplication(dssCreateApplicationData);
    }
}
