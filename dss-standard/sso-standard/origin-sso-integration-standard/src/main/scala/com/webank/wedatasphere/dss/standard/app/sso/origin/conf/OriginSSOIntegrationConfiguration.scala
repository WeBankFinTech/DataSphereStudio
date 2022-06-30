package com.webank.wedatasphere.dss.standard.app.sso.origin.conf

import org.apache.linkis.common.conf.CommonVars


object OriginSSOIntegrationConfiguration {
  val SSO_MAX_HTTP_CONNECT_TIMEOUT = CommonVars("wds.dss.sso.httpclient.max.connectTimeout", 3000000)
  val SSO_MAX_HTTP_READ_TIMEOUT = CommonVars("wds.dss.sso.httpclient.max.readTimeout", 3000000)
}
