package com.webank.wedatasphere.dss.framework.workspace.client.exception

import org.apache.linkis.common.exception.ErrorException

class WorkSpaceClientBuilderException(errorDesc: String) extends ErrorException(60022, errorDesc)
