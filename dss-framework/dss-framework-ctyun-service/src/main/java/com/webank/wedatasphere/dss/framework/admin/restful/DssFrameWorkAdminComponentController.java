package com.webank.wedatasphere.dss.framework.admin.restful;

import com.webank.wedatasphere.dss.framework.admin.common.domain.Message;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.*;
import com.webank.wedatasphere.dss.framework.admin.service.DssAdminComponentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import javax.annotation.Resource;

/**
 * @ClassName: DssFrameWorkAdminComponentController
 * @Description: dss组件api接口
 * @author: lijw
 * @date: 2022/1/6 10:14
 */
@RequestMapping(path = "/dss/framework/admin/component", produces = {"application/json"})
@RestController
public class DssFrameWorkAdminComponentController {
    @Resource
    DssAdminComponentService dssAdminComponentService;

    @RequestMapping(path ="query", method = RequestMethod.GET)
    public Message queryMenu(){
        List<DssOnestopMenuInfo> dssOnestopMenuList = dssAdminComponentService.queryMenu();
        return  Message.ok().data("dssOnestopMenuList",dssOnestopMenuList).message("成功");
    }

    @RequestMapping(path ="queryAll", method = RequestMethod.GET)
    public Message queryAll(){
        List<DssOnestopMenuJoinApplication> dssOnestopMenuJoinApplicationList = dssAdminComponentService.queryAll();
        return  Message.ok().data("dssOnestopMenuJoinApplicationList",dssOnestopMenuJoinApplicationList).message("成功");
    }

    @RequestMapping(path ="query/{id}", method = RequestMethod.GET)
    public Message query(@PathVariable("id") int id){
        DssOnestopMenuJoinApplication query = dssAdminComponentService.query(id);
        return Message.ok().data("queryResult",query).message("成功");
    }

    @RequestMapping(path = "createApplication", method = RequestMethod.POST)
    public Message createApplication(@Validated @RequestBody DssCreateApplicationData application){
        DssApplicationInfo dssApplicationInfo =new DssApplicationInfo();
        dssApplicationInfo.setName(application.getTitleEn());
        dssApplicationInfo.setUrl(application.getUrl());
        dssApplicationInfo.setProjectUrl(application.getProjectUrl());
        dssApplicationInfo.setIfIframe(application.getIfIframe());
        dssApplicationInfo.setHomepageUrl(application.getHomepageUrl());
        dssApplicationInfo.setRedirectUrl(application.getRedirectUrl());
        if (application.getId()==null){
            dssAdminComponentService.insertApplication(dssApplicationInfo);
            dssAdminComponentService.insertMenuApplication(application, dssApplicationInfo.getId());
        }
        else {
            dssApplicationInfo.setId(Integer.parseInt(application.getId()));
            dssAdminComponentService.updateApplication(dssApplicationInfo);
            dssAdminComponentService.updateMenuApplication(application);
        }
        return  Message.ok().message("成功");
    }
}
