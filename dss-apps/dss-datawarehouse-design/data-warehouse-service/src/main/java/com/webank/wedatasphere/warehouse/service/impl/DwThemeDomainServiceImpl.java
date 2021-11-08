package com.webank.wedatasphere.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.warehouse.cqe.DwThemeDomainCreateCommand;
import com.webank.wedatasphere.warehouse.cqe.DwThemeDomainQueryCommand;
import com.webank.wedatasphere.warehouse.cqe.DwThemeDomainUpdateCommand;
import com.webank.wedatasphere.warehouse.dao.domain.DwThemeDomain;
import com.webank.wedatasphere.warehouse.dao.mapper.DwThemeDomainMapper;
import com.webank.wedatasphere.warehouse.dto.DwThemeDomainDTO;
import com.webank.wedatasphere.warehouse.dto.DwThemeDomainListItemDTO;
import com.webank.wedatasphere.warehouse.dto.PageInfo;
import com.webank.wedatasphere.warehouse.exception.DwException;
import com.webank.wedatasphere.warehouse.service.DwThemeDomainService;
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
public class DwThemeDomainServiceImpl implements DwThemeDomainService {

    private final DwThemeDomainMapper dwThemeDomainMapper;

    @Autowired
    public DwThemeDomainServiceImpl(final DwThemeDomainMapper dwThemeDomainMapper) {
        this.dwThemeDomainMapper = dwThemeDomainMapper;
    }

    @Override
    public Message queryAllThemeDomains(HttpServletRequest request, DwThemeDomainQueryCommand command) throws DwException {
        String name = command.getName();

        QueryWrapper<DwThemeDomain> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", Boolean.TRUE);
        if (Strings.isNotBlank(name)) {
            queryWrapper.like("name", name).or().like("en_name", name);
        }

        List<DwThemeDomain> records = this.dwThemeDomainMapper.selectList(queryWrapper);

        List<DwThemeDomainListItemDTO> list = new ArrayList<>();
        for (DwThemeDomain domain : records) {
            DwThemeDomainListItemDTO dwThemeDomainListItemDTO = new DwThemeDomainListItemDTO();
            BeanUtils.copyProperties(domain, dwThemeDomainListItemDTO);
            list.add(dwThemeDomainListItemDTO);
        }

        return Message.ok().data("list", list);
    }

    @Transactional
    @Override
    public Message queryPage(HttpServletRequest request, DwThemeDomainQueryCommand command) throws DwException {
        Integer page = command.getPage();
        Integer size = command.getSize();
        String name = command.getName();
        if (Objects.isNull(page))
            page = 1;

        if (Objects.isNull(size))
            size = 10;

        QueryWrapper<DwThemeDomain> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", Boolean.TRUE);
        if (Strings.isNotBlank(name)) {
            queryWrapper.and(qw -> {
                qw.like("name", name).or().like("en_name", name);
            });
        }
        Page<DwThemeDomain> queryPage = new Page<>(page, size);

        IPage<DwThemeDomain> _page = this.dwThemeDomainMapper.selectPage(queryPage, queryWrapper);

        List<DwThemeDomain> records = _page.getRecords();
        List<DwThemeDomainListItemDTO> list = new ArrayList<>();
        for (DwThemeDomain domain : records) {
            DwThemeDomainListItemDTO dwThemeDomainListItemDTO = new DwThemeDomainListItemDTO();/*= this.dwThemeDomainModelMapper.toListItem(domain)*/;
            BeanUtils.copyProperties(domain, dwThemeDomainListItemDTO);
            list.add(dwThemeDomainListItemDTO);
        }

        PageInfo<DwThemeDomainListItemDTO> __page = new PageInfo<>(list, _page.getCurrent(), _page.getSize(), _page.getTotal());

        return Message.ok().data("page", __page);
    }

