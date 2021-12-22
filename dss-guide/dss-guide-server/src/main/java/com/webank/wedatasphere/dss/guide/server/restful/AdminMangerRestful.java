package com.webank.wedatasphere.dss.guide.server.restful;

import com.webank.wedatasphere.dss.guide.server.entity.GuideContent;
import com.webank.wedatasphere.dss.guide.server.entity.GuideGroup;
import com.webank.wedatasphere.dss.guide.server.service.GuideContentService;
import com.webank.wedatasphere.dss.guide.server.service.GuideGroupService;
import lombok.AllArgsConstructor;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author suyc
 * @Classname MangerRestful
 * @Description TODO
 * @Date 2021/12/17 14:53
 * @Created by suyc
 */
@RestController
@RequestMapping(path = "/dss/guide/admin", produces = {"application/json"})
@AllArgsConstructor
public class AdminMangerRestful {
    private static final Logger logger = LoggerFactory.getLogger(AdminMangerRestful.class);

    private GuideGroupService guideGroupService;
    private GuideContentService guideContentService;

    @RequestMapping(path ="/guidegroup", method = RequestMethod.POST)
    public Message saveGuideGroup(HttpServletRequest request, @RequestBody GuideGroup guideGroup){
        String userName = SecurityFilter.getLoginUsername(request);
        if(guideGroup.getId() ==null) {
            guideGroup.setCreateBy(userName);
            guideGroup.setCreateTime(new Date(System.currentTimeMillis()));
            guideGroup.setUpdateTime(new Date(System.currentTimeMillis()));
        }
        else{
            guideGroup.setUpdateBy(userName);
            guideGroup.setUpdateTime(new Date(System.currentTimeMillis()));
        }

        boolean flag = guideGroupService.saveGuideGroup(guideGroup);
        if(flag) {
            return Message.ok("保存成功");
        }else{
            return Message.error("保存失败");
        }
    }

    @RequestMapping(path ="/guidegroup/{path}", method = RequestMethod.GET)
    public Message queryGuideGroup(@PathVariable String path){
        return Message.ok().data("result", guideGroupService.queryGuideGroupByPath(path));
    }

    @RequestMapping(path ="/guidecontent", method = RequestMethod.POST)
    public Message saveGuideContent(HttpServletRequest request, @RequestBody GuideContent guideConent){
        String userName = SecurityFilter.getLoginUsername(request);
        if(guideConent.getId() ==null) {
            guideConent.setCreateBy(userName);
            guideConent.setCreateTime(new Date(System.currentTimeMillis()));
            guideConent.setUpdateTime(new Date(System.currentTimeMillis()));
        }
        else{
            guideConent.setUpdateBy(userName);
            guideConent.setUpdateTime(new Date(System.currentTimeMillis()));
        }

        boolean flag = guideContentService.saveGuideContent(guideConent);
        if(flag) {
            return Message.ok("保存成功");
        }else{
            return Message.error("保存失败");
        }
    }

    @RequestMapping(path ="/guidecontent/{path}", method = RequestMethod.GET)
    public Message queryGuideContentList(@PathVariable String path){
        return Message.ok().data("result", guideContentService.queryGuideContentByPath(path));
    }
}
