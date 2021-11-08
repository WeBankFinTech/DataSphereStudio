package com.webank.wedatasphere.dss.data.governance.exception

import com.webank.wedatasphere.linkis.common.exception.ErrorException

class DataAssetsClientBuilderException (errorDesc: String) extends ErrorException(23000, errorDesc)
