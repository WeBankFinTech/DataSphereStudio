package com.webank.wedatasphere.dss.guide.server.restful;

import com.webank.wedatasphere.dss.guide.server.conf.GuideConf;
import com.webank.wedatasphere.dss.guide.server.entity.GuideCatalog;
import com.webank.wedatasphere.dss.guide.server.entity.GuideChapter;
import com.webank.wedatasphere.dss.guide.server.service.GuideCatalogService;
import com.webank.wedatasphere.dss.guide.server.service.GuideChapterService;
import com.webank.wedatasphere.dss.guide.server.service.GuideGroupService;
import com.webank.wedatasphere.dss.guide.server.util.FileUtils;
import lombok.AllArgsConstructor;
import org.apache.linkis.common.utils.Utils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(path = "/dss/guide/admin", produces = {"application/json"})
@AllArgsConstructor
public class KnowledgeGuideAdminRestful {
    private static final Logger logger = LoggerFactory.getLogger(KnowledgeGuideAdminRestful.class);

    private GuideCatalogService guideCatalogService;
    private GuideChapterService guideChapterService;

    private GuideGroupService guideGroupService;

    private final static String SUMMARY = "SUMMARY.md";

    private final static String SHELL_COMMAND_HOST_IP = "hostname -i";

    private final static String MODEL_GITBOOK_SYNC = "gitbook";


    /**
     * 知识库目录接口
     */
    @RequestMapping(path = "/guidecatalog", method = RequestMethod.POST)
    public Message saveGuideCatalog(HttpServletRequest request, @RequestBody GuideCatalog guideCatalog) {
        String userName = SecurityFilter.getLoginUsername(request);
        if (null == guideCatalog.getId()) {
            guideCatalog.setCreateBy(userName);
            guideCatalog.setCreateTime(new Date(System.currentTimeMillis()));
        } else {
            guideCatalog.setUpdateBy(userName);
            guideCatalog.setUpdateTime(new Date(System.currentTimeMillis()));
        }

        boolean flag = guideCatalogService.saveGuideCatalog(guideCatalog);
        if (flag) {
            return Message.ok("保存成功");
        } else {
            return Message.error("保存失败");
        }
    }

    @RequestMapping(path = "/guidecatalog/{id}/delete", method = RequestMethod.POST)
    public Message deleteGroup(@PathVariable Long id) {
        guideCatalogService.deleteGuideCatalog(id);
        Message message = Message.ok("删除成功");
        return message;
    }

    @RequestMapping(path = "/guidecatalog/top", method = RequestMethod.GET)
    public Message queryGuideCatalogListForTop() {
        return Message.ok().data("result", guideCatalogService.queryGuideCatalogListForTop());
    }

    @RequestMapping(path = "/guidecatalog/{id}/detail", method = RequestMethod.GET)
    public Message queryGuideCatalogDetailById(@PathVariable Long id) {
        return Message.ok().data("result", guideCatalogService.queryGuideCatalogDetailById(id));
    }


    /**
     * 知识库文档接口
     */
    @RequestMapping(path = "/guidechapter", method = RequestMethod.POST)
    public Message saveGuideChapter(HttpServletRequest request, @RequestBody GuideChapter guideChapter) {
        String userName = SecurityFilter.getLoginUsername(request);
        if (null == guideChapter.getId()) {
            guideChapter.setCreateBy(userName);
            guideChapter.setCreateTime(new Date(System.currentTimeMillis()));
        } else {
            guideChapter.setUpdateBy(userName);
            guideChapter.setUpdateTime(new Date(System.currentTimeMillis()));
        }

        boolean flag = guideChapterService.saveGuideChapter(guideChapter);
        if (flag) {
            return Message.ok("保存成功");
        } else {
            return Message.error("保存失败");
        }
    }

    @RequestMapping(path = "/guidechapter/{id}/delete", method = RequestMethod.POST)
    public Message deleteGuideChapter(@PathVariable Long id) {
        guideChapterService.deleteGuideChapter(id);
        Message message = Message.ok("删除成功");
        return message;
    }

