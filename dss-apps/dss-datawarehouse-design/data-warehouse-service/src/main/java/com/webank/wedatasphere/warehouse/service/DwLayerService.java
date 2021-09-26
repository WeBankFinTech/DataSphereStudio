package com.webank.wedatasphere.warehouse.service;

import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.warehouse.cqe.DwLayerCreateCommand;
import com.webank.wedatasphere.warehouse.cqe.DwLayerQueryCommand;
import com.webank.wedatasphere.warehouse.cqe.DwLayerUpdateCommand;
import com.webank.wedatasphere.warehouse.exception.DwException;

import javax.servlet.http.HttpServletRequest;

public interface DwLayerService {
    Message createDwCustomLayer(HttpServletRequest request, final DwLayerCreateCommand command) throws DwException;

    Message getLayerById(HttpServletRequest request, final Long id) throws DwException;

    Message getAllPresetLayers(HttpServletRequest request) throws DwException;

    Message queryPagedCustomLayers(HttpServletRequest request, final DwLayerQueryCommand command) throws DwException;

    Message deleteById(HttpServletRequest request, Long id) throws DwException;

    Message update(HttpServletRequest request, DwLayerUpdateCommand command) throws DwException;

    Message enable(HttpServletRequest request, Long id) throws DwException;

    Message disable(HttpServletRequest request, Long id) throws DwException;

    Message getAllLayers(HttpServletRequest request) throws DwException;
}
