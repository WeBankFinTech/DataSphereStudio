package com.webank.wedatasphere.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.warehouse.cqe.DwModifierCreateCommand;
import com.webank.wedatasphere.warehouse.cqe.DwModifierQueryCommand;
import com.webank.wedatasphere.warehouse.cqe.DwModifierUpdateCommand;
import com.webank.wedatasphere.warehouse.dao.domain.*;
import com.webank.wedatasphere.warehouse.dao.domain.DwLayer;
import com.webank.wedatasphere.warehouse.dao.domain.DwThemeDomain;
import com.webank.wedatasphere.warehouse.dao.mapper.DwModifierListMapper;
import com.webank.wedatasphere.warehouse.dao.mapper.DwModifierMapper;
import com.webank.wedatasphere.warehouse.dao.mapper.DwLayerMapper;
import com.webank.wedatasphere.warehouse.dao.mapper.DwThemeDomainMapper;
import com.webank.wedatasphere.warehouse.dto.DwModifierDTO;
import com.webank.wedatasphere.warehouse.dto.DwModifierListDTO;
import com.webank.wedatasphere.warehouse.dto.DwModifierListItemDTO;
import com.webank.wedatasphere.warehouse.dto.PageInfo;
import com.webank.wedatasphere.warehouse.exception.DwException;
import com.webank.wedatasphere.warehouse.service.DwModifierService;
import com.webank.wedatasphere.warehouse.utils.PreconditionUtil;
import com.webank.wedatasphere.warehouse.utils.RegexUtil;
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
public class DwModifierServiceImpl implements DwModifierService {

    private final DwModifierMapper dwModifierMapper;
    private final DwModifierListMapper dwModifierListMapper;
    private final DwLayerMapper dwLayerMapper;
    private final DwThemeDomainMapper dwThemeDomainMapper;

    @Autowired
    public DwModifierServiceImpl(
            DwModifierMapper dwModifierMapper,
            DwModifierListMapper dwModifierListMapper,
            DwLayerMapper dwLayerMapper,
            DwThemeDomainMapper dwThemeDomainMapper
    ) {
        this.dwModifierMapper = dwModifierMapper;
        this.dwModifierListMapper = dwModifierListMapper;
        this.dwLayerMapper = dwLayerMapper;
        this.dwThemeDomainMapper = dwThemeDomainMapper;
    }

    @Override
    public Message queryAllModifiers(HttpServletRequest request, DwModifierQueryCommand command) throws DwException {
        String typeName = command.getName();
        Boolean isAvailable = command.getEnabled();

        QueryWrapper<DwModifier> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", Boolean.TRUE);
        if (!Objects.isNull(isAvailable)) {
            queryWrapper.eq("is_available", isAvailable);
        }
        if (Strings.isNotBlank(typeName)) {
            queryWrapper.and(qw -> {
                queryWrapper.like("modifier_type", typeName).or().like("modifier_type_en", typeName);
            });
        }
        List<DwModifier> records = this.dwModifierMapper.selectList(queryWrapper);

        List<DwModifierListItemDTO> list = new ArrayList<>();
        DwModifierListItemDTO dto;
        for (DwModifier modifier : records) {
            dto = new DwModifierListItemDTO();
            BeanUtils.copyProperties(modifier, dto);
            list.add(dto);
        }
        return Message.ok().data("list", list);
    }

    @Override
    public Message queryPage(HttpServletRequest request, DwModifierQueryCommand command) throws DwException {
        Integer page = command.getPage();
        Integer size = command.getSize();
        String typeName = command.getName();
        if (Objects.isNull(page))
            page = 1;

        if (Objects.isNull(size))
            size = 10;

        QueryWrapper<DwModifier> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", Boolean.TRUE);
        if (Strings.isNotBlank(typeName)) {
            queryWrapper.like("modifier_type", typeName);
        }

        if (!Objects.isNull(command.getEnabled())) {
            queryWrapper.eq("is_available", command.getEnabled());
        }

        Page<DwModifier> queryPage = new Page<>(page, size);

        IPage<DwModifier> _page = this.dwModifierMapper.selectPage(queryPage, queryWrapper);

        List<DwModifier> records = _page.getRecords();

        List<DwModifierListItemDTO> list = new ArrayList<>();
        DwModifierListItemDTO dto;
        for (DwModifier modifier : records) {
            dto = new DwModifierListItemDTO();
            BeanUtils.copyProperties(modifier, dto);
            list.add(dto);
        }

//        List<DwModifierListItemDTO> list = DwDecorationModelMapper.INSTANCE.toList(records);
//        List<DwModifierListItemDTO> list = new ArrayList<>();

        PageInfo<DwModifierListItemDTO> __page = new PageInfo<>(list, _page.getCurrent(), _page.getSize(), _page.getTotal());

        return Message.ok().data("page", __page);
    }

