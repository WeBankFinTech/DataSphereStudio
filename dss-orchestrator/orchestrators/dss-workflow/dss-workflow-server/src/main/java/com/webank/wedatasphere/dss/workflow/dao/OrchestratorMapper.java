package com.webank.wedatasphere.dss.workflow.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorReleaseInfo;

@Mapper
public interface OrchestratorMapper {

    void insert(OrchestratorReleaseInfo orchestratorReleaseInfo);

    @Select("SELECT * FROM dss_orchestrator_release_info WHERE orchestrator_id = #{orchestratorId}")
    OrchestratorReleaseInfo getByOrchestratorId(Long orchestratorId);

    void update(OrchestratorReleaseInfo orchestratorReleaseInfo);

    @Delete("DELETE FROM dss_orchestrator_release_info WHERE id = #{id}")
    int removeById(Long id);

    @Delete("DELETE FROM dss_orchestrator_release_info WHERE orchestrator_id = #{orchestratorId}")
    int removeByOrchestratorId(Long orchestratorId);

    @Select("SELECT * FROM dss_orchestrator_version_info WHERE id = #{id}")
    DSSOrchestratorVersion getOrchestratorVersionById(Long id);

    @Select("SELECT * FROM dss_orchestrator_version_info WHERE app_id = #{appId}")
    DSSOrchestratorVersion getOrchestratorVersionByAppId(Long appId);

    @Select("SELECT project_id FROM dss_orchestrator_info WHERE id = #{orchestratorId}")
    Long getProjectId(Long orchestratorId);
}
