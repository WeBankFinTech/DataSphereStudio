package com.webank.wedatasphere.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import com.webank.wedatasphere.warehouse.cqe.DwLayerCreateCommand;
import com.webank.wedatasphere.warehouse.cqe.DwLayerQueryCommand;
import com.webank.wedatasphere.warehouse.cqe.DwLayerUpdateCommand;
import com.webank.wedatasphere.warehouse.dao.domain.DwLayer;
import com.webank.wedatasphere.warehouse.dto.DwLayerDTO;
import com.webank.wedatasphere.warehouse.dto.DwLayerListItemDTO;
import com.webank.wedatasphere.warehouse.dto.PageInfo;
import com.webank.wedatasphere.warehouse.exception.DwException;
import com.webank.wedatasphere.warehouse.dao.mapper.DwLayerMapper;
//import com.webank.wedatasphere.warehouse.mapper.DwLayerModelMapper;
import com.webank.wedatasphere.warehouse.service.DwLayerService;
import com.webank.wedatasphere.warehouse.utils.PreconditionUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class DwLayerServiceImpl implements DwLayerService {

    private final DwLayerMapper dwLayerMapper;

    @Autowired
    public DwLayerServiceImpl(final DwLayerMapper dwLayerMapper) {
        this.dwLayerMapper = dwLayerMapper;
    }

    @Transactional
    @Override
    public Message createDwCustomLayer(HttpServletRequest request, DwLayerCreateCommand command) throws DwException {
        String name = command.getName();
        String enName = command.getEnName();
        String databases = command.getDatabases();
        Integer order = command.getOrder();
//        String autoCollectStrategy = command.getAutoCollectStrategy();
        String owner = command.getOwner();
        String description = command.getDescription();
        String principalName = command.getPrincipalName();

        name = PreconditionUtil.checkStringArgumentNotBlankTrim(name, DwException.argumentReject("name should not empty"));
        enName = PreconditionUtil.checkStringArgumentNotBlankTrim(enName, DwException.argumentReject("name alias should not empty"));
        owner = PreconditionUtil.checkStringArgumentNotBlankTrim(owner, DwException.argumentReject("charge user should not empty"));
//        authority = PreconditionUtil.checkStringArgumentNotBlankTrim(authority, DwException.argumentReject("authority should not empty"));
        QueryWrapper<DwLayer> nameUniqueCheckQuery = new QueryWrapper<>();
        nameUniqueCheckQuery.eq("name", name);
        nameUniqueCheckQuery.eq("status", Boolean.TRUE);
        DwLayer exist = this.dwLayerMapper.selectOne(nameUniqueCheckQuery);
        PreconditionUtil.checkState(Objects.isNull(exist), DwException.stateReject("layer name aleardy exists"));

        if (Strings.isBlank(databases)) {
            databases = "ALL";
        }
        if (Objects.isNull(order)) {
            order = 1;
        }

//        if (Strings.isBlank(autoCollectStrategy)) {
//            autoCollectStrategy = "{}";
//        }

//        String user = "hdfs";

        Date now = new Date();

        DwLayer layer = new DwLayer();
        layer.setName(name);
        layer.setEnName(enName);
        layer.setDescription(description);
        layer.setPrincipalName(principalName);
        layer.setOwner(owner);
        layer.setDbs(databases);
//        layer.setAutoCollectStrategy(autoCollectStrategy);
        // 设置为 FALSE ，因为是自定义分层
        layer.setPreset(Boolean.FALSE);
        layer.setIsAvailable(Boolean.TRUE);
//        layer.setCreateUser(user);
        layer.setSort(order);
        layer.setCreateTime(now);
        layer.setUpdateTime(now);
        layer.setStatus(Boolean.TRUE);
        layer.setLockVersion(1L);

        int insert = this.dwLayerMapper.insert(layer);
        PreconditionUtil.checkState(1 == insert, DwException.stateReject("create dw layer failed"));

        return Message.ok().data("id", layer.getId());
    }

    @Override
    public Message getLayerById(HttpServletRequest request, Long id) throws DwException {
        PreconditionUtil.checkArgument(!Objects.isNull(id), DwException.argumentReject("id should not be null"));

        DwLayer layer = this.dwLayerMapper.selectById(id);
        PreconditionUtil.checkState(!Objects.isNull(layer), DwException.stateReject("layer not found"));
        PreconditionUtil.checkState(layer.getStatus(), DwException.stateReject("layer has been removed"));

//        DwLayerDTO dto = this.dwLayerModelMapper.toDTO(layer);
        DwLayerDTO dto = new DwLayerDTO();
        BeanUtils.copyProperties(layer, dto);

        return Message.ok().data("item", dto);
    }


    @Override
    public Message getAllLayers(HttpServletRequest request) throws DwException {
        QueryWrapper<DwLayer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", Boolean.TRUE);
        List<DwLayer> records = this.dwLayerMapper.selectList(queryWrapper);

        List<DwLayerListItemDTO> dtos = new ArrayList<>();
        for (DwLayer record : records) {
            DwLayerListItemDTO dto = new DwLayerListItemDTO();
            BeanUtils.copyProperties(record, dto);
            dtos.add(dto);
        }

        return Message.ok().data("list", dtos);
    }


    @Override
    public Message getAllPresetLayers(HttpServletRequest request) throws DwException {
        QueryWrapper<DwLayer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", Boolean.TRUE);
        queryWrapper.eq("preset", Boolean.TRUE);
        List<DwLayer> records = this.dwLayerMapper.selectList(queryWrapper);
//        List<DwLayerListItemDTO> dtos = DwLayerModelMapper.INSTANCE.toList(dwLayers);
        List<DwLayerListItemDTO> dtos = new ArrayList<>();

        for (DwLayer record : records) {
            DwLayerListItemDTO dto = new DwLayerListItemDTO();
            BeanUtils.copyProperties(record, dto);
            dtos.add(dto);
        }

        return Message.ok().data("list", dtos);
    }

    @Override
    public Message queryPagedCustomLayers(HttpServletRequest request, DwLayerQueryCommand command) throws DwException {
        Integer page = command.getPage();
        Integer size = command.getSize();
        String name = command.getName();
        Boolean enabled = command.getEnabled();
        if (Objects.isNull(page))
            page = 1;

        if (Objects.isNull(size))
            size = 10;

        QueryWrapper<DwLayer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", Boolean.TRUE);
        queryWrapper.eq("preset", Boolean.FALSE);
        if (Strings.isNotBlank(name)) {
            queryWrapper.like("name", name).or().like("en_name", name);
        }
        if (!Objects.isNull(enabled)) {
            queryWrapper.eq("is_available", enabled);
        }

        Page<DwLayer> queryPage = new Page<>(page, size);

        IPage<DwLayer> _page = this.dwLayerMapper.selectPage(queryPage, queryWrapper);

        List<DwLayer> records = _page.getRecords();

//        List<DwLayerListItemDTO> list = DwLayerModelMapper.INSTANCE.toList(records);
        List<DwLayerListItemDTO> list = new ArrayList<>();

        for (DwLayer record : records) {
            DwLayerListItemDTO dto = new DwLayerListItemDTO();
            BeanUtils.copyProperties(record, dto);
            list.add(dto);
        }


        PageInfo<DwLayerListItemDTO> __page = new PageInfo<>(list, _page.getCurrent(), _page.getSize(), _page.getTotal());

        return Message.ok().data("page", __page);
    }

    // TODO 后面需要检查主题域下有没有关联的 Hive 表，才决定是否删除
    @Transactional
    @Override
    public Message deleteById(HttpServletRequest request, Long id) throws DwException {
        PreconditionUtil.checkArgument(!Objects.isNull(id), DwException.argumentReject("id should not be null"));
        DwLayer layer = this.dwLayerMapper.selectById(id);
        PreconditionUtil.checkState(!Objects.isNull(layer), DwException.stateReject("layer not found"));
        PreconditionUtil.checkState(Objects.equals(Boolean.FALSE, layer.getPreset()), DwException.stateReject("preset layer could not be removed"));
        if (Objects.equals(Boolean.FALSE, layer.getStatus())) {
            return Message.ok();
        }
        layer.setStatus(Boolean.FALSE);
        int i = this.dwLayerMapper.updateById(layer);
        PreconditionUtil.checkState(1 == i, DwException.stateReject("remove action failed"));
        return Message.ok();
    }

    /**
     * 更新操作涉及到 预置分层和自定义分层
     *
     * 预置分层是不能更新名称的，其它字段的更新和自定义分层一致。
     * 所以针对预置分层，是不需要传递 name 参数的，所以要稍微区分逻辑判断来处理
     *
     * 预留分层的 name name_alias  charge_user 无法改变
     */
    @Transactional
    @Override
    public Message update(HttpServletRequest request, DwLayerUpdateCommand command) throws DwException {
        // 基本参数校验
        Long id = command.getId();
        PreconditionUtil.checkArgument(!Objects.isNull(id), DwException.argumentReject("id should not be null"));

        // 实体校验
        DwLayer layer = this.dwLayerMapper.selectById(id);
        PreconditionUtil.checkState(!Objects.isNull(layer), DwException.stateReject("layer not found"));

        // 分层类型后的name参数单独校验
        String name = command.getName();
        String enName = command.getEnName();
        String owner = command.getOwner();
        Integer order = command.getOrder();
        String description = command.getDescription();
        String principalName = command.getPrincipalName();
//        authority = PreconditionUtil.checkStringArgumentNotBlankTrim(authority, DwException.argumentReject("authority should not empty"));
        String databases = command.getDatabases();
//        String autoCollectStrategy = command.getAutoCollectStrategy();
        if (Strings.isBlank(databases)) {
            databases = "ALL";
        }

        if (Objects.isNull(order)) {
            order = 1;
        }

//        if (Strings.isBlank(autoCollectStrategy)) {
//            autoCollectStrategy = "{}";
//        }

        // 预置分层也能修改
//        if (!layer.getPreset()) {
            name = PreconditionUtil.checkStringArgumentNotBlankTrim(name, DwException.argumentReject("name should not empty"));
            enName = PreconditionUtil.checkStringArgumentNotBlankTrim(enName, DwException.argumentReject("en name should not empty"));
            owner = PreconditionUtil.checkStringArgumentNotBlankTrim(owner, DwException.argumentReject("owner should not empty"));
            // 并且自定义分层在更新名称的时候，名称不能重复
            QueryWrapper<DwLayer> nameUniqueCheckQuery = new QueryWrapper<>();
            nameUniqueCheckQuery.eq("name", name);
            nameUniqueCheckQuery.ne("id", id);
            nameUniqueCheckQuery.eq("status", Boolean.TRUE);
            DwLayer exist = this.dwLayerMapper.selectOne(nameUniqueCheckQuery);
            PreconditionUtil.checkState(Objects.isNull(exist), DwException.stateReject("layer name aleardy exists"));
            layer.setName(name);

            // 并且自定义分层在更新名称的时候，name alias不能重复
            QueryWrapper<DwLayer> nameAliasUniqueCheckQuery = new QueryWrapper<>();
            nameAliasUniqueCheckQuery.eq("en_name", name);
            nameAliasUniqueCheckQuery.ne("id", id);
            nameAliasUniqueCheckQuery.eq("status", Boolean.TRUE);
            DwLayer nameAliasExist = this.dwLayerMapper.selectOne(nameAliasUniqueCheckQuery);
            PreconditionUtil.checkState(Objects.isNull(nameAliasExist), DwException.stateReject("layer en name aleardy exists"));
            layer.setEnName(enName);

            layer.setOwner(owner);
//        }

//        String user = SecurityFilter.getLoginUsername(request);

        Long oldVersion = layer.getLockVersion();

        Date now = new Date();
        layer.setDbs(databases);
//        layer.set(autoCollectStrategy);
        layer.setDescription(description);
        layer.setPrincipalName(principalName);
        layer.setSort(order);
//        layer.setModifyUser(user);
        layer.setUpdateTime(now);
        layer.setLockVersion(oldVersion + 1);

        UpdateWrapper<DwLayer> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", layer.getId());
        updateWrapper.eq("lock_version", oldVersion);

        int i = this.dwLayerMapper.update(layer, updateWrapper);

        PreconditionUtil.checkState(1 == i, DwException.stateReject("update layer failed"));
        return Message.ok();
    }

    @Transactional
    @Override
    public Message enable(HttpServletRequest request, Long id) throws DwException {
        changeEnable(request, id, Boolean.TRUE);
        return Message.ok();
    }

    @Transactional
    @Override
    public Message disable(HttpServletRequest request, Long id) throws DwException {
        changeEnable(request, id, Boolean.FALSE);
        return Message.ok();
    }

    private void changeEnable(HttpServletRequest request, Long id, Boolean enabled) throws DwException {
        PreconditionUtil.checkArgument(!Objects.isNull(id), DwException.argumentReject("id should not be null"));

        DwLayer record = this.dwLayerMapper.selectById(id);
        PreconditionUtil.checkState(!Objects.isNull(record), DwException.stateReject("layer not found"));
        PreconditionUtil.checkState(record.getStatus(), DwException.stateReject("layer has been removed"));
        if (Objects.equals(enabled, record.getIsAvailable())) {
            return;
        }

//        String user = "hdfs";
        Long oldVersion = record.getLockVersion();
//        record.setModifyUser(user);
        record.setUpdateTime(new Date());
        record.setIsAvailable(enabled);
        record.setLockVersion(oldVersion + 1);
        UpdateWrapper<DwLayer> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", record.getId());
        updateWrapper.eq("lock_version", oldVersion);
        int update = this.dwLayerMapper.update(record, updateWrapper);
        PreconditionUtil.checkState(1 == update, DwException.stateReject(enabled ? "enable" : "disable" +  " failed"));
    }
}