    @Transactional
    @Override
    public Message create(HttpServletRequest request, DwModifierCreateCommand command) throws DwException {
        Long themeDomainId = command.getThemeDomainId();
        Long layerId = command.getLayerId();
        String typeName = command.getTypeName();
        String typeEnName = command.getTypeEnName();

//        String typeNameAlias = command.getTypeNameAlias();
//        String decorationList = command.getDecorationList();
        String description = command.getDescription();

        PreconditionUtil.checkArgument(!Objects.isNull(themeDomainId), DwException.argumentReject("theme domain id should not empty"));
        PreconditionUtil.checkArgument(!Objects.isNull(layerId), DwException.argumentReject("layer id should not empty"));
        typeName = PreconditionUtil.checkStringArgumentNotBlankTrim(typeName, DwException.argumentReject("type name should not empty"));
        PreconditionUtil.checkArgument(RegexUtil.checkCnName(typeName), DwException.argumentReject("type name must be digitg, chinese and underline"));
        typeEnName = PreconditionUtil.checkStringArgumentNotBlankTrim(typeEnName, DwException.argumentReject("type en name should not empty"));
        PreconditionUtil.checkArgument(RegexUtil.checkEnName(typeEnName), DwException.argumentReject("type en name must be digitg, alpha and underline"));
//        typeNameAlias = PreconditionUtil.checkStringArgumentNotBlankTrim(typeNameAlias, DwException.argumentReject("typeNameAlias should not empty"));

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

        // typeName 唯一性检测
        QueryWrapper<DwModifier> nameUniqueCheckQuery = new QueryWrapper<>();
        nameUniqueCheckQuery.eq("modifier_type", typeName);
        nameUniqueCheckQuery.eq("status", Boolean.TRUE);
        DwModifier exist = this.dwModifierMapper.selectOne(nameUniqueCheckQuery);
        PreconditionUtil.checkState(Objects.isNull(exist), DwException.stateReject("modifier type name aleardy exists"));

        // typeNameEn 唯一性检测
        QueryWrapper<DwModifier> nameAliasUniqueCheckQuery = new QueryWrapper<>();
        nameAliasUniqueCheckQuery.eq("modifier_type_en", typeEnName);
        nameAliasUniqueCheckQuery.eq("status", Boolean.TRUE);
        DwModifier exist2 = this.dwModifierMapper.selectOne(nameAliasUniqueCheckQuery);
        PreconditionUtil.checkState(Objects.isNull(exist2), DwException.stateReject("modifier type en name aleardy exists"));

        Date now = new Date();

        DwModifier record = new DwModifier();
        record.setThemeDomainId(dwThemeDomain.getId());
        record.setThemeArea(dwThemeDomain.getName());
        record.setLayerId(dwLayer.getId());
        record.setLayerArea(dwLayer.getName());
        record.setModifierType(typeName);
        record.setModifierTypeEn(typeEnName);
        record.setDescription(description);
        record.setIsAvailable(Boolean.TRUE);
        record.setCreateTime(now);
        record.setUpdateTime(now);
        record.setStatus(Boolean.TRUE);
        record.setLockVersion(1L);

        int insert = this.dwModifierMapper.insert(record);
        PreconditionUtil.checkState(1 == insert, DwException.stateReject("create dw modifier failed"));

        // 新增修饰词列表信息
        List<DwModifierCreateCommand.DwModifierCreateListItem> list = command.getList();
        if (!Objects.isNull(list) && !list.isEmpty()) {
            for (DwModifierCreateCommand.DwModifierCreateListItem item : list) {
                String name = item.getName();
                String identifier = item.getIdentifier();
                String formula = item.getFormula();
                if (Strings.isBlank(name) || Strings.isBlank(identifier) || Strings.isBlank(formula)) {
                    continue;
                }

                DwModifierList m = new DwModifierList();
                m.setModifierId(record.getId());
                m.setName(name);
                m.setIdentifier(identifier);
                m.setFormula(formula);
                m.setCreateTime(now);
                m.setUpdateTime(now);
                this.dwModifierListMapper.insert(m);
            }
        }

        return Message.ok().data("id", record.getId());
    }

