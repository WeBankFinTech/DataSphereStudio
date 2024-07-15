package com.webank.wedatasphere.dss.framework.admin.restful;

import com.webank.wedatasphere.dss.common.utils.GlobalLimitsUtils;
import org.apache.linkis.server.Message;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author enjoyyin
 * @date 2022-03-29
 * @since 0.5.0
 */
@RequestMapping(path = "/dss/framework/admin", produces = {"application/json"})
@RestController
public class DSSFeatureAuthRestfulApi {

    @RequestMapping(value = "/globalLimits",method = RequestMethod.GET)
    public Message globalLimits() {
        return Message.ok().data("globalLimits", GlobalLimitsUtils.getAllGlobalLimits());
    }

    @RequestMapping(value = "/globalLimits/{globalLimitName}",method = RequestMethod.GET)
    public Message globalLimit(@PathVariable("globalLimitName") String globalLimitName) {
        return Message.ok().data("globalLimitName", globalLimitName)
                .data("content", GlobalLimitsUtils.getGlobalLimitMap(globalLimitName));
    }

}
