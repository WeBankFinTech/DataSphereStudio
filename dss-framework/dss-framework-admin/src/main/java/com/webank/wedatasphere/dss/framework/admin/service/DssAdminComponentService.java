package com.webank.wedatasphere.dss.framework.admin.service;

import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssApplicationInfo;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssCreateApplicationData;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssOnestopMenuInfo;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssOnestopMenuJoinApplication;
import java.util.List;

/**
 * @ClassName: DssAdminComponentService
 * @Description: dss component service 层
 * @author: lijw
 * @date: 2022/1/6 10:28
 */
public interface DssAdminComponentService {
    List<DssOnestopMenuInfo> queryMenu();
    List<DssOnestopMenuJoinApplication> queryAll();
    DssOnestopMenuJoinApplication query( int id);
    void insertApplication(DssApplicationInfo dssApplicationInfo);
    void insertMenuApplication( DssCreateApplicationData dssCreateApplicationData,  int id);
    void updateApplication(DssApplicationInfo dssApplicationInfo);
    void updateMenuApplication(DssCreateApplicationData dssCreateApplicationData);
}
