package com.webank.wedatasphere.linkis.oneservice.core.service;

import com.webank.wedatasphere.linkis.oneservice.core.vo.ApiVersionVo;
import com.webank.wedatasphere.linkis.oneservice.core.vo.TestParamVo;

import java.util.List;
import java.util.Map;

/**
 * 接口调用service
 *
 * @author lidongzhang
 */
public interface OneServiceQueryService {
    /**
     * 执行查询
     *
     * @param path       path
     * @param params     params
     * @param moduleName moduleName
     * @param httpMethod httpMethod
     * @return 查询接口
     */
    List<Map<String, Object>> query(String path, Map<String, Object> params, String moduleName, String httpMethod);

    List<TestParamVo> query(Long id, String version);

    /**
     * 查询api 版本信息
     *
     * @param id
     * @return
     */
    List<ApiVersionVo> queryApiVersion(Long id);
}
