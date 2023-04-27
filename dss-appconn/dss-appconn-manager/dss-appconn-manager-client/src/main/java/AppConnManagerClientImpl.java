import com.webank.wedatasphere.dss.appconn.manager.impl.AppConnManagerImpl;
import com.webank.wedatasphere.dss.appconn.manager.service.AppConnInfoService;
import com.webank.wedatasphere.dss.appconn.manager.service.AppConnInfoServiceImpl;
import com.webank.wedatasphere.dss.appconn.manager.service.AppConnResourceService;
import com.webank.wedatasphere.dss.appconn.manager.service.AppConnResourceServiceImpl;

public class AppConnManagerClientImpl extends AppConnManagerImpl {
    @Override
    protected AppConnInfoService createAppConnInfoService() {
        return new AppConnInfoServiceImpl();
    }

    @Override
    protected AppConnResourceService createAppConnResourceService() {
        return new AppConnResourceServiceImpl();
    }
}
