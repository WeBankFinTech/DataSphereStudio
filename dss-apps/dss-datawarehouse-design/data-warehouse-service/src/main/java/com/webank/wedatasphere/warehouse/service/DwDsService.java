package com.webank.wedatasphere.warehouse.service;

import com.webank.wedatasphere.linkis.server.Message;

import javax.servlet.http.HttpServletRequest;

public interface DwDsService {
    Message getAllHiveDbs(HttpServletRequest request) throws Exception;
}
