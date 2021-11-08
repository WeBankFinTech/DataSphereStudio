package com.webank.wedatasphere.warehouse.service;

import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.warehouse.cqe.DwStatisticalPeriodCreateCommand;
import com.webank.wedatasphere.warehouse.cqe.DwStatisticalPeriodQueryCommand;
import com.webank.wedatasphere.warehouse.cqe.DwStatisticalPeriodUpdateCommand;
import com.webank.wedatasphere.warehouse.exception.DwException;

import javax.servlet.http.HttpServletRequest;

public interface DwStatisticalPeriodService {
    Message queryAll(HttpServletRequest request, DwStatisticalPeriodQueryCommand command) throws DwException;

    Message queryPage(HttpServletRequest request, DwStatisticalPeriodQueryCommand command) throws DwException;

    Message create(HttpServletRequest request, DwStatisticalPeriodCreateCommand command) throws DwException;

    Message getById(HttpServletRequest request, Long id) throws DwException;

    Message deleteById(HttpServletRequest request, Long id) throws DwException;

    Message update(HttpServletRequest request, DwStatisticalPeriodUpdateCommand command) throws DwException;

    Message enable(HttpServletRequest request, Long id) throws DwException;

    Message disable(HttpServletRequest request, Long id) throws DwException;
}