    @Override
    public Message getById(HttpServletRequest request, Long id) throws DwException {
        PreconditionUtil.checkArgument(!Objects.isNull(id), DwException.argumentReject("id should not be null"));

        DwModifier record = this.dwModifierMapper.selectById(id);
        PreconditionUtil.checkState(!Objects.isNull(record), DwException.stateReject("modifier not found"));
        PreconditionUtil.checkState(record.getStatus(), DwException.stateReject("modifier has been removed"));

        DwModifierDTO dto = new DwModifierDTO();
//        DwModifierDTO dto = this.dwModifierModelMapper.toDTO(record);
        BeanUtils.copyProperties(record, dto);

        QueryWrapper<DwModifierList> dwModifierListQueryWrapper = new QueryWrapper<>();
        dwModifierListQueryWrapper.eq("modifier_id", record.getId());
        List<DwModifierList> dwModifierLists = this.dwModifierListMapper.selectList(dwModifierListQueryWrapper);

        List<DwModifierListDTO> dwModifierListDTOs = new ArrayList<>();
        for (DwModifierList item : dwModifierLists) {
            DwModifierListDTO dwModifierListDTO = new DwModifierListDTO();/*this.dwModifierListModelMapper.toListItem(item);*/
            BeanUtils.copyProperties(item, dwModifierListDTO);
            dwModifierListDTOs.add(dwModifierListDTO);
        }

        dto.setList(dwModifierListDTOs);

        return Message.ok().data("item", dto);
    }

    @Transactional
    @Override
    public Message deleteById(HttpServletRequest request, Long id) throws DwException {
        PreconditionUtil.checkArgument(!Objects.isNull(id), DwException.argumentReject("id should not be null"));
        DwModifier record = this.dwModifierMapper.selectById(id);
        PreconditionUtil.checkState(!Objects.isNull(record), DwException.stateReject("modifier not found"));
        if (Objects.equals(Boolean.FALSE, record.getStatus())) {
            return Message.ok();
        }
        record.setStatus(Boolean.FALSE);
        int i = this.dwModifierMapper.updateById(record);
        PreconditionUtil.checkState(1 == i, DwException.stateReject("remove action failed"));
        return Message.ok();
    }

