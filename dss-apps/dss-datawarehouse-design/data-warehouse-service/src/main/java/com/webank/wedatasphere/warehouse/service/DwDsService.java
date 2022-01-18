package com.webank.wedatasphere.warehouse.service;

import org.apache.linkis.server.Message;

import javax.servlet.http.HttpServletRequest;

public interface DwDsService {
    Message getAllHiveDbs(HttpServletRequest request) throws Exception;

    Message getPrincipalUsers(HttpServletRequest request, String id) throws Exception;

    Message getPrincipalRoles(HttpServletRequest request, String id) throws Exception;
}
