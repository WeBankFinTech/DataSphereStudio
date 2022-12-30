package com.webank.wedatasphere.dss.datamodel.center.client.exception

import org.apache.linkis.common.exception.ErrorException

class DatamodelCenterClientBuilderException(errorDesc: String) extends ErrorException(212400, errorDesc)
