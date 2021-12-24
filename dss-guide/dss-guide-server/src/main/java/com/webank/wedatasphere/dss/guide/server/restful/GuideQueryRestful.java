package com.webank.wedatasphere.dss.guide.server.restful;

import com.webank.wedatasphere.dss.guide.server.entity.GuideGroup;
import com.webank.wedatasphere.dss.guide.server.service.GuideContentService;
import com.webank.wedatasphere.dss.guide.server.service.GuideGroupService;
import lombok.AllArgsConstructor;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author suyc
 * @Classname GuideQueryRestful
 * @Description TODO
 * @Date 2021/12/24 10:04
 * @Created by suyc
 */
@RestController
@RequestMapping(path = "/dss/guide/query", produces = {"application/json"})
@AllArgsConstructor
public class GuideQueryRestful {
    private static final Logger logger = LoggerFactory.getLogger(GuideQueryRestful.class);

    private GuideGroupService guideGroupService;

    @RequestMapping(path ="/groupdetail", method = RequestMethod.GET)
    public Message queryGudieContent(@RequestParam String path){
        return Message.ok().data("result",guideGroupService.queryGuideGroupByPath(path));
    }
}
