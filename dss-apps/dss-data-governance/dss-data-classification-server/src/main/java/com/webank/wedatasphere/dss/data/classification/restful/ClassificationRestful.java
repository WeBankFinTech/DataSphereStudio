package com.webank.wedatasphere.dss.data.classification.restful;

import com.webank.wedatasphere.dss.data.classification.service.ClassificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.atlas.model.typedef.AtlasClassificationDef;
import org.apache.atlas.model.typedef.AtlasTypesDef;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/dss/data/governance/classification", produces = {"application/json"})
@Slf4j
@AllArgsConstructor
public class ClassificationRestful {
    private static final Logger logger = LoggerFactory.getLogger(ClassificationRestful.class);

    private ClassificationService classificationService;

    /**
     * 获取所有分类
     */
    @RequestMapping(method = RequestMethod.GET, path ="/all")
    public Message getClassificationDef() throws Exception {
        return Message.ok().data("result", classificationService.getClassificationDef());
    }

    /**
     * 根据名称获取分类,不包括包括子分类
     */
    @RequestMapping(method = RequestMethod.GET, path ="/{name}")
    public Message getClassificationDef(@PathVariable String name) throws Exception {
        return Message.ok().data("result",classificationService.getClassificationDefByName(name));
    }

    /**
     * 根据名称获取分类,包括一级子分类
     * keyword 按照分类名称模糊搜索
     */
    @RequestMapping(method = RequestMethod.GET, path ="/{name}/subtypes")
    public Message getClassificationDefList(@PathVariable String name,
                                            @RequestParam(defaultValue = "")  String keyword) throws Exception {
        List<AtlasClassificationDef> atlasClassificationDefList = classificationService.getClassificationDefListByName(name);
        if(atlasClassificationDefList ==null || keyword ==null || keyword.trim().equalsIgnoreCase("")) {
            return Message.ok().data("result", atlasClassificationDefList);
        }
        else{
            Pattern regex = Pattern.compile(keyword);
            return Message.ok().data("result",atlasClassificationDefList.stream().filter(ele -> regex.matcher(ele.getName()).find()).collect(Collectors.toList()));
        }
    }

    /**
     * 获取所有分层的一级子类型，包括系统预置分层和用户自定义分层
     */
    @RequestMapping(method = RequestMethod.GET, path ="/layers/all")
    public Message getClassificationDefListForLayer() throws Exception {
        return Message.ok().data("result",classificationService.getClassificationDefListForLayer());
    }

    /**
     * 创建新的分类
     */
    @RequestMapping(method = RequestMethod.POST, path ="/batch")
    public Message createClassificationDef(@RequestBody AtlasTypesDef typesDef) throws Exception {
        return Message.ok().data("result", classificationService.createAtlasTypeDefs(typesDef));
    }

    /**
     * 更新的分类
     */
    @RequestMapping(method = RequestMethod.PUT, path ="/batch")
    public Message updateClassificationDef(@RequestBody AtlasTypesDef typesDef) throws Exception {
        classificationService.updateAtlasTypeDefs(typesDef);
        return Message.ok().data("result","更新成功" );
    }

    /**
     * 删除分类
     * @DELETE  linkis-gateway 不支持DELETE方式
     */
    @RequestMapping(method = RequestMethod.POST, path ="/{name}/delete")
    public Message deleteClassificationDef(@PathVariable String name) throws Exception {
        classificationService.deleteClassificationDefByName(name);
        return Message.ok().data("result","删除成功");
    }
}
