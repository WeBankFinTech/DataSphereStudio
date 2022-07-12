package com.webank.wedatasphere.dss.standard.app.development.ref;

import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;

/**
 * @author enjoyyin
 * @date 2022-04-14
 * @since 1.1.0
 */
public interface QueryJumpUrlRequestRef<R extends RefJobContentRequestRef<R>> extends RefJobContentRequestRef<R> {

    default R setSSOUrlBuilderOperation(SSOUrlBuilderOperation ssoUrlBuilderOperation) {
        setParameter("ssoUrlBuilderOperation", ssoUrlBuilderOperation);
        return (R) this;
    }

    /**
     * SSOUrlBuilderOperation 用于封装出第三方系统 refJob 对应的前端页面的 URL。
     * 该 SSOUrlBuilderOperation 已填充了 setWorkspace setDSSUrl setAppName addCookie 方法，用户需按实际需要，填充
     * setReqUrl、redirectTo，以及 addQueryParameter。
     * <br/><br/>
     * 具体解释如下：
     * setReqUrl: 设置第三方应用的请求 URL，该 URL 必须要能进入到第三方系统后台的 Filter，详见DSS第一级规范
     * redirectTo：第三方系统的前端页面 URL。
     * addQueryParameter：额外的参数，DSS 支持自动替换的参数详见 {@code QueryJumpUrlConstant}
     * @return SSOUrlBuilderOperation
     */
    default SSOUrlBuilderOperation getSSOUrlBuilderOperation() {
        return (SSOUrlBuilderOperation) getParameter("ssoUrlBuilderOperation");
    }

}
