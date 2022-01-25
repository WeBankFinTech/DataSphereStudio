package com.webank.wedatasphere.dss.data.api.server.restful;

import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiInfo;
import com.webank.wedatasphere.dss.data.api.server.exception.DataApiException;
import com.webank.wedatasphere.dss.data.api.server.service.ApiManagerService;
import org.apache.linkis.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lenovo
 * @Classname DSSDbApiManagerRestful
 * @Description 服务管理--API管理
 * @Date 2021/7/19 15:38
 * @Created by suyc
 */

@RestController
@RequestMapping(path = "/dss/data/api/apimanager", produces = {"application/json"})
public class DSSDbApiManagerRestful {
    @Autowired
    private ApiManagerService apiManagerService;


    @RequestMapping(path = "list", method = RequestMethod.GET)
    public Message getApiList(HttpServletRequest request,
                              @RequestParam("workspaceId") Long workspaceId, @RequestParam("apiName") String apiName,
                              @RequestParam("pageNow") Integer pageNow, @RequestParam("pageSize") Integer pageSize){
        if(pageNow == null){
            pageNow = 1;
        }
        if(pageSize == null){
            pageSize = 20;
        }

        List<Long> totals = new ArrayList<>();
        List<ApiInfo> apiInfoList = apiManagerService.getApiInfoList(workspaceId,apiName,totals,pageNow,pageSize);
        return Message.ok().data("list",apiInfoList).data("total", totals.get(0));
    }


    @RequestMapping(path = "/offline/{apiId}", method = RequestMethod.POST)
    public Message offlineApi(@PathVariable("apiId") Long apiId){
        apiManagerService.offlineApi(apiId);
        ApiInfo apiInfo = apiManagerService.getApiInfo(apiId);

        Message message = Message.ok("下线API成功").data("apiInfo",apiInfo);
        return message;
    }


    @RequestMapping(path = "/online/{apiId}", method = RequestMethod.POST)
    public Message onlineApi(@PathVariable("apiId") Long apiId) throws DataApiException {


        ApiInfo apiInfo = apiManagerService.getApiInfo(apiId);
        if(apiInfo.getIsTest() == 0){
            throw new DataApiException("请测试通过后再上线");
        }

        if(apiInfo.getStatus() == 1){
            throw new DataApiException("该Api已发布,请勿重复发布");
        }

        apiManagerService.onlineApi(apiId);
        apiInfo = apiManagerService.getApiInfo(apiId);
        Message message = Message.ok("上线API成功").data("apiInfo",apiInfo);
        return message;
    }


    @RequestMapping(path = "/callPath/{apiId}", method = RequestMethod.GET)
    public Message getApiCallPath(@PathVariable("apiId") Long apiId){
        StringBuilder callPath =new StringBuilder("{protocol}://{host}");
        callPath.append("/api/rest_j/v1/dss/data/api/execute");
        ApiInfo apiInfo = apiManagerService.getApiInfo(apiId);
        callPath.append("/" + apiInfo.getApiPath());

        Message message = Message.ok().data("callPathPrefix", callPath.toString());
        return message;
    }


}
