package com.webank.wedatasphere.dss.guide.server.restful;

import com.webank.wedatasphere.dss.guide.server.entity.GuideChapter;
import com.webank.wedatasphere.dss.guide.server.service.GuideCatalogService;
import com.webank.wedatasphere.dss.guide.server.service.GuideChapterService;
import com.webank.wedatasphere.dss.guide.server.service.GuideGroupService;
import lombok.AllArgsConstructor;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/dss/guide/query", produces = {"application/json"})
@AllArgsConstructor
public class GuideQueryRestful {
    private static final Logger logger = LoggerFactory.getLogger(GuideQueryRestful.class);

    private GuideGroupService guideGroupService;
    private GuideCatalogService guideCatalogService;
    private GuideChapterService guideChapterService;

    @RequestMapping(path = "/groupdetail", method = RequestMethod.GET)
    public Message queryGudieContent(@RequestParam String path) {
        return Message.ok().data("result", guideGroupService.queryGuideGroupByPath(path));
    }

    @RequestMapping(path = "/guidecatalog/top", method = RequestMethod.GET)
    public Message queryGuideCatalogListForTop() {
        return Message.ok().data("result", guideCatalogService.queryGuideCatalogListForTop());
    }

    @RequestMapping(path = "/guidecatalog/{id}/detail", method = RequestMethod.GET)
    public Message queryGuideCatalogDetailById(@PathVariable Long id) {
        return Message.ok().data("result", guideCatalogService.queryGuideCatalogDetailById(id));
    }

    @RequestMapping(path = "/guidechapter", method = RequestMethod.GET)
    public Message queryGuideChapter(@RequestParam("keyword") String keyword,
                                     @RequestParam("pageNow") Integer pageNow, @RequestParam("pageSize") Integer pageSize) {
        if (pageNow == null) {
            pageNow = 1;
        }
        if (pageSize == null) {
            pageSize = 20;
        }

        List<Long> totals = new ArrayList<>();
        List<GuideChapter> guideChapterList = guideChapterService.searchGuideChapterList(keyword, totals, pageNow, pageSize);
        return Message.ok().data("result", guideChapterList).data("total", totals.get(0));
    }

    @RequestMapping(path = "/guidechapter/{id}", method = RequestMethod.GET)
    public Message queryGuideChapter(@PathVariable Long id) {
        return Message.ok().data("result", guideChapterService.queryGuideChapterById(id));
    }

}
