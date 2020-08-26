package com.webank.wedatasphere.dss.server.restful;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.server.entity.CtyunUser;
import com.webank.wedatasphere.dss.server.service.CtyunUserService;
import com.webank.wedatasphere.linkis.server.Message;

@RestController
@RequestMapping("/dss/ct-yun")
public class CtyunRestfulApi {

    @Autowired
    private CtyunUserService ctyunUserService;

    @PostMapping("/luban/openAccount")
    public Message openAccount(@RequestBody JsonNode jsonNode) throws DSSErrorException {
        // 处理请求
        String id = jsonNode.get("userId").asText();
        String name = jsonNode.get("name").asText();
        JsonNode workOrderItemConfig = jsonNode.get("workOrderItemConfig");

        String ctyunUsername = workOrderItemConfig.get("userName").asText();
        CtyunUser ctyunUser = ctyunUserService.getByCtyunUsername(ctyunUsername);
        if (ctyunUser != null) {
            // TODO: 2020/8/4 如果用户已经存在，续约/扩容
            return new Message().data("username", ctyunUser.getUsername());
        }

        ctyunUser = new CtyunUser();
        ctyunUser.setId(id);
        ctyunUser.setName(name);
        ctyunUser.setWorkOrderItemConfig(workOrderItemConfig.toString());
        ctyunUser.setCtyunUsername(ctyunUsername);
        ctyunUserService.createUser(ctyunUser);
        return new Message().data("username", ctyunUser.getUsername());
    }

}
