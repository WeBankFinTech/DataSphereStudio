package com.webank.wedatasphere.warehouse.client

import com.webank.wedatasphere.linkis.httpclient.dws.request.DWSHttpAction
import com.webank.wedatasphere.linkis.httpclient.request.UserAction

trait DwAction extends DWSHttpAction with UserAction {
}
