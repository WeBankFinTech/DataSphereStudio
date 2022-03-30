package com.webank.wedatasphere.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webank.wedatasphere.dss.data.governance.entity.ClassificationConstant;
import com.webank.wedatasphere.dss.data.governance.impl.LinkisDataAssetsRemoteClient;
import com.webank.wedatasphere.dss.data.governance.request.CreateModelTypeAction;
import com.webank.wedatasphere.dss.data.governance.request.DeleteModelTypeAction;
import com.webank.wedatasphere.dss.data.governance.request.UpdateModelTypeAction;
import com.webank.wedatasphere.dss.data.governance.response.CreateModelTypeResult;
import com.webank.wedatasphere.dss.data.governance.response.DeleteModelTypeResult;
import com.webank.wedatasphere.dss.data.governance.response.UpdateModelTypeResult;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import com.webank.wedatasphere.warehouse.LinkisRemoteClientHolder;
import com.webank.wedatasphere.warehouse.cqe.DwLayerCreateCommand;
import com.webank.wedatasphere.warehouse.cqe.DwLayerQueryCommand;
import com.webank.wedatasphere.warehouse.cqe.DwLayerUpdateCommand;
import com.webank.wedatasphere.warehouse.dao.domain.DwLayer;
import com.webank.wedatasphere.warehouse.dao.mapper.DwModifierMapper;
import com.webank.wedatasphere.warehouse.dao.mapper.DwStatisticalPeriodMapper;
import com.webank.wedatasphere.warehouse.dao.mapper.DwThemeDomainMapper;
import com.webank.wedatasphere.warehouse.dto.DwLayerDTO;
import com.webank.wedatasphere.warehouse.dto.DwLayerListItemDTO;
import com.webank.wedatasphere.warehouse.dto.PageInfo;
import com.webank.wedatasphere.warehouse.exception.DwException;
import com.webank.wedatasphere.warehouse.dao.mapper.DwLayerMapper;
import com.webank.wedatasphere.warehouse.exception.DwExceptionCode;
import com.webank.wedatasphere.warehouse.service.DwDomainReferenceAdapter;
import com.webank.wedatasphere.warehouse.service.DwLayerService;
import com.webank.wedatasphere.warehouse.utils.PreconditionUtil;
import com.webank.wedatasphere.warehouse.utils.RegexUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class DwLayerServiceImpl implements DwLayerService, DwDomainReferenceAdapter {

    private final DwLayerMapper dwLayerMapper;
    private final DwThemeDomainMapper dwThemeDomainMapper;
    private final DwModifierMapper dwModifierMapper;
    private final DwStatisticalPeriodMapper dwStatisticalPeriodMapper;

    @Autowired
    public DwLayerServiceImpl(final DwLayerMapper dwLayerMapper, final DwThemeDomainMapper dwThemeDomainMapper, final DwModifierMapper dwModifierMapper, final DwStatisticalPeriodMapper dwStatisticalPeriodMapper) {
        this.dwLayerMapper = dwLayerMapper;
        this.dwThemeDomainMapper = dwThemeDomainMapper;
        this.dwModifierMapper = dwModifierMapper;
        this.dwStatisticalPeriodMapper = dwStatisticalPeriodMapper;
    }

    @Override
    public DwModifierMapper getDwModifierMapper() {
        return dwModifierMapper;
    }

    @Override
    public DwStatisticalPeriodMapper getDwStatisticalPeriodMapper() {
        return dwStatisticalPeriodMapper;
    }

    @Override
    public DwLayerMapper getDwLayerMapper() {
        return dwLayerMapper;
    }

    @Override
    public DwThemeDomainMapper getDwThemeDomainMapper() {
        return dwThemeDomainMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Message createDwCustomLayer(HttpServletRequest request, DwLayerCreateCommand command) throws DwException {
        String username = SecurityFilter.getLoginUsername(request);
//        String username = "hdfs";
        String name = command.getName();
        String enName = command.getEnName();
        String databases = command.getDatabases();
        Integer order = command.getOrder();
//        String autoCollectStrategy = command.getAutoCollectStrategy();
        String owner = command.getOwner();
        String description = command.getDescription();
        String principalName = command.getPrincipalName();

        name = PreconditionUtil.checkStringArgumentNotBlankTrim(name, DwException.argumentReject("name should not empty"));
        PreconditionUtil.checkArgument(RegexUtil.checkCnName(name), DwException.argumentReject("name must be digitg, chinese and underline"));
        enName = PreconditionUtil.checkStringArgumentNotBlankTrim(enName, DwException.argumentReject("name alias should not empty"));
        PreconditionUtil.checkArgument(RegexUtil.checkEnName(enName), DwException.argumentReject("name must be digit, alpha and underline"));
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

        // 建立关联
        try {
            LinkisDataAssetsRemoteClient dataAssetsRemoteClient = LinkisRemoteClientHolder.getDataAssetsRemoteClient();
            CreateModelTypeAction action = new CreateModelTypeAction.Builder().setType(ClassificationConstant.LAYER).setName(enName).setUser(username).build();
            CreateModelTypeResult result = dataAssetsRemoteClient.createModelType(action);

            if (result.getStatus() != 0) {
                throw new DwException(result.getStatus(), result.getMessage());
            }
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new DwException(DwExceptionCode.CREATE_MODEL_TYPE_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new DwException(DwExceptionCode.CREATE_MODEL_TYPE_ERROR.getCode(), e.getMessage());
            }
        }


        return Message.ok().data("id", layer.getId());
    }

    @Override
    public Message getLayerById(HttpServletRequest request, Long id) throws DwException {
        PreconditionUtil.checkArgument(!Objects.isNull(id), DwException.argumentReject("id should not be null"));

        DwLayer layer = this.dwLayerMapper.selectById(id);
        PreconditionUtil.checkState(!Objects.isNull(layer), DwException.stateReject("layer not found"));
        PreconditionUtil.checkState(layer.getStatus(), DwException.stateReject("layer has been removed"));

//        DwLayerDTO dto = this.dwLayerModelMapper.toDTO(layer);
        String username = SecurityFilter.getLoginUsername(request);
        boolean inUse = isLayerInUse(layer.getId(), username);

        DwLayerDTO dto = new DwLayerDTO();
        BeanUtils.copyProperties(layer, dto);
        dto.setReferenced(inUse);

        return Message.ok().data("item", dto);
    }


    @Override
    public Message getAllLayers(HttpServletRequest request, Boolean isAvailable, String db) throws DwException {
        QueryWrapper<DwLayer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", Boolean.TRUE);
        if (!Objects.isNull(isAvailable)) {
            queryWrapper.eq("is_available", isAvailable);
        }
        List<DwLayer> records = this.dwLayerMapper.selectList(queryWrapper);
        String username = SecurityFilter.getLoginUsername(request);

        List<DwLayerListItemDTO> dtos = new ArrayList<>();
        boolean contain;
        for (DwLayer record : records) {
            contain = true;
            if (Strings.isNotBlank(db)) {
                if ("all".equalsIgnoreCase(db)) {
                    // 查询的时候传入的参数 db = all
                    contain = true;
                } else {
                    // 预置分层所有库都可以用
                    if (record.getPreset()) {
                        contain = true;
                    } else {
                        contain = false;
                        String dbs = record.getDbs();
                        if (Strings.isNotBlank(dbs)) {
                            String[] splitDbs = dbs.split(",");
                            for (String splitDb : splitDbs) {
                                // 如果分层可用库选择了所有，则直接添加到查询记录中
                                if ("all".equalsIgnoreCase(splitDb)) {
                                    contain = true;
                                    break;
                                }
                                // 否则就遍历判断当前查询库是否在当前分层中存在
                                if (Objects.equals(splitDb, db)) {
                                    contain = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (!contain) {
                continue;
            }

            DwLayerListItemDTO dto = new DwLayerListItemDTO();
            BeanUtils.copyProperties(record, dto);
            // get reference count
            int layerReferenceCount = getLayerReferenceCount(record.getId(), username);
            dto.setReferenceCount(layerReferenceCount);
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

        String username = SecurityFilter.getLoginUsername(request);

        for (DwLayer record : records) {
            DwLayerListItemDTO dto = new DwLayerListItemDTO();
            BeanUtils.copyProperties(record, dto);
            // get reference count
            int layerReferenceCount = getLayerReferenceCount(record.getId(), username);
            dto.setReferenceCount(layerReferenceCount);
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
        if (!Objects.isNull(enabled)) {
            queryWrapper.eq("is_available", enabled);
        }
        if (Strings.isNotBlank(name)) {
            queryWrapper.like("name", name).or().like("en_name", name);
        }

        Page<DwLayer> queryPage = new Page<>(page, size);

        IPage<DwLayer> _page = this.dwLayerMapper.selectPage(queryPage, queryWrapper);

        List<DwLayer> records = _page.getRecords();

//        List<DwLayerListItemDTO> list = DwLayerModelMapper.INSTANCE.toList(records);
        List<DwLayerListItemDTO> list = new ArrayList<>();
        String username = SecurityFilter.getLoginUsername(request);
        for (DwLayer record : records) {
            DwLayerListItemDTO dto = new DwLayerListItemDTO();
            BeanUtils.copyProperties(record, dto);
            // get reference count
            int layerReferenceCount = getLayerReferenceCount(record.getId(), username);
            dto.setReferenceCount(layerReferenceCount);

            list.add(dto);

        }


        PageInfo<DwLayerListItemDTO> __page = new PageInfo<>(list, _page.getCurrent(), _page.getSize(), _page.getTotal());

        return Message.ok().data("page", __page);
    }

    // TODO 后面需要检查主题域下有没有关联的 Hive 表，才决定是否删除
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Message deleteById(HttpServletRequest request, Long id) throws DwException {
        PreconditionUtil.checkArgument(!Objects.isNull(id), DwException.argumentReject("id should not be null"));
        DwLayer layer = this.dwLayerMapper.selectById(id);
        PreconditionUtil.checkState(!Objects.isNull(layer), DwException.stateReject("layer not found"));
        PreconditionUtil.checkState(Objects.equals(Boolean.FALSE, layer.getPreset()), DwException.stateReject("preset layer could not be removed"));

        String username = SecurityFilter.getLoginUsername(request);
        // check in use
        // statistical_period & modifier & data_model client api check
        boolean inUse = isLayerInUse(layer.getId(), username);
        PreconditionUtil.checkState(!inUse, DwException.stateReject("layer is in use"));

//        if (Objects.equals(Boolean.FALSE, layer.getStatus())) {
//            return Message.ok();
//        }
//        layer.setStatus(Boolean.FALSE);
//        int i = this.dwLayerMapper.updateById(layer);
        int i = this.dwLayerMapper.deleteById(layer);
        PreconditionUtil.checkState(1 == i, DwException.stateReject("remove action failed"));

        // 删除关联
        try {
            LinkisDataAssetsRemoteClient dataAssetsRemoteClient = LinkisRemoteClientHolder.getDataAssetsRemoteClient();
            DeleteModelTypeAction action = new DeleteModelTypeAction.Builder().setType(ClassificationConstant.LAYER).setName(layer.getEnName()).setUser(username).build();
            DeleteModelTypeResult result = dataAssetsRemoteClient.deleteModelType(action);

            if (result.getStatus() != 0) {
                throw new DwException(result.getStatus(), result.getMessage());
            }
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new DwException(DwExceptionCode.DELETE_MODEL_TYPE_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new DwException(DwExceptionCode.DELETE_MODEL_TYPE_ERROR.getCode(), e.getMessage());
            }
        }

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
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Message update(HttpServletRequest request, DwLayerUpdateCommand command) throws DwException {
        String username = SecurityFilter.getLoginUsername(request);
        // 基本参数校验
        Long id = command.getId();
        PreconditionUtil.checkArgument(!Objects.isNull(id), DwException.argumentReject("id should not be null"));

        // 实体校验
        DwLayer layer = this.dwLayerMapper.selectById(id);
        PreconditionUtil.checkState(!Objects.isNull(layer), DwException.stateReject("layer not found"));
        String orgEnName = layer.getEnName();

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
            PreconditionUtil.checkArgument(RegexUtil.checkCnName(name), DwException.argumentReject("name must be digitg, chinese and underline"));
            enName = PreconditionUtil.checkStringArgumentNotBlankTrim(enName, DwException.argumentReject("name alias should not empty"));
            PreconditionUtil.checkArgument(RegexUtil.checkEnName(enName), DwException.argumentReject("name must be digit, alpha and underline"));

            if (!Objects.equals(name, layer.getName()) || !Objects.equals(enName, layer.getEnName())) {
                // check name enName if not equal, we should check if in use
                // check in use
                // statistical_period & modifier & data_model client api check

                boolean inUse = isLayerInUse(layer.getId(), username);
                PreconditionUtil.checkState(!inUse, DwException.stateReject("layer is in use"));
            }

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

        // 更新关联
        if (!Objects.equals(orgEnName, layer.getEnName())) {
            try {
                LinkisDataAssetsRemoteClient dataAssetsRemoteClient = LinkisRemoteClientHolder.getDataAssetsRemoteClient();
                UpdateModelTypeAction action = new UpdateModelTypeAction.Builder().setType(ClassificationConstant.LAYER).setName(layer.getEnName()).setOrgName(orgEnName).setUser(username).build();
                UpdateModelTypeResult result = dataAssetsRemoteClient.updateModelType(action);

                if (result.getStatus() != 0) {
                    throw new DwException(result.getStatus(), result.getMessage());
                }
            } catch (Exception e) {
                if (e instanceof ErrorException) {
                    ErrorException ee = (ErrorException) e;
                    throw new DwException(DwExceptionCode.UPDATE_MODEL_TYPE_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
                } else {
                    throw new DwException(DwExceptionCode.UPDATE_MODEL_TYPE_ERROR.getCode(), e.getMessage());
                }
            }
        }

        return Message.ok();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Message enable(HttpServletRequest request, Long id) throws DwException {
        changeEnable(request, id, Boolean.TRUE);
        return Message.ok();
    }

    @Transactional(rollbackFor = Exception.class)
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
