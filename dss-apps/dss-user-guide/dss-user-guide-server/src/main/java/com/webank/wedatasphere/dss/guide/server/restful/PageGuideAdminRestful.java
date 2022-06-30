package com.webank.wedatasphere.dss.guide.server.restful;

import com.webank.wedatasphere.dss.guide.server.conf.GuideConf;
import com.webank.wedatasphere.dss.guide.server.entity.GuideContent;
import com.webank.wedatasphere.dss.guide.server.entity.GuideGroup;
import com.webank.wedatasphere.dss.guide.server.service.GuideContentService;
import com.webank.wedatasphere.dss.guide.server.service.GuideGroupService;
import com.webank.wedatasphere.dss.guide.server.util.FileUtils;
import lombok.AllArgsConstructor;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/dss/guide/admin", produces = {"application/json"})
@AllArgsConstructor
public class PageGuideAdminRestful {
    private static final Logger logger = LoggerFactory.getLogger(PageGuideAdminRestful.class);

    private GuideGroupService guideGroupService;
    private GuideContentService guideContentService;

    @RequestMapping(path = "/guidegroup", method = RequestMethod.POST)
    public Message saveGuideGroup(HttpServletRequest request, @RequestBody GuideGroup guideGroup) {
        String userName = SecurityFilter.getLoginUsername(request);
        if (null == guideGroup.getId()) {
            guideGroup.setCreateBy(userName);
            guideGroup.setCreateTime(new Date(System.currentTimeMillis()));
        } else {
            guideGroup.setUpdateBy(userName);
            guideGroup.setUpdateTime(new Date(System.currentTimeMillis()));
        }

        boolean flag = guideGroupService.saveGuideGroup(guideGroup);
        if (flag) {
            return Message.ok("保存成功");
        } else {
            return Message.error("保存失败");
        }
    }

    @RequestMapping(path = "/guidegroup", method = RequestMethod.GET)
    public Message queryGuideGroup() {
        return Message.ok().data("result", guideGroupService.getAllGuideGroupDetails());
    }

    @RequestMapping(path = "/guidegroup/{id}/delete", method = RequestMethod.POST)
    public Message deleteGuideGroup(@PathVariable Long id) {
        guideGroupService.deleteGuideGroup(id);
        Message message = Message.ok("删除成功");
        return message;
    }

    @RequestMapping(path = "/guidecontent", method = RequestMethod.POST)
    public Message saveGuideContent(HttpServletRequest request, @RequestBody GuideContent guideConent) {
        String userName = SecurityFilter.getLoginUsername(request);
        if (null == guideConent.getId()) {
            guideConent.setCreateBy(userName);
            guideConent.setCreateTime(new Date(System.currentTimeMillis()));
        } else {
            guideConent.setUpdateBy(userName);
            guideConent.setUpdateTime(new Date(System.currentTimeMillis()));
        }

        boolean flag = guideContentService.saveGuideContent(guideConent);
        if (flag) {
            return Message.ok("保存成功");
        } else {
            return Message.error("保存失败");
        }
    }

    @RequestMapping(path = "/guidecontent", method = RequestMethod.GET)
    public Message queryGuideContent(@RequestParam String path) {
        return Message.ok().data("result", guideContentService.queryGuideContentByPath(path));
    }

    @RequestMapping(path = "/guidecontent/{id}", method = RequestMethod.GET)
    public Message queryGuideContent(@PathVariable Long id) {
        return Message.ok().data("result", guideContentService.getGuideContent(id));
    }

    @RequestMapping(path = "/guidecontent/{id}/content", method = RequestMethod.POST)
    public Message updateGuideContent(@PathVariable Long id, @RequestBody Map<String, Object> map) {
        try {
            guideContentService.updateGuideContentById(id, map);
            return Message.ok("更新成功");
        } catch (Exception ex) {
            logger.error("ERROR", "Error found: ", ex);
            return Message.error(ex.getMessage());
        }
    }

    @RequestMapping(path = "/guidecontent/{id}/delete", method = RequestMethod.POST)
    public Message deleteContent(@PathVariable Long id) {
        guideContentService.deleteGuideContent(id);
        Message message = Message.ok("删除成功");
        return message;
    }

    @RequestMapping(path = "/guidecontent/uploadImages", method = RequestMethod.POST)
    public Message multFileUpload(@RequestParam(required = true) List<MultipartFile> files) {
        if (null == files || files.size() == 0) {
            return Message.error("没有上传文件");
        }

        List<Map<String, Object>> totalResult = new ArrayList<Map<String, Object>>();
        // 要上传的目标文件存放的绝对路径
        final String localPath = GuideConf.GUIDE_CONTENT_IMAGES_PATH.getValue();
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
                    fileName = "page-" + UUID.randomUUID() + suffixName;
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

    @RequestMapping(path = "/guidecontent/uploadImage", method = RequestMethod.POST)
    public Message fileUpload(@RequestParam(required = true) MultipartFile file) {
        if (null == file) {
            return Message.error("没有上传文件");
        }

        final String imagesPath = GuideConf.GUIDE_CONTENT_IMAGES_PATH.getValue();

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
}
