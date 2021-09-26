package com.webank.wedatasphere.warehouse.service;

import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.warehouse.cqe.DwModifierCreateCommand;
import com.webank.wedatasphere.warehouse.cqe.DwModifierQueryCommand;
import com.webank.wedatasphere.warehouse.cqe.DwModifierUpdateCommand;
import com.webank.wedatasphere.warehouse.exception.DwException;

import javax.servlet.http.HttpServletRequest;

public interface DwModifierService {
    Message queryAllModifiers(HttpServletRequest request, DwModifierQueryCommand command) throws DwException;

    Message queryPage(HttpServletRequest request, DwModifierQueryCommand command) throws DwException;

    Message create(HttpServletRequest request, DwModifierCreateCommand command) throws DwException;

    Message getById(HttpServletRequest request, Long id) throws DwException;

    Message deleteById(HttpServletRequest request, Long id) throws DwException;

    Message update(HttpServletRequest request, DwModifierUpdateCommand command) throws DwException;

    Message enable(HttpServletRequest request, Long id) throws DwException;

    Message disable(HttpServletRequest request, Long id) throws DwException;
}