    @Transactional
    @Override
    public Message create(HttpServletRequest request, DwThemeDomainCreateCommand command) throws DwException {
        String name = command.getName();
        String enName = command.getEnName();
        String principalName = command.getPrincipalName();
        String owner = command.getOwner();
        Integer sort = command.getSort();
        String description = command.getDescription();
//        String authority = command.getAuthority();

        name = PreconditionUtil.checkStringArgumentNotBlankTrim(name, DwException.argumentReject("name should not empty"));
//        authority = PreconditionUtil.checkStringArgumentNotBlankTrim(authority, DwException.argumentReject("authority should not empty"));
        enName = PreconditionUtil.checkStringArgumentNotBlankTrim(enName, DwException.argumentReject("en name should not empty"));
        owner = PreconditionUtil.checkStringArgumentNotBlankTrim(owner, DwException.argumentReject("owner should not empty"));
//        authority = PreconditionUtil.checkStringArgumentNotBlankTrim(authority, DwException.argumentReject("authority should not empty"));

        if (Objects.isNull(sort)) {
            sort = 1;
        }

//        if (Strings.isBlank(availableRoles)) {
//            availableRoles = "ALL";
//        }

//        String user = "hdfs";

        Date now = new Date();

        DwThemeDomain record = new DwThemeDomain();
        record.setName(name);
        record.setEnName(enName);
        record.setDescription(description);
        record.setPrincipalName(principalName);
//        record.setAvailableRoles(availableRoles);
        record.setOwner(owner);
        record.setIsAvailable(Boolean.TRUE);
//        record.setCreateUser(user);
        record.setSort(sort);
        record.setCreateTime(now);
        record.setUpdateTime(now);
        record.setStatus(Boolean.TRUE);
        record.setLockVersion(1L);

        int insert = this.dwThemeDomainMapper.insert(record);
        PreconditionUtil.checkState(1 == insert, DwException.stateReject("create dw theme domain failed"));

        return Message.ok().data("id", record.getId());
    }

    @Override
    public Message getById(HttpServletRequest request, Long id) throws DwException {
        PreconditionUtil.checkArgument(!Objects.isNull(id), DwException.argumentReject("id should not be null"));

        DwThemeDomain record = this.dwThemeDomainMapper.selectById(id);
        PreconditionUtil.checkState(!Objects.isNull(record), DwException.stateReject("theme domain not found"));
        PreconditionUtil.checkState(record.getStatus(), DwException.stateReject("theme domain has been removed"));

//        DwThemeDomainDTO dto = this.dwThemeDomainModelMapper.toDTO(record);

        DwThemeDomainDTO dto = new DwThemeDomainDTO();
        BeanUtils.copyProperties(record, dto);

        return Message.ok().data("item", dto);
    }

    // TODO 后面需要检查主题域下有没有关联的 Hive 表，才决定是否删除
    @Transactional
    @Override
    public Message deleteById(HttpServletRequest request, Long id) throws DwException {
        PreconditionUtil.checkArgument(!Objects.isNull(id), DwException.argumentReject("id should not be null"));
        DwThemeDomain record = this.dwThemeDomainMapper.selectById(id);
        PreconditionUtil.checkState(!Objects.isNull(record), DwException.stateReject("theme domain not found"));
        if (Objects.equals(Boolean.FALSE, record.getStatus())) {
            return Message.ok();
        }
        record.setStatus(Boolean.FALSE);
        int i = this.dwThemeDomainMapper.updateById(record);
        PreconditionUtil.checkState(1 == i, DwException.stateReject("remove action failed"));
        return Message.ok();
    }

