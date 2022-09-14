package com.webank.wedatasphere.warehouse.client

import org.apache.linkis.httpclient.dws.request.DWSHttpAction
import org.apache.linkis.httpclient.request.UserAction

trait DwAction extends DWSHttpAction with UserAction {
}
