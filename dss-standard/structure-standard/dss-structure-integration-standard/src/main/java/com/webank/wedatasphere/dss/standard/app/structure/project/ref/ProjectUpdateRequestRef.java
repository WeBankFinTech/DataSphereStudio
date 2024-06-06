package com.webank.wedatasphere.dss.standard.app.structure.project.ref;

import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRefImpl;

import java.util.Collections;
import java.util.List;

/**
 * @author enjoyyin
 * @date 2022-03-13
 * @since 1.1.0
 */
public interface ProjectUpdateRequestRef<R extends ProjectUpdateRequestRef<R>>
        extends DSSProjectContentRequestRef<R>, RefProjectContentRequestRef<R> {

    @Override
    default String getProjectName() {
        return getDSSProject().getName();
    }

    @Override
    default R setProjectName(String projectName) {
        getDSSProject().setName(projectName);
        return (R) this;
    }

    /**
     * 只包含本次新增的 DSS 工程相关权限用户
     * @return
     */
    default DSSProjectPrivilege getAddedDSSProjectPrivilege() {
        return (DSSProjectPrivilege) this.getParameter("addedDSSProjectPrivilege");
    }

    default R setAddedDSSProjectPrivilege(DSSProjectPrivilege addedDSSProjectPrivilege) {
        setParameter("addedDSSProjectPrivilege", addedDSSProjectPrivilege);
        return (R) this;
    }

    /**
     * 只包含本次移除的 DSS 工程相关权限用户
     * @return
     */
    default DSSProjectPrivilege getRemovedDSSProjectPrivilege() {
        return (DSSProjectPrivilege) this.getParameter("removedDSSProjectPrivilege");
    }

    default R setRemovedDSSProjectPrivilege(DSSProjectPrivilege removedDSSProjectPrivilege) {
        setParameter("removedDSSProjectPrivilege", removedDSSProjectPrivilege);
        return (R) this;
    }
    /**
     * DSS 工程的全量最新权数据源列表，包含了 DSS 工程所有的数据源
     * 第三方组件需要根据这个列表来判断哪些数据源是新增的，哪些数据源是删除的，时刻保持同步
     * @return DSSProjectPrivilege
     */
    default List<DSSProjectDataSource> getDSSProjectDataSources() {
        String json = (String) this.getParameter("dssProjectDataSources");
        if(json != null) {
            return   DSSCommonUtils.COMMON_GSON.fromJson(json, new TypeToken<List<DSSProjectDataSource>>(){}.getType());
        }
        return Collections.emptyList();
    }


    default R setDSSProjectDataSources(List<DSSProjectDataSource> dssProjectDataSources) {
        //为了让第三方组件可以不用升级dss依赖包，转成json string再存储
        String json = DSSCommonUtils.COMMON_GSON.toJson(dssProjectDataSources);
        setParameter("dssProjectDataSources", json);
        return (R) this;
    }
    class ProjectUpdateRequestRefImpl extends StructureRequestRefImpl<ProjectUpdateRequestRefImpl>
            implements ProjectUpdateRequestRef<ProjectUpdateRequestRefImpl> {}

}
