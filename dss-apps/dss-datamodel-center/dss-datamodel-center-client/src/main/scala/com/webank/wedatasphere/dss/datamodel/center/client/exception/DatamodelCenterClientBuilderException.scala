package com.webank.wedatasphere.dss.datamodel.center.client.exception

import com.webank.wedatasphere.linkis.common.exception.ErrorException

class DatamodelCenterClientBuilderException(errorDesc: String) extends ErrorException(212400, errorDesc)
