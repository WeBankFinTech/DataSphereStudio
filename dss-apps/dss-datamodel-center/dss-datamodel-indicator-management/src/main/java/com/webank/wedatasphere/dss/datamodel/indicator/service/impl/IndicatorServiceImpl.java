package com.webank.wedatasphere.dss.datamodel.indicator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.ErrorCode;
import com.webank.wedatasphere.dss.datamodel.center.common.exception.DSSDatamodelCenterException;
import com.webank.wedatasphere.dss.datamodel.indicator.dao.DssDatamodelIndicatorMapper;
import com.webank.wedatasphere.dss.datamodel.indicator.dao.IndicatorQueryMapper;
import com.webank.wedatasphere.dss.datamodel.indicator.dto.IndicatorContentQueryDTO;
import com.webank.wedatasphere.dss.datamodel.indicator.dto.IndicatorQueryDTO;
import com.webank.wedatasphere.dss.datamodel.indicator.dto.IndicatorVersionDTO;
import com.webank.wedatasphere.dss.datamodel.indicator.dto.IndicatorVersionQueryDTO;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicator;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicatorContent;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicatorQuery;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicatorVersion;
import com.webank.wedatasphere.dss.datamodel.indicator.restful.IndicatorRestfulApi;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorContentService;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorService;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorVersionService;
import com.webank.wedatasphere.dss.datamodel.indicator.vo.*;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.server.Message;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author helong
 * @date 2021/9/16
 */
