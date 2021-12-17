package com.webank.wedatasphere.dss.guide.restful;

import lombok.AllArgsConstructor;
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
import java.util.Map;

/**
 * @author suyc
 * @Classname MangerRestful
 * @Description TODO
 * @Date 2021/12/17 14:53
 * @Created by suyc
 */
@RestController
@RequestMapping(path = "/dss/guide/manager", produces = {"application/json"})
@AllArgsConstructor
public class MangerRestful {
    private static final Logger logger = LoggerFactory.getLogger(MangerRestful.class);


    @RequestMapping(method = RequestMethod.GET, path ="/hello")
    public Message hello() throws Exception {
        return Message.ok().data("result", "ok");
    }

}