    @RequestMapping(path = "/guidechapter/{id}", method = RequestMethod.GET)
    public Message queryGuideChapter(@PathVariable Long id) {
        return Message.ok().data("result", guideChapterService.queryGuideChapterById(id));
    }

    @RequestMapping(path = "/guidechapter/uploadImages", method = RequestMethod.POST)
    public Message multFileUpload(@RequestParam(required = true) List<MultipartFile> files) {
        if (null == files || files.size() == 0) {
            return Message.error("没有上传文件");
        }

        List<Map<String, Object>> totalResult = new ArrayList<Map<String, Object>>();
        // 要上传的目标文件存放的绝对路径
        final String localPath = GuideConf.GUIDE_CHAPTER_IMAGES_PATH.getValue();
        for (MultipartFile file : files) {
            Map<String, Object> result = new HashMap<String, Object>();
            String result_msg = "";

            if (file.getSize() > 5 * 1024 * 1024) {
                result_msg = "图片大小不能超过5M";
            } else {
                //判断上传文件格式
                String fileType = file.getContentType();
                if (fileType.equals("image/jpeg") || fileType.equals("image/png") || fileType.equals("image/jpg")) {
                    //获取文件名
                    String fileName = file.getOriginalFilename();
                    //获取文件后缀名
                    String suffixName = fileName.substring(fileName.lastIndexOf("."));
                    //重新生成文件名
                    fileName = "knowledge-" + UUID.randomUUID() + suffixName;
                    if (FileUtils.upload(file, localPath, fileName)) {
                        String relativePath = fileName;
                        result.put("relativePath", relativePath);
                        result_msg = "图片上传成功";
                    } else {
                        result_msg = "图片上传失败";
                    }
                } else {
                    result_msg = "图片格式不正确";
                }
            }
            result.put("result_msg", result_msg);
            totalResult.add(result);
        }
        return Message.ok().data("result", totalResult);
    }

    @RequestMapping(path = "/guidechapter/uploadImage", method = RequestMethod.POST)
    public Message fileUpload(@RequestParam(required = true) MultipartFile file) {
        if (null == file) {
            return Message.error("没有上传文件");
        }

        final String imagesPath = GuideConf.GUIDE_CHAPTER_IMAGES_PATH.getValue();

        if (file.getSize() > 5 * 1024 * 1024) {
            return Message.error("图片大小不能超过5M");
        } else {
            //判断上传文件格式
            String fileType = file.getContentType();
            if (fileType.equals("image/jpeg") || fileType.equals("image/png") || fileType.equals("image/jpg")) {
                //获取文件名
                String fileName = file.getOriginalFilename();
                //获取文件后缀名
                String suffixName = fileName.substring(fileName.lastIndexOf("."));
                //重新生成文件名
                fileName = UUID.randomUUID() + suffixName;
                if (FileUtils.upload(file, imagesPath, fileName)) {
                    return Message.ok().data("result", fileName);
                } else {
                    return Message.error("图片上传失败");
                }
            } else {
                return Message.error("图片格式不正确");
            }
        }

    }

    @PostConstruct
    public void syncKnowledge() {
        final String summaryPath = GuideConf.HOST_GITBOOK_PATH.getValue() + File.separator + SUMMARY;
        logger.info("开始执行定时任务...");
        Utils.defaultScheduler().scheduleAtFixedRate(() -> {
            try {
                guideCatalogService.syncKnowledge(summaryPath, GuideConf.SUMMARY_IGNORE_MODEL.getValue());
                guideGroupService.asyncGuide(summaryPath, GuideConf.SUMMARY_IGNORE_MODEL.getValue());
            } catch (Exception e) {
                logger.error("定时任务执行异常：" + e);
                throw new RuntimeException(e);
            }
            logger.info("定时任务执行结束！！！");
        }, 0, 2, TimeUnit.HOURS);
    }

}
