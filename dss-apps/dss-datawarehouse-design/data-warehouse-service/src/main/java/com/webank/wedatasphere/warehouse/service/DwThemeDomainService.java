package com.webank.wedatasphere.warehouse.service;

import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.warehouse.cqe.*;
import com.webank.wedatasphere.warehouse.exception.DwException;

import javax.servlet.http.HttpServletRequest;

public interface DwThemeDomainService {
    Message queryAllThemeDomains(HttpServletRequest request, DwThemeDomainQueryCommand command) throws DwException;

    Message queryPage(HttpServletRequest request, DwThemeDomainQueryCommand command) throws DwException;

    Message create(HttpServletRequest request, DwThemeDomainCreateCommand command) throws DwException;

    Message getById(HttpServletRequest request, Long id) throws DwException;

    Message deleteById(HttpServletRequest request, Long id) throws DwException;

    Message update(HttpServletRequest request, DwThemeDomainUpdateCommand command) throws DwException;

    Message enable(HttpServletRequest request, Long id) throws DwException;

    Message disable(HttpServletRequest request, Long id) throws DwException;
}
