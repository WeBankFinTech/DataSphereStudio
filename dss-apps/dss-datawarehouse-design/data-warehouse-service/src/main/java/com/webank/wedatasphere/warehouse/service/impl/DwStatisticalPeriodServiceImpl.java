package com.webank.wedatasphere.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.warehouse.cqe.DwStatisticalPeriodCreateCommand;
import com.webank.wedatasphere.warehouse.cqe.DwStatisticalPeriodQueryCommand;
import com.webank.wedatasphere.warehouse.cqe.DwStatisticalPeriodUpdateCommand;
import com.webank.wedatasphere.warehouse.dao.domain.DwLayer;
import com.webank.wedatasphere.warehouse.dao.domain.DwStatisticalPeriod;
import com.webank.wedatasphere.warehouse.dao.domain.DwThemeDomain;
import com.webank.wedatasphere.warehouse.dao.mapper.DwLayerMapper;
import com.webank.wedatasphere.warehouse.dao.mapper.DwStatisticalPeriodMapper;
import com.webank.wedatasphere.warehouse.dao.mapper.DwThemeDomainMapper;
import com.webank.wedatasphere.warehouse.dao.vo.DwStatisticalPeriodVo;
import com.webank.wedatasphere.warehouse.dto.PageInfo;
import com.webank.wedatasphere.warehouse.exception.DwException;
import com.webank.wedatasphere.warehouse.service.DwStatisticalPeriodService;
import com.webank.wedatasphere.warehouse.utils.PreconditionUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class DwStatisticalPeriodServiceImpl implements DwStatisticalPeriodService {

    private final DwStatisticalPeriodMapper dwStatisticalPeriodMapper;
    private final DwLayerMapper dwLayerMapper;
    private final DwThemeDomainMapper dwThemeDomainMapper;

    @Autowired
    public DwStatisticalPeriodServiceImpl(
            final DwStatisticalPeriodMapper dwStatisticalPeriodMapper,
            final DwLayerMapper dwLayerMapper,
            final DwThemeDomainMapper dwThemeDomainMapper

    ) {
        this.dwStatisticalPeriodMapper = dwStatisticalPeriodMapper;
        this.dwLayerMapper = dwLayerMapper;
        this.dwThemeDomainMapper = dwThemeDomainMapper;
    }

    @Override
    public Message queryAll(HttpServletRequest request, DwStatisticalPeriodQueryCommand command) throws DwException {
        String name = command.getName();
        QueryWrapper<DwStatisticalPeriod> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", Boolean.TRUE);
        if (Strings.isNotBlank(name)) {
            queryWrapper.and(qw -> {
                qw.like("name", name).or().like("en_name", name);
            });
//            queryWrapper.like("name", name).or().like("en_name", name);
        }

        List<DwStatisticalPeriodVo> records = this.dwStatisticalPeriodMapper.selectItems(queryWrapper);
        return Message.ok().data("list", records);
    }

    @Override
    public Message queryPage(HttpServletRequest request, DwStatisticalPeriodQueryCommand command) throws DwException {
        Integer page = command.getPage();
        Integer size = command.getSize();
        String name = command.getName();
        if (Objects.isNull(page))
            page = 1;

        if (Objects.isNull(size))
            size = 10;

        QueryWrapper<DwStatisticalPeriod> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", Boolean.TRUE);
        if (Strings.isNotBlank(name)) {
            queryWrapper.and(qw -> {
                qw.like("name", name).or().like("en_name", name);
            });
        }

        if (!Objects.isNull(command.getEnabled())) {
            queryWrapper.eq("is_available", command.getEnabled());
        }

        Page<DwStatisticalPeriod> queryPage = new Page<>(page, size);

        IPage<DwStatisticalPeriodVo> _page = this.dwStatisticalPeriodMapper.selectPageItems(queryPage, queryWrapper);

        List<DwStatisticalPeriodVo> records = _page.getRecords();

        PageInfo<DwStatisticalPeriodVo> __page = new PageInfo<>(records, _page.getCurrent(), _page.getSize(), _page.getTotal());

        return Message.ok().data("page", __page);
    }

    @Transactional
    @Override
    public Message create(HttpServletRequest request, DwStatisticalPeriodCreateCommand command) throws DwException {
        String name = command.getName();
        String enName = command.getEnName();
        String principalName = command.getPrincipalName();
        String owner = command.getOwner();
        String description = command.getDescription();
        Long themeDomainId = command.getThemeDomainId();
        Long layerId = command.getLayerId();
        String statStartFormula = command.getStatStartFormula();
        String statEndFormula = command.getStatEndFormula();

        name = PreconditionUtil.checkStringArgumentNotBlankTrim(name, DwException.argumentReject("name should not empty"));
        enName = PreconditionUtil.checkStringArgumentNotBlankTrim(enName, DwException.argumentReject("en name should not empty"));
        owner = PreconditionUtil.checkStringArgumentNotBlankTrim(owner, DwException.argumentReject("owner should not empty"));
        statStartFormula = PreconditionUtil.checkStringArgumentNotBlankTrim(statStartFormula, DwException.argumentReject("stat start formula should not empty"));
        statEndFormula = PreconditionUtil.checkStringArgumentNotBlankTrim(statEndFormula, DwException.argumentReject("stat end formula should not empty"));
        PreconditionUtil.checkState(!Objects.isNull(layerId), DwException.argumentReject("layer id not empty"));
        PreconditionUtil.checkState(!Objects.isNull(themeDomainId), DwException.argumentReject("theme domain id not empty"));

        // 验证 主题域  分层是否存在
        QueryWrapper<DwLayer> layerQueryWrapper = new QueryWrapper<>();
        layerQueryWrapper.eq("id", layerId);
        layerQueryWrapper.eq("status", Boolean.TRUE);
        DwLayer dwLayer = this.dwLayerMapper.selectOne(layerQueryWrapper);
        PreconditionUtil.checkState(!Objects.isNull(dwLayer), DwException.stateReject("layer not found"));
        PreconditionUtil.checkState(dwLayer.getIsAvailable(), DwException.stateReject("layer disabled"));

        QueryWrapper<DwThemeDomain> themeDomainQueryWrapper = new QueryWrapper<>();
        themeDomainQueryWrapper.eq("id", themeDomainId);
        themeDomainQueryWrapper.eq("status", Boolean.TRUE);
        DwThemeDomain dwThemeDomain = this.dwThemeDomainMapper.selectOne(themeDomainQueryWrapper);
        PreconditionUtil.checkState(!Objects.isNull(dwThemeDomain), DwException.stateReject("theme domain not found"));

//        String user = "hdfs";
        Date now = new Date();

        DwStatisticalPeriod record = new DwStatisticalPeriod();
        record.setName(name);
        record.setEnName(enName);
        record.setThemeDomainId(themeDomainId);
        record.setLayerId(layerId);
        record.setDescription(description);
        record.setPrincipalName(principalName);
        record.setStartTimeFormula(statStartFormula);
        record.setEndTimeFormula(statEndFormula);
        record.setOwner(owner);
        record.setIsAvailable(Boolean.TRUE);
        record.setCreateTime(now);
        record.setUpdateTime(now);
        record.setStatus(Boolean.TRUE);
        record.setLockVersion(1L);

        int insert = this.dwStatisticalPeriodMapper.insert(record);
        PreconditionUtil.checkState(1 == insert, DwException.stateReject("create statistical period failed"));

        return Message.ok().data("id", record.getId());
    }

    @Override
    public Message getById(HttpServletRequest request, Long id) throws DwException {
        PreconditionUtil.checkArgument(!Objects.isNull(id), DwException.argumentReject("id should not be null"));

        DwStatisticalPeriodVo record = this.dwStatisticalPeriodMapper.selectItemById(id);
        PreconditionUtil.checkState(!Objects.isNull(record), DwException.stateReject("statistical period not found"));
        PreconditionUtil.checkState(record.getStatus(), DwException.stateReject("statistical period has been removed"));

        return Message.ok().data("item", record);
    }

    @Transactional
    @Override
    public Message deleteById(HttpServletRequest request, Long id) throws DwException {
        PreconditionUtil.checkArgument(!Objects.isNull(id), DwException.argumentReject("id should not be null"));
        DwStatisticalPeriod record = this.dwStatisticalPeriodMapper.selectById(id);
        PreconditionUtil.checkState(!Objects.isNull(record), DwException.stateReject("statistical period not found"));
        if (Objects.equals(Boolean.FALSE, record.getStatus())) {
            return Message.ok();
        }
        Long oldLockVersion = record.getLockVersion();
        QueryWrapper<DwStatisticalPeriod> updateWrapper = new QueryWrapper<>();
        updateWrapper.eq("lock_version", oldLockVersion);
        updateWrapper.eq("id", record.getId());

        DwStatisticalPeriod updateBean = new DwStatisticalPeriod();
        updateBean.setLockVersion(oldLockVersion + 1);
        updateBean.setStatus(Boolean.FALSE);

        int i = this.dwStatisticalPeriodMapper.update(updateBean, updateWrapper);
        PreconditionUtil.checkState(1 == i, DwException.stateReject("remove action failed"));
        return Message.ok();
    }

    @Transactional
    @Override
    public Message update(HttpServletRequest request, DwStatisticalPeriodUpdateCommand command) throws DwException {
        Long id = command.getId();
        String name = command.getName();
        String enName = command.getEnName();
        String principalName = command.getPrincipalName();
        String owner = command.getOwner();
        String description = command.getDescription();
        Long themeDomainId = command.getThemeDomainId();
        Long layerId = command.getLayerId();
        String statStartFormula = command.getStatStartFormula();
        String statEndFormula = command.getStatEndFormula();

        PreconditionUtil.checkState(!Objects.isNull(id), DwException.argumentReject("id not empty"));
        name = PreconditionUtil.checkStringArgumentNotBlankTrim(name, DwException.argumentReject("name should not empty"));
        enName = PreconditionUtil.checkStringArgumentNotBlankTrim(enName, DwException.argumentReject("en name should not empty"));
        owner = PreconditionUtil.checkStringArgumentNotBlankTrim(owner, DwException.argumentReject("owner should not empty"));
        statStartFormula = PreconditionUtil.checkStringArgumentNotBlankTrim(statStartFormula, DwException.argumentReject("stat start formula should not empty"));
        statEndFormula = PreconditionUtil.checkStringArgumentNotBlankTrim(statEndFormula, DwException.argumentReject("stat end formula should not empty"));
        PreconditionUtil.checkState(!Objects.isNull(layerId), DwException.argumentReject("layer id not empty"));
        PreconditionUtil.checkState(!Objects.isNull(themeDomainId), DwException.argumentReject("theme domain id not empty"));

        QueryWrapper<DwStatisticalPeriod> statisticalPeriodQueryWrapper = new QueryWrapper<>();
        statisticalPeriodQueryWrapper.eq("id", id);
        statisticalPeriodQueryWrapper.eq("status", Boolean.TRUE);
        DwStatisticalPeriod record = this.dwStatisticalPeriodMapper.selectOne(statisticalPeriodQueryWrapper);
        PreconditionUtil.checkState(!Objects.isNull(record), DwException.stateReject("period not found"));
        PreconditionUtil.checkState(record.getIsAvailable(), DwException.stateReject("period disabled"));

        // 验证 主题域  分层是否存在
        QueryWrapper<DwLayer> layerQueryWrapper = new QueryWrapper<>();
        layerQueryWrapper.eq("id", layerId);
        layerQueryWrapper.eq("status", Boolean.TRUE);
        DwLayer dwLayer = this.dwLayerMapper.selectOne(layerQueryWrapper);
        PreconditionUtil.checkState(!Objects.isNull(dwLayer), DwException.stateReject("layer not found"));
        PreconditionUtil.checkState(dwLayer.getIsAvailable(), DwException.stateReject("layer disabled"));

        QueryWrapper<DwThemeDomain> themeDomainQueryWrapper = new QueryWrapper<>();
        themeDomainQueryWrapper.eq("id", themeDomainId);
        themeDomainQueryWrapper.eq("status", Boolean.TRUE);
        DwThemeDomain dwThemeDomain = this.dwThemeDomainMapper.selectOne(themeDomainQueryWrapper);
        PreconditionUtil.checkState(!Objects.isNull(dwThemeDomain), DwException.stateReject("theme domain not found"));

//        String user = "hdfs";
        Date now = new Date();
        Long oldLockVersion = record.getLockVersion();

        QueryWrapper<DwStatisticalPeriod> updateWrapper = new QueryWrapper<>();
        updateWrapper.eq("lock_version", oldLockVersion);
        updateWrapper.eq("id", record.getId());

        record.setName(name);
        record.setEnName(enName);
        record.setThemeDomainId(themeDomainId);
        record.setLayerId(layerId);
        record.setDescription(description);
        record.setPrincipalName(principalName);
        record.setStartTimeFormula(statStartFormula);
        record.setEndTimeFormula(statEndFormula);
        record.setOwner(owner);
        record.setUpdateTime(now);
        record.setLockVersion(oldLockVersion + 1);

        int update = this.dwStatisticalPeriodMapper.update(record, updateWrapper);
        PreconditionUtil.checkState(1 == update, DwException.stateReject("update statistical period failed"));

        return Message.ok();
    }

    @Override
    public Message enable(HttpServletRequest request, Long id) throws DwException {
        changeEnable(request, id, Boolean.TRUE);
        return Message.ok();
    }

    @Override
    public Message disable(HttpServletRequest request, Long id) throws DwException {
        changeEnable(request, id, Boolean.FALSE);
        return Message.ok();
    }

    private void changeEnable(HttpServletRequest request, Long id, Boolean enabled) throws DwException {
        PreconditionUtil.checkArgument(!Objects.isNull(id), DwException.argumentReject("id should not be null"));

        DwStatisticalPeriod record = this.dwStatisticalPeriodMapper.selectById(id);
        PreconditionUtil.checkState(!Objects.isNull(record), DwException.stateReject("statistical period not found"));
        PreconditionUtil.checkState(record.getStatus(), DwException.stateReject("statistical period has been removed"));
        if (Objects.equals(enabled, record.getIsAvailable())) {
            return;
        }

//        String user = "hdfs";

        Long oldVersion = record.getLockVersion();
        UpdateWrapper<DwStatisticalPeriod> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", record.getId());
        updateWrapper.eq("lock_version", oldVersion);

        DwStatisticalPeriod updateBean = new DwStatisticalPeriod();
        updateBean.setIsAvailable(enabled);
        updateBean.setUpdateTime(new Date());
        updateBean.setLockVersion(oldVersion + 1);

        int update = this.dwStatisticalPeriodMapper.update(updateBean, updateWrapper);
        PreconditionUtil.checkState(1 == update, DwException.stateReject(enabled ? "enable" : "disable" +  " failed"));
    }
}