    @Transactional
    @Override
    public Message update(HttpServletRequest request, DwModifierUpdateCommand command) throws DwException {
        Long id = command.getId();
        Long themeDomainId = command.getThemeDomainId();
        Long layerId = command.getLayerId();
        String typeName = command.getTypeName();
        String typeEnName = command.getTypeEnName();
        String description = command.getDescription();

        PreconditionUtil.checkArgument(!Objects.isNull(id), DwException.argumentReject("id should not empty"));
        PreconditionUtil.checkArgument(!Objects.isNull(themeDomainId), DwException.argumentReject("theme domain id should not empty"));
        PreconditionUtil.checkArgument(!Objects.isNull(layerId), DwException.argumentReject("layer id should not empty"));
//        typeName = PreconditionUtil.checkStringArgumentNotBlankTrim(typeName, DwException.argumentReject("modifier type name should not empty"));
        typeName = PreconditionUtil.checkStringArgumentNotBlankTrim(typeName, DwException.argumentReject("type name should not empty"));
        PreconditionUtil.checkArgument(RegexUtil.checkCnName(typeName), DwException.argumentReject("type name must be digitg, chinese and underline"));

        typeEnName = PreconditionUtil.checkStringArgumentNotBlankTrim(typeEnName, DwException.argumentReject("type en name should not empty"));
        PreconditionUtil.checkArgument(RegexUtil.checkEnName(typeEnName), DwException.argumentReject("type en name must be digitg, alpha and underline"));
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

        // 实体校验
        DwModifier record = this.dwModifierMapper.selectById(id);
        PreconditionUtil.checkState(!Objects.isNull(record), DwException.stateReject("modifier not found"));

        // typeName 唯一性检测
        QueryWrapper<DwModifier> nameUniqueCheckQuery = new QueryWrapper<>();
        nameUniqueCheckQuery.eq("modifier_type", typeName);
        nameUniqueCheckQuery.ne("id", id);
        nameUniqueCheckQuery.eq("status", Boolean.TRUE);
        DwModifier exist = this.dwModifierMapper.selectOne(nameUniqueCheckQuery);
        PreconditionUtil.checkState(Objects.isNull(exist), DwException.stateReject("modifier type name aleardy exists"));
        // typeNameAlias 唯一性检测
        QueryWrapper<DwModifier> nameAliasUniqueCheckQuery = new QueryWrapper<>();
        nameAliasUniqueCheckQuery.eq("modifier_type_en", typeEnName);
        nameAliasUniqueCheckQuery.ne("id", id);
        nameAliasUniqueCheckQuery.eq("status", Boolean.TRUE);
        DwModifier exist2 = this.dwModifierMapper.selectOne(nameAliasUniqueCheckQuery);
        PreconditionUtil.checkState(Objects.isNull(exist2), DwException.stateReject("modifier type en name aleardy exists"));

        Long oldVersion = record.getLockVersion();

        Date now = new Date();

//        record.setSubjectDomainId(dwThemeDomain.getId());
//        record.setLayerId(dwLayer.getId());
        record.setModifierType(typeName);
        record.setThemeDomainId(dwThemeDomain.getId());
        record.setThemeArea(dwThemeDomain.getName());
        record.setLayerId(dwLayer.getId());
        record.setLayerArea(dwLayer.getName());
        record.setDescription(description);
//        record.setModifyUser(user);
        record.setUpdateTime(now);
        record.setLockVersion(oldVersion + 1);

        UpdateWrapper<DwModifier> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", record.getId());
        updateWrapper.eq("lock_version", oldVersion);

        int i = this.dwModifierMapper.update(record, updateWrapper);
        PreconditionUtil.checkState(1 == i, DwException.stateReject("update modifier failed"));

        // 更新修饰词列表信息
        List<DwModifierUpdateCommand.DwModifierUpdateListItem> list = command.getList();
        if (!Objects.isNull(list) && !list.isEmpty()) {
            // 如果不为空，先清空某个 modifier 下的关联修饰词
            QueryWrapper<DwModifierList> dwModifierListQueryWrapper = new QueryWrapper<>();
            dwModifierListQueryWrapper.eq("modifier_id", record.getId());
            this.dwModifierListMapper.delete(dwModifierListQueryWrapper);

            // 删除后再新增
            for (DwModifierUpdateCommand.DwModifierUpdateListItem item : list) {
                String name = item.getName();
                String identifier = item.getIdentifier();
                String formula = item.getFormula();
                if (Strings.isBlank(name) || Strings.isBlank(identifier) || Strings.isBlank(formula)) {
                    continue;
                }

                DwModifierList m = new DwModifierList();
                m.setModifierId(record.getId());
                m.setName(name);
                m.setIdentifier(identifier);
                m.setFormula(formula);
                m.setCreateTime(now);
                m.setUpdateTime(now);
                this.dwModifierListMapper.insert(m);
            }
        } else {
            QueryWrapper<DwModifierList> dwModifierListQueryWrapper = new QueryWrapper<>();
            dwModifierListQueryWrapper.eq("modifier_id", record.getId());
            this.dwModifierListMapper.delete(dwModifierListQueryWrapper);
        }


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

        DwModifier record = this.dwModifierMapper.selectById(id);
        PreconditionUtil.checkState(!Objects.isNull(record), DwException.stateReject("modifier not found"));
        PreconditionUtil.checkState(record.getStatus(), DwException.stateReject("modifier has been removed"));
        if (Objects.equals(enabled, record.getIsAvailable())) {
            return;
        }

        Long oldVersion = record.getLockVersion();
        record.setUpdateTime(new Date());
        record.setIsAvailable(enabled);
        record.setLockVersion(oldVersion + 1);
        UpdateWrapper<DwModifier> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", record.getId());
        updateWrapper.eq("lock_version", oldVersion);
        int update = this.dwModifierMapper.update(record, updateWrapper);
        PreconditionUtil.checkState(1 == update, DwException.stateReject(enabled ? "enable" : "disable" +  " failed"));
    }
}