@Service
public class IndicatorServiceImpl extends ServiceImpl<DssDatamodelIndicatorMapper, DssDatamodelIndicator> implements IndicatorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorRestfulApi.class);

    private final Gson gson = new Gson();

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private IndicatorContentService indicatorContentService;

    @Resource
    private IndicatorVersionService indicatorVersionService;

    @Resource
    private IndicatorQueryMapper indicatorQueryMapper;

    @Override
    @Transactional
    public int addIndicator(IndicatorAddVO vo, String version) throws ErrorException {
        DssDatamodelIndicator newOne = modelMapper.map(vo, DssDatamodelIndicator.class);
        //基本信息中设置版本号
        newOne.setVersion(version);
        if (getBaseMapper().selectCount(Wrappers.<DssDatamodelIndicator>lambdaQuery().eq(DssDatamodelIndicator::getName, vo.getName())) > 0) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_ADD_ERROR.getCode(), "indicator name can not repeat");
        }
        if (!save(newOne)) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_ADD_ERROR.getCode(), "add indicator error");
        }
        return indicatorContentService.addIndicatorContent(newOne.getId(), version, vo.getContent());
    }


    @Override
    @Transactional
    public int updateIndicator(Long id, IndicatorUpdateVO vo) throws ErrorException {
        DssDatamodelIndicator org = getBaseMapper().selectById(id);
        if (org == null) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_UPDATE_ERROR.getCode(), "update indicator error not exists");
        }


        //当更新指标名称时,存在其他指标名称同名或者当前指标名称已经存在版本信息，则不允许修改指标名称
        if (!StringUtils.equals(vo.getName(), org.getName())) {
            int repeat = getBaseMapper().selectCount(Wrappers.<DssDatamodelIndicator>lambdaQuery().eq(DssDatamodelIndicator::getName, vo.getName()));
            String lastVersion = indicatorVersionService.findLastVersion(org.getName());
            if (repeat > 0 || StringUtils.isNotBlank(lastVersion)) {
                throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_UPDATE_ERROR.getCode(), "indicator name can not repeat");
            }
        }


        Long orgId = org.getId();
        String version = org.getVersion();

        //更新原有指标
        DssDatamodelIndicator updateOne = modelMapper.map(vo, DssDatamodelIndicator.class);
        updateOne.setUpdateTime(new Date());
        int result = getBaseMapper().update(updateOne, Wrappers.<DssDatamodelIndicator>lambdaUpdate().eq(DssDatamodelIndicator::getId, id));
        if (result != 1) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_UPDATE_ERROR.getCode(), "update indicator error not exists");
        }

        return indicatorContentService.updateIndicatorContent(orgId, version, vo.getContent());
    }


    @Override
    public int deleteIndicator(Long id) throws ErrorException {
        return 0;
    }


    @Override
    public Message listIndicators(IndicatorQueryVO vo) {
        QueryWrapper<DssDatamodelIndicatorQuery> queryWrapper = new QueryWrapper<DssDatamodelIndicatorQuery>()
                .like(StringUtils.isNotBlank(vo.getName()), "name", vo.getName())
                .eq(vo.getIsAvailable() != null, "is_available", vo.getIsAvailable())
                .like(StringUtils.isNotBlank(vo.getOwner()), "owner", vo.getOwner());
        IPage<DssDatamodelIndicatorQuery> iPage = indicatorQueryMapper.page(new Page<>(vo.getPageNum(), vo.getPageSize()), queryWrapper);

        return Message.ok()
                .data("list", iPage.getRecords())
                .data("total", iPage.getTotal());
    }


    @Override
    public Message queryById(Long id) throws DSSDatamodelCenterException {
        DssDatamodelIndicator indicator = baseMapper.selectById(id);
        if (indicator == null) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_QUERY_ERROR.getCode(), "indicator id " + id + " not exists");
        }

        DssDatamodelIndicatorContent content = indicatorContentService.queryByIndicateId(id);
        if (content == null) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_QUERY_ERROR.getCode(), "indicator content id " + id + " not exists");
        }

        IndicatorQueryDTO indicatorQueryDTO = modelMapper.map(indicator, IndicatorQueryDTO.class);
        IndicatorContentQueryDTO indicatorContentQueryDTO = modelMapper.map(content, IndicatorContentQueryDTO.class);
        indicatorQueryDTO.setContent(indicatorContentQueryDTO);
        return Message.ok().data("detail", indicatorQueryDTO);
    }


    @Override
    @Transactional
    public int enableIndicator(Long id, IndicatorEnableVO vo) {
        DssDatamodelIndicator updateOne = modelMapper.map(vo, DssDatamodelIndicator.class);
        return getBaseMapper().update(updateOne, Wrappers.<DssDatamodelIndicator>lambdaUpdate().eq(DssDatamodelIndicator::getId, id));
    }


    @Override
    @Transactional
    public int addIndicatorVersion(Long id, IndicatorVersionAddVO vo) throws ErrorException {

        //判断旧版本指标是否存在
        DssDatamodelIndicator orgVersion = getBaseMapper().selectById(id);
        if (orgVersion == null) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_VERSION_ADD_ERROR.getCode(), "indicator  id " + id + " not exists");
        }
        doIndicatorVersionBackUp(id, null, vo, orgVersion);

        return 1;
    }

    private void doIndicatorVersionBackUp(Long id, String assignVersion, IndicatorVersionAddVO vo, DssDatamodelIndicator orgVersion) throws ErrorException {
        String orgName = orgVersion.getName();
        String orgV = orgVersion.getVersion();
        //指标名称不能改变
        if (!StringUtils.equals(orgName, vo.getName())) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_VERSION_ADD_ERROR.getCode(), "indicator name must be same");
        }
        //查询指标详细信息
        DssDatamodelIndicatorContent orgContent = indicatorContentService.queryByIndicateId(id);
        if (orgContent == null) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_VERSION_ADD_ERROR.getCode(), "indicator content id " + id + " not exists");
        }

        //旧版本备份
        indicatorVersionService.addOlderVersion(orgName, orgVersion.getPrincipalName(),orgV, orgVersion.getComment(), IndicatorVersionDTO.of(orgVersion, orgContent));

        //删除旧版本
        getBaseMapper().deleteById(id);

        //填充新增版本
        DssDatamodelIndicator newOne = modelMapper.map(vo, DssDatamodelIndicator.class);
        //基本信息中设置版本号,如果新增时未指定版本号则生成新版本号
        String newVersion = StringUtils.isNotBlank(assignVersion) ? assignVersion : newVersion(orgName, orgV);
        newOne.setVersion(newVersion);
        //保存新版本
        if (save(newOne)) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_VERSION_ADD_ERROR.getCode(), "save new version indicator");

        }
        //更新指标详细信息
        indicatorContentService.updateIndicatorContent(newOne.getId(), newVersion, vo.getContent());
    }


    private String newVersion(String orgName, String orgVersion) {
        String lastVersion = indicatorVersionService.findLastVersion(orgName);
        return StringUtils.isBlank(lastVersion) ?
                Integer.parseInt(orgVersion) + 1 + "" : Integer.parseInt(lastVersion) + 1 + "";
    }


    @Override
    @Transactional
    public int versionRollBack(IndicatorVersionRollBackVO vo) throws ErrorException {

        String name = vo.getName();
        String version = vo.getVersion();

        //获取当前试用版本指标
        DssDatamodelIndicator current =
                getBaseMapper().selectOne(Wrappers.<DssDatamodelIndicator>lambdaQuery().eq(DssDatamodelIndicator::getName, name));
        //判断当前使用指标
        if (current == null) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_VERSION_ROLL_BACK_ERROR.getCode(), "current indicator not exists, name : " + name + " version : " +version);
        }
        //查询当前详细信息
        DssDatamodelIndicatorContent currentContent = indicatorContentService.queryByIndicateId(current.getId());
        if (currentContent == null) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_VERSION_ROLL_BACK_ERROR.getCode(), "current indicator content not exists, name : " + name + " version : " +version);
        }
        //当前版本备份
        indicatorVersionService.addOlderVersion(current.getName(), current.getPrincipalName(),current.getVersion(), current.getComment(), IndicatorVersionDTO.of(current, currentContent));
        //删除当前版本
        getBaseMapper().deleteById(current.getId());
        //删除当前版本详情
        indicatorContentService.deleteByIndicatorId(current.getId());

        //获取需要回退指标版本信息
        DssDatamodelIndicatorVersion rollBackVersion = indicatorVersionService.findBackup(name, version);
        if (rollBackVersion == null) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_VERSION_ROLL_BACK_ERROR.getCode(), "indicator name : " + name + " version : " + version + " not exist");
        }

        IndicatorVersionDTO rollBackDTO = gson.fromJson(rollBackVersion.getVersionContext(),IndicatorVersionDTO.class);
        DssDatamodelIndicator rollBackIndicator = rollBackDTO.getEssential();
        //重置id
        rollBackIndicator.setId(null);
        save(rollBackIndicator);


        DssDatamodelIndicatorContent rollBackContent = gson.fromJson(rollBackVersion.getVersionContext(),DssDatamodelIndicatorContent.class);
        //重置id
        rollBackContent.setId(null);
        //设置新指标id
        rollBackContent.setIndicatorId(rollBackIndicator.getId());
        indicatorContentService.save(rollBackContent);
        indicatorVersionService.getBaseMapper().deleteById(rollBackVersion.getId());
        return 1;
    }


    @Override
    public Message listIndicatorVersions(IndicatorVersionQueryVO vo) {
        List<IndicatorVersionQueryDTO> list = indicatorVersionService.getBaseMapper()
                .selectList(Wrappers.<DssDatamodelIndicatorVersion>lambdaQuery().eq(DssDatamodelIndicatorVersion::getName,vo.getName()))
                .stream()
                .map(dssDatamodelIndicatorVersion -> modelMapper.map(dssDatamodelIndicatorVersion,IndicatorVersionQueryDTO.class))
                .collect(Collectors.toList());
        return Message.ok().data("list",list);
    }
}