    @Transactional
    @Override
    public Message update(HttpServletRequest request, DwThemeDomainUpdateCommand command) throws DwException {
        // 基本参数校验
        Long id = command.getId();
        String name = command.getName();
        String enName = command.getEnName();
        String owner = command.getOwner();
        Integer sort = command.getSort();
        String principalName = command.getPrincipalName();
//        String authority = command.getAuthority();
        String description = command.getDescription();
        PreconditionUtil.checkArgument(!Objects.isNull(id), DwException.argumentReject("id should not be null"));
        name = PreconditionUtil.checkStringArgumentNotBlankTrim(name, DwException.argumentReject("name should not empty"));
//        authority = PreconditionUtil.checkStringArgumentNotBlankTrim(authority, DwException.argumentReject("authority should not empty"));
        enName = PreconditionUtil.checkStringArgumentNotBlankTrim(enName, DwException.argumentReject("en name should not empty"));
        owner = PreconditionUtil.checkStringArgumentNotBlankTrim(owner, DwException.argumentReject("owner should not empty"));
//        authority = PreconditionUtil.checkStringArgumentNotBlankTrim(authority, DwException.argumentReject("authority should not empty"));

//        if (Strings.isBlank(principalName)) {
//            principalName = "ALL";
//        }

        if (Objects.isNull(sort)) {
            sort = 1;
        }

        // 实体校验
        DwThemeDomain record = this.dwThemeDomainMapper.selectById(id);
        PreconditionUtil.checkState(!Objects.isNull(record), DwException.stateReject("theme domain not found"));

        // name 唯一性检测
        QueryWrapper<DwThemeDomain> nameUniqueCheckQuery = new QueryWrapper<>();
        nameUniqueCheckQuery.eq("name", name);
        nameUniqueCheckQuery.ne("id", id);
        nameUniqueCheckQuery.eq("status", Boolean.TRUE);
        DwThemeDomain exist = this.dwThemeDomainMapper.selectOne(nameUniqueCheckQuery);
        PreconditionUtil.checkState(Objects.isNull(exist), DwException.stateReject("theme domain name aleardy exists"));

        // name alias 唯一性检测
        QueryWrapper<DwThemeDomain> nameAliasUniqueCheckQuery = new QueryWrapper<>();
        nameAliasUniqueCheckQuery.eq("en_name", enName);
        nameAliasUniqueCheckQuery.ne("id", id);
        nameAliasUniqueCheckQuery.eq("status", Boolean.TRUE);
        DwThemeDomain nameAliasExist = this.dwThemeDomainMapper.selectOne(nameUniqueCheckQuery);
        PreconditionUtil.checkState(Objects.isNull(nameAliasExist), DwException.stateReject("theme domain en name aleardy exists"));

//        String user = "hdfs";

        Long oldVersion = record.getLockVersion();

        Date now = new Date();
        record.setName(name);
        record.setEnName(enName);
        record.setDescription(description);
//        record.setPrincipalName(authority);
        record.setPrincipalName(principalName);
        record.setOwner(owner);
        record.setSort(sort);
//        record.setModifyUser(user);
        record.setUpdateTime(now);
        record.setLockVersion(oldVersion + 1);

        UpdateWrapper<DwThemeDomain> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", record.getId());
        updateWrapper.eq("lock_version", oldVersion);

        int i = this.dwThemeDomainMapper.update(record, updateWrapper);

        PreconditionUtil.checkState(1 == i, DwException.stateReject("update theme domain failed"));

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

        DwThemeDomain record = this.dwThemeDomainMapper.selectById(id);
        PreconditionUtil.checkState(!Objects.isNull(record), DwException.stateReject("theme domain not found"));
        PreconditionUtil.checkState(record.getStatus(), DwException.stateReject("theme domain has been removed"));
        if (Objects.equals(enabled, record.getIsAvailable())) {
            return;
        }

//        String user = "hdfs";

        Long oldVersion = record.getLockVersion();
//        record.setModifyUser(user);
        record.setUpdateTime(new Date());
        record.setIsAvailable(enabled);
        record.setLockVersion(oldVersion + 1);
        UpdateWrapper<DwThemeDomain> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", record.getId());
        updateWrapper.eq("lock_version", oldVersion);
        int update = this.dwThemeDomainMapper.update(record, updateWrapper);
        PreconditionUtil.checkState(1 == update, DwException.stateReject(enabled ? "enable" : "disable" +  " failed"));
    }

}
